package mastrjimbo.itemsmith.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * Cancels blast damage from cosmetic fireworks spawned by the {@code firework_effect} /
 * {@code spawn_firework} actions. Those actions tag their firework in its PDC; a firework-star
 * detonation otherwise deals damage (and bypasses invulnerability frames when the hit is larger),
 * so the only reliable way to keep the effect purely visual is to veto its damage event here.
 */
public final class FireworkListener implements Listener {

    /** PDC key an ItemSmith cosmetic firework is stamped with. Must match the firework actions. */
    public static final String COSMETIC_KEY = "cosmetic_firework";

    private final NamespacedKey cosmeticKey;

    public FireworkListener(Plugin plugin) {
        this.cosmeticKey = new NamespacedKey(plugin, COSMETIC_KEY);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework fw
                && fw.getPersistentDataContainer().has(cosmeticKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);
        }
    }
}
