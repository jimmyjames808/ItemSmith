package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.Entity;

/** Passes when the target is on fire. Fail-closed when there is no entity target. */
public final class TargetIsOnFireCondition implements Condition {

    public static final String ID = "target_is_on_fire";

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
        return "Target Is On Fire";
    }

    @Override
    public String description() {
        return "True when the target is burning.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Entity e = Targets.entity(target);
        return e != null && e.getFireTicks() > 0;
    }
}
