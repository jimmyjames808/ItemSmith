package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import mastrjimbo.itemsmith.util.Visuals;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * A sustained grappling-hook pull: plants a hook at the target's location and hauls the caster
 * to it over several ticks, holding the rope and the hook on screen for the whole trip.
 *
 * <p>Works both ways depending on what was hooked. Hook the world and the caster is reeled to it;
 * hook a living entity and that entity is reeled to the caster instead. Pair it with the
 * {@code looking_at} targeter and one button covers both.
 *
 * <p>Distinct from {@code pull_self}, which is a single velocity impulse and draws nothing. This
 * one keeps state: it re-applies the pull every tick (so gravity and collisions can't stall the
 * flight), redraws the rope from the caster's <em>current</em> position, and only releases on
 * arrival.
 */
public final class GrappleAction implements Action {

    public static final String ID = "grapple";

    /** Minecraft clamps velocity components to ±3.9 blocks/tick on the wire. */
    private static final double MAX_COMPONENT = 3.9;

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("strength", ParamType.DOUBLE, 0.9)
                    .label("Strength").min(0).desc("Pull speed per tick. Capped near 3.9."))
            .add(ParamDef.of("launch_speed", ParamType.DOUBLE, 2.0)
                    .label("Launch speed").min(0.1)
                    .desc("Blocks per tick the hook flies out before the reel begins. The rope pays "
                            + "out behind it, so you see the hook travel rather than teleport."))
            .add(ParamDef.of("arc", ParamType.DOUBLE, 0.25)
                    .label("Arc").desc("Extra lift each tick, so you rise over the lip you hooked."))
            .add(ParamDef.of("arrive_distance", ParamType.DOUBLE, 2.5)
                    .label("Arrive distance").min(0.5)
                    .desc("Release once this close to the anchor."))
            .add(ParamDef.of("max_ticks", ParamType.INT, 100)
                    .label("Max ticks").min(1)
                    .desc("Give up after this long, so a blocked path can't trap you."))
            .add(ParamDef.of("tip_model", ParamType.STRING, "")
                    .label("Tip model").desc("Namespaced item-model shown as the hook at the anchor, "
                            + "e.g. itemsmith:grappling_hook_deployed. Blank uses the held item."))
            .add(ParamDef.of("particle", ParamType.PARTICLE, "crit")
                    .label("Rope particle").desc("Particle drawn along the rope each tick."))
            .add(ParamDef.of("points", ParamType.INT, 24)
                    .label("Rope points").min(2).desc("Particles drawn along the rope."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Grapple"; }
    @Override public String description() { return "Plants a hook and hauls the caster to it, rope and all."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        // An entity target flips the direction of the pull: the victim comes to us.
        Entity victim = Targets.entity(target);
        Location anchor = Targets.location(target);
        if (anchor == null) return;
        Player player = ctx.player();
        if (player == null) return;
        World world = anchor.getWorld();
        if (world == null || !world.equals(player.getWorld())) return;

        double strength = Math.max(0, params.getDouble("strength", 0.9));
        double launchSpeed = Math.max(0.1, params.getDouble("launch_speed", 2.0));
        double arc = params.getDouble("arc", 0.25);
        double arrive = Math.max(0.5, params.getDouble("arrive_distance", 2.5));
        int maxTicks = Math.max(1, params.getInt("max_ticks", 100));
        Particle rope = params.getParticle("particle");
        int points = Math.max(2, params.getInt("points", 24));

        // The hook that stays planted at the anchor for the whole trip.
        ItemStack tip = tipStack(ctx, params.getString("tip_model", ""));
        Location anchorPoint = anchor.clone().add(0.5, 0.5, 0.5); // centre of the hooked block
        anchorPoint.setYaw(0f);
        anchorPoint.setPitch(0f);

        // Spawned at the caster and flown out, so the hook visibly travels to the anchor.
        Location spawn = player.getEyeLocation().clone();
        spawn.setYaw(0f);
        spawn.setPitch(0f);
        ItemDisplay hook = world.spawn(spawn, ItemDisplay.class, d -> {
            d.setItemStack(tip);
            d.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE);
            d.setTeleportDuration(2); // client interpolates the flight instead of snapping
            d.setBillboard(Display.Billboard.FIXED);
            d.setBrightness(new Display.Brightness(15, 15));
            d.setPersistent(false);
        });

        new BukkitRunnable() {
            private int ticks = 0;
            private boolean launching = true;              // phase 1: hook flying out
            private final Location hookPos = spawn.clone();

            @Override
            public void run() {
                if (++ticks > maxTicks || !hook.isValid() || !player.isOnline() || player.isDead()) {
                    finish();
                    return;
                }

                // --- Phase 1: throw the hook. No pull yet, and only a light trail, so the
                // model itself is what you follow across the gap. ---
                if (launching) {
                    // Re-read a live target each tick so the hook tracks a mob that's moving.
                    if (victim != null && victim.isValid()) {
                        Location vloc = victim.getLocation();
                        anchorPoint.setX(vloc.getX());
                        anchorPoint.setY(vloc.getY() + 1.0);
                        anchorPoint.setZ(vloc.getZ());
                    }
                    Vector out = anchorPoint.toVector().subtract(hookPos.toVector());
                    if (out.length() <= launchSpeed) {
                        hookPos.setX(anchorPoint.getX());
                        hookPos.setY(anchorPoint.getY());
                        hookPos.setZ(anchorPoint.getZ());
                        launching = false; // bitten in — start reeling next tick
                    } else {
                        hookPos.add(out.normalize().multiply(launchSpeed));
                    }
                    hook.teleport(hookPos);
                    if (rope != null) {
                        Visuals.emit(ctx.plugin(), hookPos, rope, 1, 0, 0, 0, 0, null, "");
                    }
                    return;
                }

                // --- Phase 2: reel. Same rope, same tick loop; only the end that moves differs. ---
                Location eye = player.getEyeLocation();

                if (victim != null) {
                    // Hooked a creature: drag IT to US. The hook rides along on its back.
                    if (!victim.isValid() || victim.isDead()) {
                        finish();
                        return;
                    }
                    Location vloc = victim.getLocation();
                    Vector toCaster = eye.toVector().subtract(vloc.toVector());
                    if (toCaster.length() <= arrive) {
                        finish();
                        return;
                    }
                    Vector pullV = toCaster.clone().normalize().multiply(strength);
                    pullV.setY(pullV.getY() + arc);
                    victim.setVelocity(new Vector(clamp(pullV.getX()), clamp(pullV.getY()), clamp(pullV.getZ())));
                    hookPos.setX(vloc.getX());
                    hookPos.setY(vloc.getY() + 1.0);
                    hookPos.setZ(vloc.getZ());
                    hook.teleport(hookPos);
                    drawRope(ctx, eye, hookPos.toVector().subtract(eye.toVector()), rope, points);
                    return;
                }

                Vector delta = anchorPoint.toVector().subtract(eye.toVector());
                if (delta.length() <= arrive) { // arrived — let go
                    finish();
                    return;
                }

                // Re-applied every tick so gravity and glancing collisions can't stall the flight.
                Vector v = delta.clone().normalize().multiply(strength);
                v.setY(v.getY() + arc);
                player.setVelocity(new Vector(clamp(v.getX()), clamp(v.getY()), clamp(v.getZ())));

                // Rope redrawn from where the caster is NOW, so it tracks them in flight.
                drawRope(ctx, eye, delta, rope, points);
            }

            private void finish() {
                hook.remove();
                cancel();
            }
        }.runTaskTimer(ctx.plugin(), 1L, 1L);
    }

    /** Draws the rope as a line of particles from {@code from} along {@code delta}. */
    private static void drawRope(AbilityContext ctx, Location from, Vector delta, Particle rope, int points) {
        if (rope == null) return;
        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;
            Location p = from.clone().add(delta.clone().multiply(t));
            Visuals.emit(ctx.plugin(), p, rope, 1, 0, 0, 0, 0, null, "");
        }
    }

    /**
     * The stack rendered as the planted hook. A blank {@code tip_model} reuses the held item;
     * otherwise a bare stick wears the requested item-model, so any model in the pack can be the
     * hook without needing a separate ItemSmith item to exist for it.
     */
    private static ItemStack tipStack(AbilityContext ctx, String modelKey) {
        if (modelKey == null || modelKey.isBlank()) {
            ItemStack held = ctx.itemStack();
            ItemStack copy = held != null ? held.clone() : new ItemStack(Material.STICK);
            copy.setAmount(1);
            return copy;
        }
        ItemStack stack = new ItemStack(Material.STICK);
        NamespacedKey key = NamespacedKey.fromString(modelKey);
        if (key != null) {
            ItemMeta meta = stack.getItemMeta();
            if (meta != null) {
                meta.setItemModel(key);
                stack.setItemMeta(meta);
            }
        }
        return stack;
    }

    private static double clamp(double v) {
        return Math.max(-MAX_COMPONENT, Math.min(MAX_COMPONENT, v));
    }
}
