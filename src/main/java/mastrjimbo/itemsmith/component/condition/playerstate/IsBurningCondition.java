package mastrjimbo.itemsmith.component.condition.playerstate;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.param.ParamValues;

/** Passes while the caster is on fire. */
public final class IsBurningCondition implements Condition {

    public static final String ID = "is_burning";

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
        return "Is Burning";
    }

    @Override
    public String description() {
        return "True while the caster is on fire.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getFireTicks() > 0;
    }
}
