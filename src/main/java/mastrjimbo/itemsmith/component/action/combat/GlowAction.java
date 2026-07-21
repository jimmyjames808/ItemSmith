package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import mastrjimbo.itemsmith.util.TempTasks;
import org.bukkit.entity.Entity;

/** Outlines the target with a glow for a number of seconds, then clears it (a self-reverting temp effect). */
public final class GlowAction implements Action {

    public static final String ID = "glow";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("seconds", ParamType.INT, 5)
                    .label("Duration (seconds)").min(0).desc("How long the glow lasts (0 = until cleared elsewhere)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Glow"; }
    @Override public String description() { return "Makes the target glow for a while."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null) return;
        entity.setGlowing(true);
        int seconds = params.getInt("seconds", 5);
        if (seconds > 0) {
            TempTasks.later(ctx.plugin(), seconds * 20L, () -> {
                if (entity.isValid()) entity.setGlowing(false);
            });
        }
    }
}
