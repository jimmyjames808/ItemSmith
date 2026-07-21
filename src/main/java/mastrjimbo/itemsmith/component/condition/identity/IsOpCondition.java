package mastrjimbo.itemsmith.component.condition.identity;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster is a server operator. */
public final class IsOpCondition implements Condition {

    public static final String ID = "is_op";

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
        return "Is Op";
    }

    @Override
    public String description() {
        return "True while the caster is a server operator.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().isOp();
    }
}
