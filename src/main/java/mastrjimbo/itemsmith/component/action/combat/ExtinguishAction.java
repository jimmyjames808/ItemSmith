package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Entity;

/** Puts out any fire currently burning on the target. No-op for non-entity targets. */
public final class ExtinguishAction implements Action {

    public static final String ID = "extinguish";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Extinguish"; }
    @Override public String description() { return "Puts out fire currently burning on the target."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null) return;
        entity.setFireTicks(0);
    }
}
