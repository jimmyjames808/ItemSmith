package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

import java.util.List;

/**
 * Targets the trigger's natural target — e.g. the entity struck by a hit
 * activator. Yields an empty list (and thus runs no actions) when the trigger
 * had no target, such as a click in the air.
 */
public final class TriggerTargeter implements Targeter {

    public static final String ID = "target";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.TARGETER;
    }

    @Override
    public String displayName() {
        return "Trigger Target";
    }

    @Override
    public String description() {
        return "The entity or block the trigger acted on (e.g. the mob you hit).";
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        Object target = ctx.eventTarget();
        return target == null ? List.of() : List.of(target);
    }
}
