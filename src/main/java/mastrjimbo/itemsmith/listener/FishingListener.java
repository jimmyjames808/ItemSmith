package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

/** The full fishing lifecycle: cast, bite, catch (fish or entity), bobber-in-ground, fail and reel. */
public final class FishingListener implements Listener {

    private final AbilityEngine engine;

    public FishingListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        String activator = switch (event.getState()) {
            case FISHING -> Activators.FISH_CAST;
            case BITE -> Activators.FISH_BITE;
            case CAUGHT_FISH -> Activators.FISH_CATCH;
            case CAUGHT_ENTITY -> Activators.FISH_CATCH_ENTITY;
            case IN_GROUND -> Activators.FISH_IN_GROUND;
            case REEL_IN -> Activators.FISH_REEL;
            default -> null; // FAILED_ATTEMPT (too finicky to be useful), LURED, and any future states
        };
        if (activator == null) return;
        ItemStack rod = event.getPlayer().getInventory().getItemInMainHand();
        Object target = event.getCaught() != null ? event.getCaught() : event.getHook();
        engine.fireItem(activator, event.getPlayer(), rod, event, target);
    }
}
