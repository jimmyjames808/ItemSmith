package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;

/**
 * Drives the passive/timer activators on a fixed interval: {@code hold_tick}
 * (main hand), {@code equip_tick} (each worn armour piece), and
 * {@code inventory_tick} (each distinct item carried anywhere). Only items that
 * declare one of these do any work — the engine's identity check skips the rest,
 * so the per-tick cost is a handful of cheap PDC reads per player. M4 will make
 * the interval and opt-in configurable.
 */
public final class PassiveTask implements Runnable {

    /** Ticks between passive sweeps (20 = once per second). */
    public static final long INTERVAL_TICKS = 20L;

    private final ItemRegistry registry;
    private final AbilityEngine engine;

    public PassiveTask(ItemRegistry registry, AbilityEngine engine) {
        this.registry = registry;
        this.engine = engine;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerInventory inv = player.getInventory();

            engine.fireItem(Activators.HOLD_TICK, player, inv.getItemInMainHand(), null, null);

            engine.fireItem(Activators.EQUIP_TICK, player, inv.getHelmet(), null, null);
            engine.fireItem(Activators.EQUIP_TICK, player, inv.getChestplate(), null, null);
            engine.fireItem(Activators.EQUIP_TICK, player, inv.getLeggings(), null, null);
            engine.fireItem(Activators.EQUIP_TICK, player, inv.getBoots(), null, null);

            Set<String> seen = new HashSet<>();
            for (ItemStack item : inv.getContents()) {
                String id = registry.idOf(item);
                if (id == null || !seen.add(id)) continue;
                engine.fireItem(Activators.INVENTORY_TICK, player, item, null, null);
            }
        }
    }
}
