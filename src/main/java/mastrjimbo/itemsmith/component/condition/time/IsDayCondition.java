package mastrjimbo.itemsmith.component.condition.time;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while it is daytime in the caster's world. */
public final class IsDayCondition implements Condition {

    public static final String ID = "is_day";

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
        return "Is Day";
    }

    @Override
    public String description() {
        return "True while it is daytime in the caster's world.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        long t = ctx.player().getWorld().getTime();
        return t < 12300 || t > 23850;
    }
}
