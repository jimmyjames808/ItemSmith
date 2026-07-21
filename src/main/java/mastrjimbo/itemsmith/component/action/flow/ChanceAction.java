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
import java.util.concurrent.ThreadLocalRandom;

/**
 * Runs its {@code do} body with a given probability, then continues either way. Ergonomic sugar for
 * the common "N% chance to do X" without wiring up an {@code if} + chance condition.
 */
public final class ChanceAction implements FlowAction {

    public static final String ID = "chance";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("chance", ParamType.DOUBLE, 0.5)
                    .label("Chance (0-1)").range(0, 1).desc("Probability the body runs (1.0 = always)."))
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
        return "Chance";
    }

    @Override
    public String description() {
        return "Runs the nested actions a fraction of the time.";
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
        double chance = node.params().getDouble("chance", 0.5);
        List<ActionNode> body = node.bodies().getOrDefault("do", List.of());
        boolean pass = chance >= 1.0 || (chance > 0 && ThreadLocalRandom.current().nextDouble() < chance);
        if (pass && !body.isEmpty()) {
            executor.run(body, ctx, target, next);
        } else {
            next.run();
        }
    }
}
