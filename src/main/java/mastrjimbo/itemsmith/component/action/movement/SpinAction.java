package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/** Cosmetically spins the caster's yaw around over a number of ticks (a self-scheduling flourish). */
public final class SpinAction implements Action {

    public static final String ID = "spin";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("degrees", ParamType.DOUBLE, 360.0)
                    .label("Degrees").desc("Total yaw rotation across the spin."))
            .add(ParamDef.of("steps", ParamType.INT, 20)
                    .label("Steps").min(1).desc("How many ticks the spin takes."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Spin"; }
    @Override public String description() { return "Spins the caster's yaw around cosmetically."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double degrees = params.getDouble("degrees", 360.0);
        int steps = Math.max(1, params.getInt("steps", 20));
        float perStep = (float) (degrees / steps);

        new BukkitRunnable() {
            int remaining = steps;

            @Override
            public void run() {
                if (remaining-- <= 0 || !player.isValid()) {
                    cancel();
                    return;
                }
                float yaw = player.getLocation().getYaw() + perStep;
                float pitch = player.getLocation().getPitch();
                player.setRotation(yaw, pitch);
            }
        }.runTaskTimer(ctx.plugin(), 1L, 1L);
    }
}
