package mastrjimbo.itemsmith.integration;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Isolates every WorldGuard/WorldEdit class reference. It is instantiated by
 * {@link ProtectionHook} <em>only after</em> WorldGuard is detected, so the JVM
 * never links these types when the plugin is absent (no {@code NoClassDefFoundError}).
 * Package-private on purpose — nothing outside {@link ProtectionHook} touches it.
 */
final class WorldGuardBridge {

    private RegionQuery query() {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
    }

    private LocalPlayer wrap(Player player) {
        return WorldGuardPlugin.inst().wrapPlayer(player);
    }

    boolean canBuild(Player player, Location loc) {
        LocalPlayer lp = wrap(player);
        // Respect WorldGuard's bypass (ops / worldguard.region.bypass.*): a bypassing player can build
        // anywhere, so testState — which only reports the raw region member-rule — must not override it.
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(loc.getWorld());
        if (WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(lp, weWorld)) {
            return true;
        }
        com.sk89q.worldedit.util.Location wl = BukkitAdapter.adapt(loc);
        return query().testState(wl, lp, Flags.BUILD);
    }

    boolean isInRegion(Player player, String regionName) {
        ApplicableRegionSet set = query().getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        for (ProtectedRegion r : set) {
            if (r.getId().equalsIgnoreCase(regionName)) return true;
        }
        return false;
    }

    boolean isMember(Player player, String regionName) {
        LocalPlayer lp = wrap(player);
        ApplicableRegionSet set = query().getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
        for (ProtectedRegion r : set) {
            if (r.getId().equalsIgnoreCase(regionName)) {
                return r.isMember(lp) || r.isOwner(lp);
            }
        }
        return false;
    }
}
