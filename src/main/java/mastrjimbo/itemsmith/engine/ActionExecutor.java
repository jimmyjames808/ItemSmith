package mastrjimbo.itemsmith.engine;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs an item's action tree against one target. A flat list runs top-to-bottom;
 * a control-flow node ({@link FlowAction}) — delay, repeat, if, random — can pause,
 * branch, or loop by scheduling the continuation of the remaining list. Because
 * {@code delay} reschedules the tail on a later tick, execution is a continuation
 * chain rather than a simple loop, so a nested delay inside a repeat/if/random body
 * correctly holds up whatever follows it (in the body and in the parent list).
 *
 * <p>Each action is fault-isolated: if one throws, it is logged and skipped and the
 * chain continues — the same guarantee the pre-M2 synchronous loop gave.
 */
public final class ActionExecutor {

    private final Logger logger;

    public ActionExecutor(Logger logger) {
        this.logger = logger;
    }

    /** Runs {@code nodes} in order, invoking {@code onComplete} (may be null) once the whole list finishes. */
    public void run(List<ActionNode> nodes, AbilityContext ctx, Object target, Runnable onComplete) {
        step(nodes, 0, ctx, target, onComplete);
    }

    private void step(List<ActionNode> nodes, int index, AbilityContext ctx, Object target, Runnable onComplete) {
        if (index >= nodes.size()) {
            if (onComplete != null) onComplete.run();
            return;
        }
        ActionNode node = nodes.get(index);
        Runnable next = () -> step(nodes, index + 1, ctx, target, onComplete);
        Action def = node.definition();
        try {
            if (def instanceof FlowAction flow) {
                // Resolve <stat:...> tokens in this node's own params (e.g. repeat times, chance
                // percent); nested bodies/conditions resolve themselves as they run.
                flow.execute(ctx, target, node.resolved(ctx), this, next);
            } else {
                def.run(ctx, target, node.params().resolve(ctx));
                next.run();
            }
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Action '" + def.id() + "' on item '" + ctx.itemId()
                    + "' threw; skipping it.", e);
            next.run();
        }
    }
}
