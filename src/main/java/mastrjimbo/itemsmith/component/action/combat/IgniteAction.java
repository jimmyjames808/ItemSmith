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

/** Sets the target on fire for a number of seconds. No-op for non-entity targets. */
public final class IgniteAction implements Action {

    public static final String ID = "ignite";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("seconds", ParamType.INT, 3)
                    .label("Duration (seconds)").min(0).desc("How long the target burns."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Ignite"; }
    @Override public String description() { return "Sets the target on fire for a while."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null) return;
        int seconds = params.getInt("seconds", 3);
        if (seconds <= 0) return;
        entity.setFireTicks(seconds * 20);
    }
}
