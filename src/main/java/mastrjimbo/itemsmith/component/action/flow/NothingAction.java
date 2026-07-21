package mastrjimbo.itemsmith.component.action.flow;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/**
 * Does nothing. A readable no-op — mainly a weight filler inside a {@code random} branch to express a
 * "chance to do nothing" outcome.
 */
public final class NothingAction implements Action {

    public static final String ID = "nothing";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.META;
    }

    @Override
    public String displayName() {
        return "Nothing";
    }

    @Override
    public String description() {
        return "Does nothing (random-branch filler).";
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        // intentionally empty
    }
}
