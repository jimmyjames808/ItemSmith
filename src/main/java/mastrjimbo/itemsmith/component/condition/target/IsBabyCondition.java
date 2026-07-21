package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;

/** Passes when the target is a baby entity. Fail-closed when there is no entity target. */
public final class IsBabyCondition implements Condition {

    public static final String ID = "is_baby";

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
        return "Is Baby";
    }

    @Override
    public String description() {
        return "True when the target is a baby entity.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Entity e = Targets.entity(target);
        if (e == null) return false;
        if (e instanceof Ageable ag) return !ag.isAdult();
        if (e instanceof Zombie z) return z.isBaby();
        return false;
    }
}
