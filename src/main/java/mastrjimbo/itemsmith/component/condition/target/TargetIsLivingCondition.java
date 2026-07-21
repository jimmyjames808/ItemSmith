package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

/** Passes when the target is a living entity. Fail-closed when there is no living target. */
public final class TargetIsLivingCondition implements Condition {

    public static final String ID = "target_is_living";

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
        return "Target Is Living";
    }

    @Override
    public String description() {
        return "True when the target is a living entity.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return Targets.living(target) != null;
    }
}
