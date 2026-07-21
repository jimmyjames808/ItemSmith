package mastrjimbo.itemsmith.integration;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

/**
 * Null-object adapter over land-protection plugins (WorldGuard for regions +
 * build flags; GriefPrevention for claim build-checks, best-effort via reflection
 * so it needs no compile dependency).
 *
 * <p>Deliberate fail-direction asymmetry:
 * <ul>
 *   <li>{@link #canBuild} <b>fails OPEN</b> (allow) when no protection plugin is present —
 *       there's nothing to enforce, so nothing is blocked.</li>
 *   <li>{@link #isInRegion}/{@link #isRegionMember} <b>fail CLOSED</b> (deny) when WorldGuard
 *       is absent — a region-locked ability can't be verified, so it must not silently
 *       become free.</li>
 * </ul>
 */
public final class ProtectionHook {

    private final WorldGuardBridge worldGuard; // null when WorldGuard absent
    private final boolean griefPrevention;

    private ProtectionHook(WorldGuardBridge worldGuard, boolean griefPrevention) {
        this.worldGuard = worldGuard;
        this.griefPrevention = griefPrevention;
    }

    public static ProtectionHook detect(Plugin plugin) {
        WorldGuardBridge wg = null;
        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            try {
                wg = new WorldGuardBridge();
            } catch (Throwable t) {
                plugin.getLogger().warning("WorldGuard present but failed to hook: " + t.getMessage());
                wg = null;
            }
        }
        boolean gp = plugin.getServer().getPluginManager().getPlugin("GriefPrevention") != null;
        return new ProtectionHook(wg, gp);
    }

    public boolean hasWorldGuard() {
        return worldGuard != null;
    }

    public boolean hasGriefPrevention() {
        return griefPrevention;
    }

    /** May the player build/modify at {@code loc}? Allows when no protection plugin objects. */
    public boolean canBuild(Player player, Location loc) {
        if (worldGuard != null) {
            try {
                if (!worldGuard.canBuild(player, loc)) return false;
            } catch (Throwable ignored) {
                // treat a hook failure as "no objection" (fail open for build)
            }
        }
        if (griefPrevention) {
            try {
                if (!griefPreventionAllowsBuild(player, loc)) return false;
            } catch (Throwable ignored) {
                // best-effort; ignore reflection/version issues
            }
        }
        return true;
    }

    public boolean isInRegion(Player player, String regionName) {
        if (worldGuard == null || regionName == null || regionName.isEmpty()) return false;
        try {
            return worldGuard.isInRegion(player, regionName);
        } catch (Throwable t) {
            return false;
        }
    }

    public boolean isRegionMember(Player player, String regionName) {
        if (worldGuard == null || regionName == null || regionName.isEmpty()) return false;
        try {
            return worldGuard.isMember(player, regionName);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * GriefPrevention build-check via reflection: {@code GriefPrevention.instance.allowBuild(player, loc)}
     * returns null when allowed, a reason String when denied. No compile-time dependency on GP.
     */
    private boolean griefPreventionAllowsBuild(Player player, Location loc) throws Exception {
        Class<?> gp = Class.forName("me.ryanhamshire.GriefPrevention.GriefPrevention");
        Object instance = gp.getField("instance").get(null);
        if (instance == null) return true;
        Method allowBuild = gp.getMethod("allowBuild", Player.class, Location.class);
        Object result = allowBuild.invoke(instance, player, loc);
        return result == null;
    }
}
