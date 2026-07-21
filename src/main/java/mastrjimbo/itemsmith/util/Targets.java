package mastrjimbo.itemsmith.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Uniformly narrows the raw {@code Object} target the engine hands an action
 * (which may be an {@link Entity}, {@link Block} or {@link Location}) to the kind
 * an action needs. Every getter returns null when the target isn't that kind, so
 * actions can guard with a single null check instead of repeating instanceof
 * chains — the established "no-op on inapplicable target" convention.
 */
public final class Targets {

    private Targets() {
    }

    /** A location for the target: its own if a Location, else the entity/block position; null if none. */
    public static Location location(Object target) {
        if (target instanceof Location l) return l;
        if (target instanceof Entity e) return e.getLocation();
        if (target instanceof Block b) return b.getLocation();
        return null;
    }

    public static Entity entity(Object target) {
        return target instanceof Entity e ? e : null;
    }

    public static LivingEntity living(Object target) {
        return target instanceof LivingEntity l ? l : null;
    }

    public static Player player(Object target) {
        return target instanceof Player p ? p : null;
    }

    public static Block block(Object target) {
        return target instanceof Block b ? b : null;
    }
}
