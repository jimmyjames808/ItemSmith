package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/** Swaps the caster's main-hand and off-hand items. */
public final class SwapHandsAction implements Action {

    public static final String ID = "swap_hands";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Swap Hands"; }
    @Override public String description() { return "Swaps the caster's main-hand and off-hand items."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        PlayerInventory inv = ctx.player().getInventory();
        ItemStack main = inv.getItemInMainHand();
        inv.setItemInMainHand(inv.getItemInOffHand());
        inv.setItemInOffHand(main);
    }
}
