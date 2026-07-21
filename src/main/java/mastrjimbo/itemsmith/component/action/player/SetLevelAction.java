package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Sets the caster's experience level directly. */
public final class SetLevelAction implements Action {

    public static final String ID = "set_level";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("level", ParamType.INT, 0)
                    .label("Level").min(0).desc("The experience level to set."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Set Level"; }
    @Override public String description() { return "Sets the caster's experience level."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        int level = params.getInt("level", 0);
        ctx.player().setLevel(Math.max(0, level));
    }
}
