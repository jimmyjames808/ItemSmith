package mastrjimbo.itemsmith.component.condition.region;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes when the caster is allowed to build at their current location. Allows when no protection plugin. */
public final class CanBuildCondition implements Condition {

    public static final String ID = "can_build";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.CONDITION; }
    @Override public String displayName() { return "Can Build"; }
    @Override public String description() { return "True when the caster may build where they stand."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return true;
        return plugin.protection().canBuild(ctx.player(), ctx.player().getLocation());
    }
}
