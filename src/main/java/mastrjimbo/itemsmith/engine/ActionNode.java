package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.param.ParamValues;

import java.util.List;
import java.util.Map;

/**
 * One node in an item's action tree. A leaf node is just a configured {@link Action}
 * ({@code definition} + {@code params}) with empty {@code conditions}, {@code bodies}
 * and {@code branches}. A control-flow node (a {@link FlowAction} such as repeat/if/random)
 * additionally carries:
 * <ul>
 *   <li>{@code conditions} — an inline gate (used by {@code if});</li>
 *   <li>{@code bodies} — named nested action-lists (e.g. {@code do}, {@code then}, {@code else});</li>
 *   <li>{@code branches} — weighted nested action-lists (used by {@code random}).</li>
 * </ul>
 * This replaces the flat {@code Configured<Action>} so actions can nest, which is what
 * lets flow control (delay/repeat/if/random) exist without special-casing the engine.
 */
public record ActionNode(
        Action definition,
        ParamValues params,
        List<Configured<Condition>> conditions,
        Map<String, List<ActionNode>> bodies,
        List<Branch> branches
) {

    /** One weighted branch of a {@code random} node: a relative weight and the actions to run if chosen. */
    public record Branch(double weight, List<ActionNode> body) {
    }

    /**
     * Returns a copy of this node with its top-level {@code params} stat-resolved against
     * {@code ctx} (see {@link ParamValues#resolve}). Nested {@code conditions}/{@code bodies}
     * are left untouched — they resolve themselves when they run (each through its own
     * {@code Conditions}/{@code ActionExecutor} pass). Returns {@code this} when nothing changed.
     */
    public ActionNode resolved(AbilityContext ctx) {
        ParamValues r = params.resolve(ctx);
        return r == params ? this : new ActionNode(definition, r, conditions, bodies, branches);
    }
}
