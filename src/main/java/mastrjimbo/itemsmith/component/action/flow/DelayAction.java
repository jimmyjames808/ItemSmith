package mastrjimbo.itemsmith.component.action.flow;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.ActionExecutor;
import mastrjimbo.itemsmith.engine.ActionNode;
import mastrjimbo.itemsmith.engine.FlowAction;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.registry.Categories;

/**
 * Waits a number of ticks, then runs the following actions. This is what makes
 * timed combos possible (e.g. teleport → delay → explosion): the executor
 * reschedules the rest of the list on a later tick rather than blocking.
 */
public final class DelayAction implements FlowAction {

    public static final String ID = "delay";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("ticks", ParamType.INT, 20)
                    .label("Delay (ticks)").range(0, 72000)
                    .desc("Ticks to wait before the following actions run (20 ticks = 1 second)."))
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
        return "Delay";
    }

    @Override
    public String description() {
        return "Waits before running the following actions.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void execute(AbilityContext ctx, Object target, ActionNode node, ActionExecutor executor, Runnable next) {
        int ticks = node.params().getInt("ticks", 20);
        if (ticks <= 0) {
            next.run();
            return;
        }
        ctx.plugin().getServer().getScheduler().runTaskLater(ctx.plugin(), next, ticks);
    }
}
