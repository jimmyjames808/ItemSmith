package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.param.ParamValues;

import java.util.Set;

/**
 * A control-flow action — delay, repeat, if, random, chance, abort. Unlike a leaf
 * {@link Action}, a flow action drives execution: it can pause (schedule the
 * continuation on a later tick), branch, or loop by running nested action-lists
 * via the {@link ActionExecutor}. The executor dispatches to {@link #execute}
 * instead of {@link #run}.
 */
public interface FlowAction extends Action {

    /** Config keys under which a single nested action-list lives (e.g. {@code do}, {@code then}, {@code else}). */
    default Set<String> bodyKeys() {
        return Set.of();
    }

    /** True if this node reads a weighted {@code branches:} list (used by {@code random}). */
    default boolean usesBranches() {
        return false;
    }

    /**
     * Runs this control node. The implementation MUST eventually invoke {@code next} to continue the
     * rest of the parent action-list — except deliberately terminal nodes (e.g. {@code abort}), which
     * drop it to stop the sequence. Run a child body with
     * {@code executor.run(body, ctx, target, onDone)} and pass {@code next} (or a continuation that
     * ends in {@code next}) as its completion callback so nested delays hold up the parent correctly.
     */
    void execute(AbilityContext ctx, Object target, ActionNode node, ActionExecutor executor, Runnable next);

    /** Flow nodes are dispatched via {@link #execute}; they never run as a leaf. */
    @Override
    default void run(AbilityContext ctx, Object target, ParamValues params) {
        // no-op — control flow happens in execute()
    }
}
