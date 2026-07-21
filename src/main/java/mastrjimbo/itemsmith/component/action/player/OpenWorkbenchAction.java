package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Opens a virtual crafting table for the caster, usable anywhere. */
public final class OpenWorkbenchAction implements Action {

    public static final String ID = "open_workbench";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Open Workbench"; }
    @Override public String description() { return "Opens a virtual crafting table for the caster."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ctx.player().openWorkbench(null, true);
    }
}
