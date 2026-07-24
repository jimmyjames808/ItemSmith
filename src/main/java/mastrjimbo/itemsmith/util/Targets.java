package mastrjimbo.itemsmith.util;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.param.ParamValues;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Locale;

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

    /**
     * The center point for an area targeter, honoring a {@code relative_to: self|target} param.
     * {@code self} (the default) is the caster's location; {@code target} is the trigger's target
     * (e.g. a projectile's impact point), and is {@code null} when the trigger had no target so the
     * caller can return no results. Mirrors the pattern already used by {@code ring} / {@code offset}.
     */
    public static Location center(AbilityContext ctx, ParamValues params) {
        String relativeTo = params.getString("relative_to", "self").trim().toLowerCase(Locale.ROOT);
        return relativeTo.equals("target") ? location(ctx.eventTarget()) : ctx.player().getLocation();
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
