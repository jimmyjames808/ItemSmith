package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Sets the caster's world time-of-day (shared by everyone in that world). */
public final class SetTimeAction implements Action {

    public static final String ID = "set_time";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("time", ParamType.INT, 1000)
                    .label("Time").range(0, 24000).desc("The world time to set (0-24000)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Set Time"; }
    @Override public String description() { return "Sets the caster's world time-of-day."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        int time = params.getInt("time", 1000);
        ctx.player().getWorld().setTime(time);
    }
}
