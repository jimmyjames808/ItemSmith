package mastrjimbo.itemsmith.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Wraps action-inflicted damage so the combat listener can tell it apart from a real player hit.
 * While {@link #deal} is on the stack, {@link #inProgress()} is true and CombatListener skips the
 * attacker's hit abilities — killing both the synchronous loop AND the async (delayed) chain the
 * per-fire re-entrancy guard can't catch. Main-thread only, so a plain counter is safe.
 */
public final class ActionDamage {
    private static int depth = 0;
    private ActionDamage() {}

    public static void deal(LivingEntity target, double amount, Entity source) {
        depth++;
        try {
            target.damage(amount, source);
        } finally {
            depth--;
        }
    }

    public static boolean inProgress() {
        return depth > 0;
    }
}