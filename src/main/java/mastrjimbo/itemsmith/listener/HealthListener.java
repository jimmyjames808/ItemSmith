package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.PlayerInventory;

/** Health / XP / food / respawn / totem triggers. Item = main hand (or a held totem). */
public final class HealthListener implements Listener {

    private final AbilityEngine engine;

    public HealthListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        engine.fireItem(Activators.PLAYER_RESPAWN, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRegain(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        engine.fireItem(Activators.REGAIN_HEALTH, player, player.getInventory().getItemInMainHand(), event, null);
    }

    @EventHandler
    public void onXp(PlayerExpChangeEvent event) {
        engine.fireItem(Activators.EXPERIENCE_CHANGE, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, null);
    }

    @EventHandler
    public void onLevel(PlayerLevelChangeEvent event) {
        String activator = event.getNewLevel() > event.getOldLevel() ? Activators.LEVEL_UP : Activators.LEVEL_DOWN;
        engine.fireItem(activator, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        engine.fireItem(Activators.FOOD_CHANGE, player, player.getInventory().getItemInMainHand(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTotem(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        PlayerInventory inv = player.getInventory();
        engine.fireItem(Activators.TOTEM_USE, player, inv.getItemInMainHand(), event, null);
        engine.fireItem(Activators.TOTEM_USE, player, inv.getItemInOffHand(), event, null);
    }
}
