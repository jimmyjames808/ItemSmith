package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Opens the caster's own ender chest inventory. */
public final class OpenEnderchestAction implements Action {

    public static final String ID = "open_enderchest";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Open Ender Chest"; }
    @Override public String description() { return "Opens the caster's ender chest."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ctx.player().openInventory(ctx.player().getEnderChest());
    }
}
