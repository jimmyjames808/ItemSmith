package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

/** Passes when the current target is a player. Fail-closed when there is no player target. */
public final class TargetIsPlayerCondition implements Condition {

    public static final String ID = "target_is_player";

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
        return "Target Is Player";
    }

    @Override
    public String description() {
        return "True when the target is a player.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return Targets.player(target) != null;
    }
}
