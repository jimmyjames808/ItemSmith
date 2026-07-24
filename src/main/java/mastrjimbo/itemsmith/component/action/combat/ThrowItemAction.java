package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Activators;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.ActionDamage;
import mastrjimbo.itemsmith.util.Visuals;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Locale;

/**
 * Hurls the caster's own item forward as a tumbling projectile, damages the first
 * living entity it meets, then flies back to the caster and vanishes — a boomerang
 * / Mjolnir throw.
 *
 * <p>Uses an {@link ItemDisplay} rather than a dropped item: it renders the stack
 * (so the item's {@code item-model} shows) and, crucially, supports
 * {@link Display#setTeleportDuration(int)} so the client <em>interpolates</em> between
 * per-tick moves. A dropped item teleported each tick visibly stutters; this does not.
 * The display also can't be picked up, merged, or despawned by the item rules.
 */
public final class ThrowItemAction implements Action {

    public static final String ID = "throw_item";

    /** Hard stop so a throw can never leak an entity if something goes strange. */
    private static final int MAX_TICKS = 200;
    /** Ticks the client smooths each hop over. Matches our 1-tick update rate, +1 for headroom. */
    private static final int TELEPORT_SMOOTHING = 2;

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("speed", ParamType.DOUBLE, 1.1)
                    .label("Speed").min(0.1).desc("Blocks travelled per tick on the way out."))
            .add(ParamDef.of("range", ParamType.DOUBLE, 18.0)
                    .label("Range").min(1).desc("How far it flies before turning back."))
            .add(ParamDef.of("damage", ParamType.DOUBLE, 8.0)
                    .label("Damage").min(0).desc("Damage dealt to the entity it strikes."))
            .add(ParamDef.of("hit_radius", ParamType.DOUBLE, 1.5)
                    .label("Hit radius").min(0.1).desc("How close it must pass to strike something."))
            .add(ParamDef.of("spin", ParamType.DOUBLE, 40.0)
                    .label("Spin").desc("Degrees the projectile tumbles per tick. 0 for no spin."))
            .add(ParamDef.of("face", ParamType.ENUM, "-y")
                    .label("Facing axis").options("+x", "-x", "+y", "-y", "+z", "-z")
                    .desc("Which axis of the model is pointed along the direction of travel. "
                            + "Depends on how the model was built; try -y first, then +y."))
            .add(ParamDef.of("pivot", ParamType.DOUBLE, 0.0)
                    .label("Pivot offset").desc("Blocks along the model's Y axis to its visual centre. "
                            + "Non-zero stops an off-origin model orbiting instead of spinning in place."))
            .add(ParamDef.of("particle", ParamType.PARTICLE, "electric_spark")
                    .label("Trail particle").desc("Particle trailed along the flight path."))
            .add(ParamDef.of("hit_sound", ParamType.SOUND, "")
                    .label("Hit sound").desc("Played where it strikes something. Blank for silent."))
            .add(ParamDef.of("catch_sound", ParamType.SOUND, "")
                    .label("Catch sound").desc("Played on the caster when it flies back into their hand. "
                            + "Blank for silent."))
            .add(ParamDef.of("sound_pitch", ParamType.DOUBLE, 1.0)
                    .label("Sound pitch").desc("Pitch for the hit and catch sounds."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Throw Item"; }
    @Override public String description() { return "Throws the held item, strikes a target, then returns it."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        Location start = player.getEyeLocation();
        World world = start.getWorld();
        if (world == null) return;

        double speed = Math.max(0.1, params.getDouble("speed", 1.1));
        double range = Math.max(1, params.getDouble("range", 18.0));
        double damage = params.getDouble("damage", 8.0);
        double hitRadius = Math.max(0.1, params.getDouble("hit_radius", 1.5));
        double spinDeg = params.getDouble("spin", 40.0);
        Vector3f faceAxis = axis(params.getString("face", "-y"));
        double pivot = params.getDouble("pivot", 0.0);
        Particle trail = params.getParticle("particle");
        String hitSound = params.getString("hit_sound", "");
        String catchSound = params.getString("catch_sound", "");
        float soundPitch = (float) params.getDouble("sound_pitch", 1.0);
        final ItemSmith smith = ctx.plugin() instanceof ItemSmith is ? is : null;

        ItemStack visual = ctx.itemStack() != null ? ctx.itemStack().clone() : null;
        if (visual == null) return;
        visual.setAmount(1);

        // Spawn with zeroed yaw/pitch: a display entity's own rotation is applied ON TOP of its
        // Transformation, so any yaw/pitch here would compose with (and corrupt) our aim maths.
        ItemDisplay display = world.spawn(flat(start.clone()), ItemDisplay.class, d -> {
            d.setItemStack(visual);
            d.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.NONE); // pinned, not defaulted
            d.setTeleportDuration(TELEPORT_SMOOTHING); // the anti-stutter setting
            d.setBillboard(Display.Billboard.FIXED);
            d.setBrightness(new Display.Brightness(15, 15)); // always lit, so it reads at night
            d.setPersistent(false);
        });

        Vector dir = start.getDirection().normalize();

        new BukkitRunnable() {
            private final Location cur = flat(start.clone());
            private boolean returning = false;
            private int ticks = 0;
            private float angle = 0f;

            @Override
            public void run() {
                if (++ticks > MAX_TICKS || !display.isValid() || !player.isOnline() || player.isDead()) {
                    finish();
                    return;
                }

                // The vector we actually move this tick IS the facing direction, so the
                // head leads on the way out and points back at the caster on the way home.
                Vector step;
                if (!returning) {
                    step = dir.clone().multiply(speed);
                    cur.add(step);
                    for (Entity e : world.getNearbyEntities(cur, hitRadius, hitRadius, hitRadius)) {
                        if (e.equals(player) || e.equals(display) || !(e instanceof LivingEntity living)) continue;
                        // Routed through ActionDamage so this can't re-trigger hit activators.
                        if (damage > 0) ActionDamage.deal(living, damage, player);
                        if (!hitSound.isBlank()) world.playSound(cur, hitSound, 1f, soundPitch);
                        // Run the item's projectile_hit_entity ability (if any) on whatever we struck,
                        // the same hook shoot_projectile uses — so the impact is authored in YAML.
                        if (smith != null) {
                            smith.engine().fireItem(Activators.PROJECTILE_HIT_ENTITY, player, visual, null, living);
                        }
                        returning = true;
                        break;
                    }
                    if (cur.distanceSquared(start) >= range * range) returning = true;
                } else {
                    // Home in on wherever the caster is now, so it still lands if they move.
                    Location home = player.getEyeLocation();
                    Vector back = home.toVector().subtract(cur.toVector());
                    if (back.lengthSquared() <= 2.25) { // within 1.5 blocks - caught
                        if (!catchSound.isBlank()) {
                            world.playSound(player.getLocation(), catchSound, 1f, soundPitch);
                        }
                        finish();
                        return;
                    }
                    step = back.normalize().multiply(speed * 1.3); // returns a little faster
                    cur.add(step);
                }

                display.teleport(cur);
                orient(display, step, faceAxis, pivot, angle);
                angle += (float) Math.toRadians(spinDeg);
                if (trail != null) {
                    Visuals.emit(ctx.plugin(), cur, trail, 3, 0.05, 0.05, 0.05, 0.0, null, "");
                }
            }

            private void finish() {
                display.remove();
                cancel();
            }
        }.runTaskTimer(ctx.plugin(), 1L, 1L);
    }

    /**
     * Strips yaw/pitch from a location. A display entity's own rotation composes with its
     * {@link Transformation}, so the location we teleport to must carry no rotation of its own
     * or the orientation maths below is applied on top of wherever the caster happened to look.
     */
    private static Location flat(Location loc) {
        loc.setYaw(0f);
        loc.setPitch(0f);
        return loc;
    }

    /** Maps a {@code face} value like {@code "-y"} to the model-local axis it names. */
    private static Vector3f axis(String face) {
        return switch (face == null ? "" : face.trim().toLowerCase(Locale.ROOT)) {
            case "+x", "x" -> new Vector3f(1, 0, 0);
            case "-x" -> new Vector3f(-1, 0, 0);
            case "+y", "y" -> new Vector3f(0, 1, 0);
            case "+z", "z" -> new Vector3f(0, 0, 1);
            case "-z" -> new Vector3f(0, 0, -1);
            default -> new Vector3f(0, -1, 0); // "-y" — the common case for held models
        };
    }

    /**
     * Points the item along its direction of travel and rolls it about that same axis.
     *
     * <p>The hammer's head hangs along local -Y at rest, so swinging that axis onto the travel
     * vector tips it the ~90 degrees needed to lead with the head. Because the travel vector
     * reverses on the return leg, the head turns to face the caster for free.
     * The spin is a roll <em>about the travel axis</em> rather than about world-Y, so the
     * head keeps pointing where it's going instead of sweeping around.
     */
    private static void orient(ItemDisplay display, Vector step, Vector3f faceAxis, double pivot, float roll) {
        Vector3f forward = new Vector3f((float) step.getX(), (float) step.getY(), (float) step.getZ());
        if (forward.lengthSquared() < 1.0e-6f) return;
        forward.normalize();

        // A reference "up" makes the roll deterministic. Shortest-arc (rotationTo) only pins the
        // head onto the travel axis and leaves roll about that axis arbitrary — which is why
        // off-axis throws looked twisted, and why the return leg (a ~180 degree reversal, where
        // shortest-arc must pick an essentially random perpendicular) came back facing oddly.
        Vector3f refUp = new Vector3f(0, 1, 0);
        if (Math.abs(forward.dot(refUp)) > 0.999f) refUp.set(1, 0, 0); // thrown straight up/down

        Vector3f right = new Vector3f(refUp).cross(forward).normalize();
        Vector3f up = new Vector3f(forward).cross(right).normalize();

        // Model-local basis: the axis that should lead, plus two perpendicular to it.
        Vector3f lf = new Vector3f(faceAxis);
        Vector3f ls = Math.abs(lf.y) > 0.9f ? new Vector3f(0, 0, 1) : new Vector3f(0, 1, 0);
        ls.sub(new Vector3f(lf).mul(ls.dot(lf))).normalize(); // orthogonalise against lf
        Vector3f lt = new Vector3f(lf).cross(ls).normalize();

        // Rotation carrying the local basis onto the world one: world * localᵀ.
        Matrix3f local = new Matrix3f().setColumn(0, lf).setColumn(1, ls).setColumn(2, lt);
        Matrix3f world = new Matrix3f().setColumn(0, forward).setColumn(1, right).setColumn(2, up);
        Quaternionf aim = new Quaternionf().setFromNormalized(world.mul(local.transpose()));

        Quaternionf q = new Quaternionf().rotateAxis(roll, forward).mul(aim); // aim first, then roll

        // Rotate about the model's centre rather than the entity origin. A display applies its
        // transformation as  p' = translation + rotation * p,  so rotation is about the origin —
        // and a model whose geometry sits off-origin would visibly ORBIT that point instead of
        // spinning in place. Setting translation = c - R*c re-centres the pivot on c.
        Vector3f c = new Vector3f(0, (float) pivot, 0);
        Vector3f translation = new Vector3f(c).sub(q.transform(new Vector3f(c)));

        Transformation t = display.getTransformation();
        display.setTransformation(new Transformation(translation, q, t.getScale(), t.getRightRotation()));
        display.setInterpolationDuration(TELEPORT_SMOOTHING);
        display.setInterpolationDelay(0);
    }
}
