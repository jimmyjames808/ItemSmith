package mastrjimbo.itemsmith.component.action.combat;

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
 * Freezes the target with the powder-snow frost effect (white overlay, shaking, slowdown) for a
 * duration. A one-shot {@code setFreezeTicks} decays every tick outside powder snow, so this pins the
 * freeze counter at max for the duration and clears it at the end — giving a visible, lasting freeze.
 */
public final class FreezeAction implements Action {

    public static final String ID = "freeze";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("seconds", ParamType.INT, 3)
                    .label("Duration (seconds)").min(0).desc("How long the frozen effect lasts."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Freeze"; }
    @Override public String description() { return "Freezes the target (powder-snow frost) for a while."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null) return;
        int seconds = params.getInt("seconds", 3);
        if (seconds <= 0) return;
        int max = Math.max(140, entity.getMaxFreezeTicks());
        int durationTicks = seconds * 20;
        new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                if (elapsed >= durationTicks || entity.isDead() || !entity.isValid()) {
                    if (entity.isValid()) entity.setFreezeTicks(0);
                    cancel();
                    return;
                }
                entity.setFreezeTicks(max);
                elapsed += 2;
            }
        }.runTaskTimer(ctx.plugin(), 0L, 2L);
    }
}
