package mastrjimbo.itemsmith.engine;

import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;

/**
 * Thin wrapper over Paper's native key-based item-cooldown API (1.21.8). Each ability drives its own
 * cooldown group key, so abilities are gated independently and per-player. The item also carries a
 * per-item visual group ({@link ItemBuilder} sets its {@code UseCooldownComponent}) so a fired ability
 * shows the vanilla grey-out sweep on that item — best-effort, since a stack has only one visual.
 */
public final class CooldownManager {

    /** True if no cooldown is currently active for {@code group} on this player. */
    public boolean ready(Player player, Key group) {
        return player.getCooldown(group) <= 0;
    }

    /** Starts a {@code ticks}-long cooldown for {@code group} on this player (no-op for ticks ≤ 0). */
    public void trigger(Player player, Key group, int ticks) {
        if (ticks > 0) {
            player.setCooldown(group, ticks);
        }
    }
}
