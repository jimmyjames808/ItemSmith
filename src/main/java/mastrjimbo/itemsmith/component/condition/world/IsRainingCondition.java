package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while it is raining (storming) in the caster's world. */
public final class IsRainingCondition implements Condition {

    public static final String ID = "is_raining";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.CONDITION;
    }

    @Override
    public String displayName() {
        return "Is Raining";
    }

    @Override
    public String description() {
        return "True while it is raining in the caster's world.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getWorld().hasStorm();
    }
}
