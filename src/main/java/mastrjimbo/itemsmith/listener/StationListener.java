package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;

/**
 * Crafting / station triggers. For craft/enchant/smith the "item" is the subject
 * of the station (the result or the enchanted item), so an ability can react to
 * its own creation; recipe-discover and advancement key to the main hand.
 */
public final class StationListener implements Listener {

    private final AbilityEngine engine;

    public StationListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        engine.fireItem(Activators.CRAFT_ITEM, player, event.getCurrentItem(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        engine.fireItem(Activators.ENCHANT_ITEM, event.getEnchanter(), event.getItem(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmith(SmithItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        engine.fireItem(Activators.SMITH_ITEM, player, event.getCurrentItem(), event, null);
    }

    @EventHandler
    public void onRecipeDiscover(PlayerRecipeDiscoverEvent event) {
        engine.fireItem(Activators.RECIPE_DISCOVER, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, null);
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        engine.fireItem(Activators.ADVANCEMENT, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, null);
    }
}
