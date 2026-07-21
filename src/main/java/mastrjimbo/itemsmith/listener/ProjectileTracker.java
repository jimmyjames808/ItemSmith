package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Watches fired custom-item projectiles each tick and fires
 * {@code projectile_enter_liquid} the moment one first crosses into a water/lava
 * block. Projectiles pass through liquids rather than "hitting" them, so a
 * per-tick check is the only dependable way to detect entry — the hit event
 * can't. Only projectiles stamped with our id are tracked, so the cost is
 * proportional to in-flight custom projectiles (usually zero).
 */
public final class ProjectileTracker implements Runnable {

    /** Stop tracking a projectile after this many server ticks as a safety net. */
    private static final int MAX_AGE_TICKS = 600;

    private final Plugin plugin;
    private final AbilityEngine engine;
    private final ItemRegistry registry;
    private final NamespacedKey shotKey;
    private final Set<Projectile> tracked = new HashSet<>();

    public ProjectileTracker(Plugin plugin, ItemRegistry registry, AbilityEngine engine) {
        this.plugin = plugin;
        this.registry = registry;
        this.engine = engine;
        this.shotKey = new NamespacedKey(plugin, "shot_item");
    }

    public void track(Projectile projectile) {
        tracked.add(projectile);
    }

    public void untrack(Projectile projectile) {
        tracked.remove(projectile);
    }

    @Override
    public void run() {
        if (tracked.isEmpty()) return;
        Iterator<Projectile> it = tracked.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            if (p == null || p.isDead() || !p.isValid() || p.getTicksLived() > MAX_AGE_TICKS) {
                it.remove();
                continue;
            }
            Block block = p.getLocation().getBlock();
            if (block.isLiquid()) {
                fireEnterLiquid(p);
                it.remove();
            }
        }
    }

    private void fireEnterLiquid(Projectile p) {
        String id = p.getPersistentDataContainer().get(shotKey, PersistentDataType.STRING);
        if (id == null) return;
        if (!(p.getShooter() instanceof Player player)) return;
        engine.fire(Activators.PROJECTILE_ENTER_LIQUID, new AbilityContext(plugin, player, null, id, null, p, registry));
    }
}
