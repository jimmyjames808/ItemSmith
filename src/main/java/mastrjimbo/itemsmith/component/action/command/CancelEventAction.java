package mastrjimbo.itemsmith.component.action.command;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.event.Cancellable;

/**
 * Cancels the triggering Bukkit event, if it is {@link Cancellable} and one
 * fired. Only effective when this action runs before any delay in the chain —
 * once a tick has passed the event has already resolved.
 */
public final class CancelEventAction implements Action {

    public static final String ID = "cancel_event";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMMAND; }
    @Override public String displayName() { return "Cancel Event"; }
    @Override public String description() { return "Cancels the triggering event."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (ctx.event() instanceof Cancellable c) {
            c.setCancelled(true);
        }
    }
}
