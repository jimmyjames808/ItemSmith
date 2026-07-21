package mastrjimbo.itemsmith.util;

import org.bukkit.entity.Player;

/**
 * Experience-points math. Bukkit exposes level + progress-to-next but no reliable
 * running total ({@code getTotalExperience} drifts), so we compute the true total
 * from the vanilla level curve. Used for raw-XP-point use-costs; XP-<em>level</em>
 * costs need none of this (they read {@link Player#getLevel()} directly).
 */
public final class Xp {

    private Xp() {
    }

    /** Total experience points the player currently holds. */
    public static int total(Player player) {
        int level = player.getLevel();
        return expToReach(level) + Math.round(player.getExp() * expToNext(level));
    }

    /** Points required to advance from {@code level} to {@code level + 1}. */
    public static int expToNext(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    /** Total points accumulated to first reach {@code level} from 0. */
    public static int expToReach(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int) (2.5 * level * level - 40.5 * level + 360);
        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }
}
