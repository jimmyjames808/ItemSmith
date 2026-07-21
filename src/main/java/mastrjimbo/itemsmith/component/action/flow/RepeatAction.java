package mastrjimbo.itemsmith.component.action.flow;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.ActionExecutor;
import mastrjimbo.itemsmith.engine.ActionNode;
import mastrjimbo.itemsmith.engine.FlowAction;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.registry.Categories;

import java.util.List;
import java.util.Set;

/**
 * Runs its {@code do} body a fixed number of times, sequentially. Each iteration
 * completes fully — including any {@code delay} inside the body — before the next
 * begins, so {@code repeat{times: 5, do: [particle_ring, delay: 2]}} spaces the
 * rings two ticks apart rather than firing them all at once.
 */
public final class RepeatAction implements FlowAction {

    public static final String ID = "repeat";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("times", ParamType.INT, 3)
                    .label("Times").range(0, 1000).desc("How many times to run the body."))
            .build();

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
        return "Repeat";
    }

    @Override
    public String description() {
        return "Runs the nested actions a number of times.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public Set<String> bodyKeys() {
        return Set.of("do");
    }

    @Override
    public void execute(AbilityContext ctx, Object target, ActionNode node, ActionExecutor executor, Runnable next) {
        int times = node.params().getInt("times", 1);
        List<ActionNode> body = node.bodies().getOrDefault("do", List.of());
        if (times <= 0 || body.isEmpty()) {
            next.run();
            return;
        }
        iterate(executor, body, ctx, target, times, 0, next);
    }

    private void iterate(ActionExecutor executor, List<ActionNode> body, AbilityContext ctx, Object target,
                         int times, int i, Runnable next) {
        if (i >= times) {
            next.run();
            return;
        }
        executor.run(body, ctx, target, () -> iterate(executor, body, ctx, target, times, i + 1, next));
    }
}
