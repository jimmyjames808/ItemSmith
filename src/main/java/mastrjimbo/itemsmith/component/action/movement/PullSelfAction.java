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
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Flings the caster toward the target's location — the pull half of a grappling hook.
 *
 * <p>Distinct from {@code velocity}, which takes fixed x/y/z, and from {@code propel}/{@code leap},
 * which move the <em>target</em>. This is the only action that moves the caster <em>toward a
 * resolved location</em>, so it pairs with a targeter like {@code looking_at_block}.
 *
 * <p>{@code arc} adds lift on top of the straight-line pull so you rise over the lip of whatever
 * you hooked instead of slamming into its face.
 */
public final class PullSelfAction implements Action {

    public static final String ID = "pull_self";

    /**
     * Minecraft encodes entity velocity as a short clamped to ±3.9 blocks/tick, so anything past
     * this is silently truncated on the wire. Clamping here keeps the action honest: a huge
     * strength quietly doing nothing extra is a nasty thing to debug.
     */
    private static final double MAX_COMPONENT = 3.9;

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("strength", ParamType.DOUBLE, 1.4)
                    .label("Strength").min(0).desc("Pull speed in blocks per tick. Capped near 3.9."))
            .add(ParamDef.of("arc", ParamType.DOUBLE, 0.4)
                    .label("Arc").desc("Extra upward lift, so you clear the ledge you hooked."))
            .add(ParamDef.of("max_distance", ParamType.DOUBLE, 0.0)
                    .label("Max distance").min(0)
                    .desc("Skip the pull past this range. 0 for no limit."))
            .add(ParamDef.of("min_distance", ParamType.DOUBLE, 1.5)
                    .label("Min distance").min(0)
                    .desc("Skip the pull when already this close, so point-blank use doesn't fling you."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Pull Self"; }
    @Override public String description() { return "Flings the caster toward the target's location."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location dest = Targets.location(target);
        if (dest == null) return;
        Player player = ctx.player();
        if (player == null || dest.getWorld() == null
                || !dest.getWorld().equals(player.getWorld())) return;

        double strength = Math.max(0, params.getDouble("strength", 1.4));
        double arc = params.getDouble("arc", 0.4);
        double maxDistance = Math.max(0, params.getDouble("max_distance", 0.0));
        double minDistance = Math.max(0, params.getDouble("min_distance", 1.5));

        Vector delta = dest.toVector().subtract(player.getEyeLocation().toVector());
        double distance = delta.length();
        if (distance < minDistance) return;
        if (maxDistance > 0 && distance > maxDistance) return;

        Vector velocity = delta.normalize().multiply(strength);
        velocity.setY(velocity.getY() + arc);
        player.setVelocity(new Vector(
                clamp(velocity.getX()), clamp(velocity.getY()), clamp(velocity.getZ())));
    }

    private static double clamp(double v) {
        return Math.max(-MAX_COMPONENT, Math.min(MAX_COMPONENT, v));
    }
}
