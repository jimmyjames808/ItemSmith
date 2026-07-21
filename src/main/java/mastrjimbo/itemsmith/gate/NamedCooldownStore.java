package mastrjimbo.itemsmith.gate;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A shared, logical cooldown store keyed by an arbitrary name, per player. Distinct
 * from {@link mastrjimbo.itemsmith.engine.CooldownManager} (which drives the native
 * per-item client cooldown sweep): a named group can be <em>shared across different
 * items</em>, so e.g. every "teleport" item can sit on one {@code teleport} cooldown.
 *
 * <p>Backs the ability gate's {@code cooldown-group}, the {@code set_cooldown} action
 * and the {@code cooldown_ready} condition. In-memory only — cleared on restart
 * (acceptable for cooldowns; a future milestone can persist it).
 */
public final class NamedCooldownStore {

    private final Map<UUID, Map<String, Long>> readyAt = new HashMap<>();

    /** True if the player has no active cooldown under {@code key} (or it has elapsed). */
    public boolean ready(Player player, String key) {
        Map<String, Long> m = readyAt.get(player.getUniqueId());
        if (m == null) return true;
        Long at = m.get(key);
        return at == null || System.currentTimeMillis() >= at;
    }

    /** Milliseconds until {@code key} is ready again, or 0 if already ready. */
    public long remainingMillis(Player player, String key) {
        Map<String, Long> m = readyAt.get(player.getUniqueId());
        if (m == null) return 0;
        Long at = m.get(key);
        if (at == null) return 0;
        return Math.max(0, at - System.currentTimeMillis());
    }

    /** Arms the cooldown: {@code key} becomes unready for {@code seconds}. */
    public void trigger(Player player, String key, double seconds) {
        if (key == null || key.isEmpty() || seconds <= 0) return;
        readyAt.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(key, System.currentTimeMillis() + (long) (seconds * 1000));
    }

    public void clear(Player player, String key) {
        Map<String, Long> m = readyAt.get(player.getUniqueId());
        if (m != null) m.remove(key);
    }

    /** Drops all cooldowns for a player (e.g. on quit, to avoid a slow leak). */
    public void clearPlayer(UUID id) {
        readyAt.remove(id);
    }
}
