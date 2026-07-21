package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;

/** Passes when the target is a tamed entity. Fail-closed when there is no entity target. */
public final class IsTamedCondition implements Condition {

    public static final String ID = "is_tamed";

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
        return "Is Tamed";
    }

    @Override
    public String description() {
        return "True when the target is a tamed entity.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Entity e = Targets.entity(target);
        return e instanceof Tameable t && t.isTamed();
    }
}
