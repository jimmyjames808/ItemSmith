package mastrjimbo.itemsmith.component.condition.playerstate;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.param.ParamValues;

/** Passes while the caster is sprinting. */
public final class IsSprintingCondition implements Condition {

    public static final String ID = "is_sprinting";

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
        return "Is Sprinting";
    }

    @Override
    public String description() {
        return "True while the caster is sprinting.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().isSprinting();
    }
}
