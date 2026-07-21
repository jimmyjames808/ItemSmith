package mastrjimbo.itemsmith.listener;

import io.papermc.paper.event.player.PlayerNameEntityEvent;
import io.papermc.paper.event.player.PlayerTradeEvent;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/** Player-to-entity interactions: feeding, breeding, taming, naming, leashing and trading. Item = main hand. */
public final class EntityInteractListener implements Listener {

    private final Plugin plugin;
    private final ItemRegistry registry;
    private final AbilityEngine engine;

    public EntityInteractListener(Plugin plugin, ItemRegistry registry, AbilityEngine engine) {
        this.plugin = plugin;
        this.registry = registry;
        this.engine = engine;
    }

    private void fireMain(String activator, Player player, org.bukkit.event.Event event, Object target) {
        engine.fireItem(activator, player, player.getInventory().getItemInMainHand(), event, target);
    }

    /**
     * Broad "feed" detection: right-clicking an animal with a food item consumes one from the
     * hand. There is no vanilla "fed" event (love-mode only covers adults), so we snapshot the
     * held item and, one tick later, fire {@code feed_entity} if exactly that item was eaten —
     * i.e. same type, fewer of them (or the last one gone). A type change (e.g. bucket → milk
     * bucket) is not a feed and is ignored. Note: creative mode doesn't consume items, so it
     * can't be detected there.
     */
    @EventHandler(ignoreCancelled = true)
    public void onFeed(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof Animals animal)) return;

        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        if (held == null || held.getType().isAir() || registry.idOf(held) == null) return;

        ItemStack fed = held.clone();
        int beforeAmount = fed.getAmount();
        Material beforeType = fed.getType();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            ItemStack after = player.getInventory().getItemInMainHand();
            boolean eaten = after == null || after.getType().isAir()
                    || (after.getType() == beforeType && after.getAmount() < beforeAmount);
            if (eaten) {
                engine.fireItem(Activators.FEED_ENTITY, player, fed, null, animal);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreed(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player player)) return;
        fireMain(Activators.BREED_ENTITY, player, event, event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player player)) return;
        fireMain(Activators.TAME_ENTITY, player, event, event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onName(PlayerNameEntityEvent event) {
        fireMain(Activators.NAME_ENTITY, event.getPlayer(), event, event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeash(PlayerLeashEntityEvent event) {
        fireMain(Activators.LEASH_ENTITY, event.getPlayer(), event, event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTrade(PlayerTradeEvent event) {
        fireMain(Activators.TRADE, event.getPlayer(), event, event.getVillager());
    }
}
