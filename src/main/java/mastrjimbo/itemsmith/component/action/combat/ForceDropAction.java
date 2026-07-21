package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Drops the target player's held item on the ground and empties their hand.
 * No-op if the target isn't a player or is holding nothing.
 */
public final class ForceDropAction implements Action {

    public static final String ID = "force_drop";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Force Drop"; }
    @Override public String description() { return "Drops the target player's held item on the ground."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = Targets.player(target);
        if (player == null) return;
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || held.getType().isAir()) return;
        player.getWorld().dropItemNaturally(player.getLocation(), held);
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
    }
}
