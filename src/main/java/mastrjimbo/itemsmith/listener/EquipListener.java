package mastrjimbo.itemsmith.listener;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

/**
 * The equipment event family. Uses Paper's armour-change event to fire
 * {@code equip} for a newly worn custom item and {@code unequip} for one taken
 * off.
 *
 * <p>Important subtlety: this event also fires when worn armour merely changes in
 * place — most commonly a durability tick when the wearer is hit — where the old
 * and new stacks are the same logical item. We compare the two sides' ItemSmith
 * ids and fire only on a genuine identity change, so taking damage no longer
 * spuriously fires equip/unequip.
 */
public final class EquipListener implements Listener {

    private final ItemRegistry registry;
    private final AbilityEngine engine;

    public EquipListener(ItemRegistry registry, AbilityEngine engine) {
        this.registry = registry;
        this.engine = engine;
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        String oldId = registry.idOf(event.getOldItem());
        String newId = registry.idOf(event.getNewItem());
        if (Objects.equals(oldId, newId)) return; // same item modified in place (e.g. durability) — not a real (un)equip

        if (newId != null) {
            engine.fireItem(Activators.EQUIP, event.getPlayer(), event.getNewItem(), event, null);
        }
        if (oldId != null) {
            engine.fireItem(Activators.UNEQUIP, event.getPlayer(), event.getOldItem(), event, null);
        }
    }
}
