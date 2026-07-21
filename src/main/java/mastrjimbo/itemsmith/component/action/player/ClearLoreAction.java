package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/** Clears the triggering item's lore entirely. No-op if there is no triggering item. */
public final class ClearLoreAction implements Action {

    public static final String ID = "clear_lore";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.PLAYER;
    }

    @Override
    public String displayName() {
        return "Clear Lore";
    }

    @Override
    public String description() {
        return "Clears the triggering item's lore.";
    }

    @Override
    public ParamSchema schema() {
        return ParamSchema.EMPTY;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack itemStack = ctx.itemStack();
        if (itemStack == null) return;
        ItemMeta m = itemStack.getItemMeta();
        m.lore(null);
        itemStack.setItemMeta(m);
    }
}
