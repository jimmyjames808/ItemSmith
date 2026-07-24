package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Cosmetically spins the <em>target's</em> yaw around over a number of ticks — the
 * target-aware counterpart to {@code spin}, which turns the caster.
 *
 * <p>Reads well on mobs. A player's client owns its own camera, so forcing another
 * player's yaw fights the client and looks jittery; prefer this on non-player targets.
 */
public final class SpinTargetAction implements Action {

    public static final String ID = "spin_target";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("degrees", ParamType.DOUBLE, 720.0)
                    .label("Degrees").desc("Total yaw rotation across the spin."))
            .add(ParamDef.of("steps", ParamType.INT, 20)
                    .label("Steps").min(1).desc("How many ticks the spin takes."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Spin Target"; }
    @Override public String description() { return "Spins the target's yaw around cosmetically."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null) return;
        double degrees = params.getDouble("degrees", 720.0);
        int steps = Math.max(1, params.getInt("steps", 20));
        float perStep = (float) (degrees / steps);

        new BukkitRunnable() {
            int remaining = steps;

            @Override
            public void run() {
                if (remaining-- <= 0 || !entity.isValid()) {
                    cancel();
                    return;
                }
                entity.setRotation(entity.getLocation().getYaw() + perStep, entity.getLocation().getPitch());
            }
        }.runTaskTimer(ctx.plugin(), 1L, 1L);
    }
}
