package mastrjimbo.itemsmith.component.action.flow;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.ActionExecutor;
import mastrjimbo.itemsmith.engine.ActionNode;
import mastrjimbo.itemsmith.engine.FlowAction;
import mastrjimbo.itemsmith.registry.Categories;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Picks one of several weighted {@code branches} and runs its {@code do} body. Higher {@code weight}
 * makes a branch more likely; a branch with an empty body (or a {@code nothing} action) is the classic
 * "chance to do nothing" filler.
 */
public final class RandomAction implements FlowAction {

    public static final String ID = "random";

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
        return "Random";
    }

    @Override
    public String description() {
        return "Runs one of several weighted branches at random.";
    }

    @Override
    public boolean usesBranches() {
        return true;
    }

    @Override
    public void execute(AbilityContext ctx, Object target, ActionNode node, ActionExecutor executor, Runnable next) {
        List<ActionNode.Branch> branches = node.branches();
        double total = 0;
        for (ActionNode.Branch b : branches) {
            if (b.weight() > 0) total += b.weight();
        }
        if (total <= 0) {
            next.run();
            return;
        }
        double roll = ThreadLocalRandom.current().nextDouble() * total;
        for (ActionNode.Branch b : branches) {
            if (b.weight() <= 0) continue;
            roll -= b.weight();
            if (roll < 0) {
                executor.run(b.body(), ctx, target, next);
                return;
            }
        }
        next.run(); // rounding fallthrough
    }
}
