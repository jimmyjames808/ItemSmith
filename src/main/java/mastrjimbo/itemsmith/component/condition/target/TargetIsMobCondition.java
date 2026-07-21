package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.Monster;

/** Passes when the target is a hostile mob. Fail-closed when there is no monster target. */
public final class TargetIsMobCondition implements Condition {

    public static final String ID = "target_is_mob";

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
        return "Target Is Mob";
    }

    @Override
    public String description() {
        return "True when the target is a hostile mob.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return Targets.entity(target) instanceof Monster;
    }
}
