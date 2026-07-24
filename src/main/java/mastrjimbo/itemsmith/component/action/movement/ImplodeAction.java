package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.Locale;

/**
 * Yanks the target toward a centre point — the inward half of a black hole, whirlpool, tractor beam
 * or gravity well.
 *
 * <p>Complements {@code pull}, which drags the target toward the <em>caster</em>. This pulls toward
 * a chosen centre: {@code trigger} (the event's target, e.g. where a projectile landed) or
 * {@code caster}. Paired with {@code entities_near_target} and a {@code trigger} centre, a single
 * ability sucks a whole crowd into an impact point.
 *
 * <p>{@code lift} adds an upward component so grounded targets pop off the floor rather than being
 * braked to a stop by friction, and {@code min_distance} keeps something already at the centre from
 * being flung by a near-zero direction vector.
 */
public final class ImplodeAction implements Action {

    public static final String ID = "implode";

    /** Velocity components are clamped to ±3.9 blocks/tick on the wire. */
    private static final double MAX_COMPONENT = 3.9;

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("strength", ParamType.DOUBLE, 1.2)
                    .label("Strength").min(0).desc("Pull speed toward the centre, blocks per tick."))
            .add(ParamDef.of("lift", ParamType.DOUBLE, 0.2)
                    .label("Lift").desc("Upward velocity added, so grounded targets aren't held by friction."))
            .add(ParamDef.of("center", ParamType.ENUM, "trigger")
                    .label("Centre").options("trigger", "caster")
                    .desc("Pull toward the trigger's target (e.g. an impact point) or the caster."))
            .add(ParamDef.of("min_distance", ParamType.DOUBLE, 0.5)
                    .label("Min distance").min(0)
                    .desc("Skip a target already this close, so it isn't flung by a tiny direction vector."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Implode"; }
    @Override public String description() { return "Pulls the target toward a centre point (impact or caster)."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity victim = Targets.entity(target);
        if (victim == null || ctx.player() == null) return;

        Location center = "caster".equals(params.getString("center", "trigger").trim().toLowerCase(Locale.ROOT))
                ? ctx.player().getLocation()
                : Targets.location(ctx.eventTarget());
        if (center == null) center = ctx.player().getLocation(); // no trigger target — fall back to caster
        if (center.getWorld() == null || !center.getWorld().equals(victim.getWorld())) return;

        double strength = Math.max(0, params.getDouble("strength", 1.2));
        double lift = params.getDouble("lift", 0.2);
        double minDistance = Math.max(0, params.getDouble("min_distance", 0.5));

        Vector toCenter = center.toVector().subtract(victim.getLocation().toVector());
        if (toCenter.length() < minDistance) return;

        Vector v = toCenter.normalize().multiply(strength);
        v.setY(v.getY() + lift);
        victim.setVelocity(new Vector(clamp(v.getX()), clamp(v.getY()), clamp(v.getZ())));
    }

    private static double clamp(double v) {
        return Math.max(-MAX_COMPONENT, Math.min(MAX_COMPONENT, v));
    }
}
