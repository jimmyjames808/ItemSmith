package mastrjimbo.itemsmith.component.action.flow;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.ActionExecutor;
import mastrjimbo.itemsmith.engine.ActionNode;
import mastrjimbo.itemsmith.engine.Conditions;
import mastrjimbo.itemsmith.engine.FlowAction;
import mastrjimbo.itemsmith.registry.Categories;

import java.util.List;
import java.util.Set;

/**
 * Branches on an inline {@code conditions:} gate: runs the {@code then} body when every condition
 * passes, otherwise the {@code else} body. Reuses the engine's condition registry, so it gains every
 * condition added in M3 automatically (today only {@code chance} exists).
 */
public final class IfAction implements FlowAction {

    public static final String ID = "if";

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
        return "If / Else";
    }

    @Override
    public String description() {
        return "Runs one branch if its conditions pass, otherwise the other.";
    }

    @Override
    public Set<String> bodyKeys() {
        return Set.of("then", "else");
    }

    @Override
    public void execute(AbilityContext ctx, Object target, ActionNode node, ActionExecutor executor, Runnable next) {
        // Conditions here see the resolved per-target object, so an if inside a radius/nearby targeter
        // filters per entity (e.g. "if target_is_player"). Route through the shared gate for one
        // definition of pass/fail (AND, fail-closed, logs throwers).
        boolean pass = Conditions.allPass(node.conditions(), ctx, target, ctx.plugin().getLogger());
        List<ActionNode> body = node.bodies().getOrDefault(pass ? "then" : "else", List.of());
        if (body.isEmpty()) {
            next.run();
            return;
        }
        executor.run(body, ctx, target, next);
    }
}
