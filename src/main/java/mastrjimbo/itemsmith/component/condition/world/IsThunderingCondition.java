package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while a thunderstorm is active in the caster's world. */
public final class IsThunderingCondition implements Condition {

    public static final String ID = "is_thundering";

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
        return "Is Thundering";
    }

    @Override
    public String description() {
        return "True while a thunderstorm is active in the caster's world.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getWorld().isThundering();
    }
}
