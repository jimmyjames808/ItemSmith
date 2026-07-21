package mastrjimbo.itemsmith.listener;

import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/** Item-use family: finishing a consume, releasing a charging use (bow/food), and editing a book. */
public final class ItemUseListener implements Listener {

    private final AbilityEngine engine;

    public ItemUseListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        engine.fireItem(Activators.ITEM_CONSUME, event.getPlayer(), event.getItem(), event, null);
    }

    @EventHandler
    public void onStopUsing(PlayerStopUsingItemEvent event) {
        // This event also fires on normal completion (fully eating/drawing); only fire our trigger
        // when the use was actually interrupted early, so it doesn't double up with item_consume etc.
        int maxUse = event.getItem().getMaxItemUseDuration(event.getPlayer());
        if (maxUse > 0 && event.getTicksHeldFor() >= maxUse - 1) return;
        engine.fireItem(Activators.STOP_USING_ITEM, event.getPlayer(), event.getItem(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBookEdit(PlayerEditBookEvent event) {
        String activator = event.isSigning() ? Activators.BOOK_SIGN : Activators.BOOK_EDIT;
        engine.fireItem(activator, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, null);
    }
}
