package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;

/** Fires when a player gains a potion effect or one expires. Item = main hand. */
public final class EffectListener implements Listener {

    private final AbilityEngine engine;

    public EffectListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEffect(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        String activator = switch (event.getAction()) {
            case ADDED, CHANGED -> Activators.PLAYER_RECEIVE_EFFECT;
            case REMOVED, CLEARED -> Activators.PLAYER_EFFECT_EXPIRE;
        };
        engine.fireItem(activator, player, player.getInventory().getItemInMainHand(), event, null);
    }
}
