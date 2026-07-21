package mastrjimbo.itemsmith.component.action.flow;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.ActionExecutor;
import mastrjimbo.itemsmith.engine.ActionNode;
import mastrjimbo.itemsmith.engine.FlowAction;
import mastrjimbo.itemsmith.registry.Categories;

/**
 * Stops the current action sequence: it deliberately does not continue the chain, so nothing after it
 * runs. Useful inside an {@code if}/{@code chance} branch to bail out early.
 */
public final class AbortAction implements FlowAction {

    public static final String ID = "abort";

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
        return "Abort";
    }

    @Override
    public String description() {
        return "Stops the rest of this ability's actions from running.";
    }

    @Override
    public void execute(AbilityContext ctx, Object target, ActionNode node, ActionExecutor executor, Runnable next) {
        // Terminal: drop the continuation so the sequence ends here.
    }
}
