package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Overrides the caster's personal client-side time, independent of the world clock. */
public final class SetPlayerTimeAction implements Action {

    public static final String ID = "set_player_time";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("time", ParamType.INT, 1000)
                    .label("Time").range(0, 24000).desc("The personal time to show the caster."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Set Player Time"; }
    @Override public String description() { return "Overrides the caster's personal time view, independent of the world."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        int time = params.getInt("time", 1000);
        ctx.player().setPlayerTime(time, false);
    }
}
