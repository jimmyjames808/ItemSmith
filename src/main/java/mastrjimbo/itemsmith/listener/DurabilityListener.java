package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;

/** Item durability triggers: losing durability, breaking, and Mending repair. */
public final class DurabilityListener implements Listener {

    private final AbilityEngine engine;

    public DurabilityListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(PlayerItemDamageEvent event) {
        engine.fireItem(Activators.ITEM_DURABILITY_DAMAGE, event.getPlayer(), event.getItem(), event, null);
    }

    @EventHandler
    public void onBreak(PlayerItemBreakEvent event) {
        engine.fireItem(Activators.ITEM_BREAK, event.getPlayer(), event.getBrokenItem(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMend(PlayerItemMendEvent event) {
        engine.fireItem(Activators.ITEM_MEND, event.getPlayer(), event.getItem(), event, null);
    }
}
