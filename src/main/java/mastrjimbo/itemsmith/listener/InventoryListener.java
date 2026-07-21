package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 * The inventory / item-movement family: drop, pickup, hand-swap, hotbar
 * select/deselect, GUI click and drag, container open/close, and arrow pickup.
 */
public final class InventoryListener implements Listener {

    private final AbilityEngine engine;

    public InventoryListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        engine.fireItem(Activators.ITEM_DROP, event.getPlayer(),
                event.getItemDrop().getItemStack(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        engine.fireItem(Activators.ITEM_PICKUP, player, event.getItem().getItemStack(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupArrow(PlayerPickupArrowEvent event) {
        // Fire for the held item (e.g. a bow/quiver reacting); the projectile is the target.
        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        AbstractArrow arrow = event.getArrow();
        engine.fireItem(Activators.PICKUP_PROJECTILE, player, held, event, arrow);
        if (arrow instanceof Trident) {
            engine.fireItem(Activators.PICKUP_TRIDENT, player, held, event, arrow);
        } else if (arrow instanceof SpectralArrow) {
            engine.fireItem(Activators.PICKUP_SPECTRAL_ARROW, player, held, event, arrow);
        } else if (arrow instanceof Arrow) {
            engine.fireItem(Activators.PICKUP_ARROW, player, held, event, arrow);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwap(PlayerSwapHandItemsEvent event) {
        engine.fireItem(Activators.SWAP_HAND, event.getPlayer(), event.getMainHandItem(), event, null);
        engine.fireItem(Activators.SWAP_HAND, event.getPlayer(), event.getOffHandItem(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onHeld(PlayerItemHeldEvent event) {
        PlayerInventory inv = event.getPlayer().getInventory();
        engine.fireItem(Activators.ITEM_HOLD, event.getPlayer(), inv.getItem(event.getNewSlot()), event, null);
        engine.fireItem(Activators.ITEM_UNHOLD, event.getPlayer(), inv.getItem(event.getPreviousSlot()), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        engine.fireItem(Activators.INVENTORY_CLICK, player, event.getCurrentItem(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        engine.fireItem(Activators.INVENTORY_DRAG, player, event.getOldCursor(), event, null);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        engine.fireItem(Activators.OPEN_INVENTORY, player, player.getInventory().getItemInMainHand(), event, null);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        engine.fireItem(Activators.CLOSE_INVENTORY, player, player.getInventory().getItemInMainHand(), event, null);
    }
}
