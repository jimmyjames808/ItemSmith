package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.registry.Activators;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.ActionDamage;
import mastrjimbo.itemsmith.util.Visuals;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Launches a real, travelling projectile: an invisible armour stand wearing a chosen head (a floating
 * mob head) that flies from the caster in their look direction, trails particles each tick, and — on
 * colliding with a living entity or a solid block — deals damage, bursts particles, and vanishes.
 * Self-contained (no target needed); it always fires from the caster.
 */
public final class ShootProjectileAction implements Action {

    public static final String ID = "shoot_projectile";

    // A (small) armour stand's helmet renders this far above the stand's base location, so we place the
    // stand this much BELOW the logical projectile point to make the floating head sit on the trajectory.
    private static final double HEAD_OFFSET = 0.75;

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("head", ParamType.HEAD, "wither_skeleton_skull")
                    .label("Head / display").desc("Item worn on the flying armour stand (e.g. a mob head)."))
            .add(ParamDef.of("speed", ParamType.DOUBLE, 0.8)
                    .label("Speed").range(0.1, 5.0).desc("Blocks travelled per tick."))
            .add(ParamDef.of("range", ParamType.DOUBLE, 24.0)
                    .label("Range").range(1, 128).desc("Max blocks travelled before it fizzles."))
            .add(ParamDef.of("damage", ParamType.DOUBLE, 6.0)
                    .label("Damage").min(0).desc("Damage dealt to the first entity hit."))
            .add(ParamDef.of("hit_radius", ParamType.DOUBLE, 1.2)
                    .label("Hit radius").range(0.2, 4.0).desc("How close to an entity counts as a hit."))
            .add(ParamDef.of("particle", ParamType.PARTICLE, "flame")
                    .label("Trail particle").desc("Particle trailed each tick and burst on impact."))
            .add(ParamDef.of("gravity", ParamType.BOOLEAN, false)
                    .label("Gravity").desc("If true the projectile arcs and drops instead of flying straight."))
            .add(ParamDef.of("trail_head", ParamType.HEAD, "")
                    .label("Trail head (optional)").desc("Show floating heads for the trail/impact instead of the particle. Blank = use the particle."))
            .add(ParamDef.of("trail_head_owner", ParamType.STRING, "")
                    .label("Trail player-head owner").desc("If trail_head is player_head, the player name whose skin to use."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Shoot Projectile"; }
    @Override public String description() { return "Fires a flying head-on-armour-stand projectile that damages what it hits."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player source = ctx.player();
        World world = source.getWorld();
        Location eye = source.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        Material head = params.getMaterial("head");
        if (head == null) head = Material.WITHER_SKELETON_SKULL;
        double speed = Math.max(0.1, params.getDouble("speed", 0.8));
        double range = Math.max(1.0, params.getDouble("range", 24.0));
        double damage = Math.max(0.0, params.getDouble("damage", 6.0));
        double hitRadius = Math.max(0.2, params.getDouble("hit_radius", 1.2));
        Particle particle = params.getParticle("particle");
        if (particle == null) particle = Particle.FLAME;
        boolean gravity = params.getBool("gravity", false);
        final Material trailHead = params.getMaterial("trail_head");
        final String trailOwner = params.getString("trail_head_owner", "");
        // Captured so the projectile can fire hit activators back onto the item's pipeline on impact.
        final ItemSmith smith = ctx.plugin() instanceof ItemSmith is ? is : null;
        final ItemStack projectileItem = ctx.itemStack();

        // The logical projectile point (where the head renders, and where collisions/particles happen)
        // starts just in front of the caster's eyes; the stand's base sits HEAD_OFFSET below it.
        Location start = eye.clone().add(dir.clone().multiply(1.2));
        Location spawnLoc = start.clone().subtract(0, HEAD_OFFSET, 0);
        spawnLoc.setDirection(dir);
        final ItemStack helmet = new ItemStack(head);
        ArmorStand stand = world.spawn(spawnLoc, ArmorStand.class, s -> {
            s.setVisible(false);       // body hidden — only the helmet (the "head") renders, floating
            s.setSmall(true);
            s.setGravity(false);       // movement is driven manually below
            s.setBasePlate(false);
            s.setArms(false);
            s.setInvulnerable(true);
            s.setCollidable(false);
            s.setPersistent(false);
            if (s.getEquipment() != null) s.getEquipment().setHelmet(helmet);
        });

        final Particle trail = particle;
        new BukkitRunnable() {
            final Location head = start.clone(); // the projectile point (where the skull visually is)
            double travelled = 0;
            int ticks = 0;
            final Vector velocity = dir.clone().multiply(speed);

            @Override
            public void run() {
                if (stand.isDead() || !stand.isValid()) {
                    cancel();
                    return;
                }
                if (travelled >= range || ticks++ > 200) {
                    stand.remove();
                    cancel();
                    return;
                }
                if (gravity) velocity.setY(velocity.getY() - 0.03);

                head.add(velocity); // advance the logical head point
                if (head.getBlock().getType().isSolid()) {
                    impact(ctx.plugin(), head, trail, trailHead, trailOwner);
                    if (smith != null) {
                        smith.engine().fireItem(Activators.PROJECTILE_HIT_BLOCK, source, projectileItem, null,
                                head.getBlock());
                    }
                    stand.remove();
                    cancel();
                    return;
                }
                // Place the stand HEAD_OFFSET below the head point so the floating helmet sits on the path.
                Location standLoc = head.clone().subtract(0, HEAD_OFFSET, 0);
                standLoc.setDirection(velocity);
                stand.teleport(standLoc);
                Visuals.emit(ctx.plugin(), head, trail, 3, 0.05, 0.05, 0.05, 0.0, trailHead, trailOwner);

                for (Entity e : world.getNearbyEntities(head, hitRadius, hitRadius, hitRadius)) {
                    if (e instanceof LivingEntity le && !(e instanceof ArmorStand) && !e.equals(source)) {
                        if (damage > 0) ActionDamage.deal(le, damage, ctx.player());
                        impact(ctx.plugin(), head, trail, trailHead, trailOwner);
                        // Run the item's projectile_hit_entity ability (if any) on the entity we hit —
                        // this is how a projectile "does stuff" beyond its built-in damage.
                        if (smith != null) {
                            smith.engine().fireItem(Activators.PROJECTILE_HIT_ENTITY, source, projectileItem, null, le);
                        }
                        stand.remove();
                        cancel();
                        return;
                    }
                }
                travelled += velocity.length();
            }
        }.runTaskTimer(ctx.plugin(), 0L, 1L);
    }

    private void impact(org.bukkit.plugin.Plugin plugin, Location loc, Particle particle, Material head, String owner) {
        Visuals.emit(plugin, loc, particle, 24, 0.3, 0.3, 0.3, 0.05, head, owner);
    }
}
