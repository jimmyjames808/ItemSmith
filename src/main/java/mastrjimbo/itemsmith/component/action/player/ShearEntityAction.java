package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Sheep;

/** Shears the target sheep. No-op for non-sheep targets. */
public final class ShearEntityAction implements Action {

    public static final String ID = "shear_entity";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Shear Entity"; }
    @Override public String description() { return "Shears the target sheep."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (target instanceof Sheep s) {
            s.setSheared(true);
        }
    }
}
