package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/** Strips every enchantment off the triggering item. No-op if there is no triggering item. */
public final class ClearEnchantsAction implements Action {

    public static final String ID = "clear_enchants";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Clear Enchants"; }
    @Override public String description() { return "Removes all enchantments from the triggering item."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack itemStack = ctx.itemStack();
        if (itemStack == null) return;
        for (Enchantment e : new ArrayList<>(itemStack.getEnchantments().keySet())) {
            itemStack.removeEnchantment(e);
        }
    }
}
