package mastrjimbo.itemsmith.util;

import org.bukkit.plugin.Plugin;

/**
 * Schedules the "revert" half of a temporary effect — timed glow, a block that
 * auto-reverts, a short-lived attribute modifier — a number of ticks later.
 * Actions that grant something temporary apply it now and register the undo
 * here, so no engine-level flow control is needed for self-reverting effects.
 */
public final class TempTasks {

    private TempTasks() {
    }

    public static void later(Plugin plugin, long ticks, Runnable revert) {
        plugin.getServer().getScheduler().runTaskLater(plugin, revert, Math.max(1L, ticks));
    }
}
