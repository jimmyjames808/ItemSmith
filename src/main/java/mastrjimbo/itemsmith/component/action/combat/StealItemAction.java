package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Takes the first non-air item stack out of the target player's inventory and gives it to
 * the caster. No-op if the target isn't a player, the caster is unknown, or the target's
 * inventory is empty.
 */
public final class StealItemAction implements Action {

    public static final String ID = "steal_item";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Steal Item"; }
    @Override public String description() { return "Takes an item from the target player and gives it to the caster."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player targetPlayer = Targets.player(target);
        Player caster = ctx.player();
        if (targetPlayer == null || caster == null) return;
        PlayerInventory inv = targetPlayer.getInventory();
        ItemStack[] contents = inv.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType().isAir()) continue;
            caster.getInventory().addItem(stack.clone());
            inv.setItem(i, null);
            return;
        }
    }
}
