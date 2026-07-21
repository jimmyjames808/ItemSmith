package mastrjimbo.itemsmith.engine;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Everything a running ability pipeline needs: who triggered it, with which
 * item, the raw Bukkit event that fired it, the trigger's natural target, and a
 * scratch variable map that actions can read/write to pass data down the chain.
 *
 * <p>Built fresh per activation by a listener, then handed to conditions,
 * targeters and actions unchanged.
 */
public final class AbilityContext {

    private final Plugin plugin;
    private final Player player;
    private final ItemStack itemStack;
    private final String itemId;
    private final Event event;
    private final Object eventTarget;
    private final ItemRegistry registry;
    private final Map<String, Object> variables = new HashMap<>();

    public AbilityContext(Plugin plugin, Player player, ItemStack itemStack, String itemId,
                          Event event, Object eventTarget, ItemRegistry registry) {
        this.plugin = plugin;
        this.player = player;
        this.itemStack = itemStack;
        this.itemId = itemId;
        this.event = event;
        this.eventTarget = eventTarget;
        this.registry = registry;
    }

    public Plugin plugin() {
        return plugin;
    }

    /** The player who triggered the ability (the caster). */
    public Player player() {
        return player;
    }

    /** The exact item stack that triggered it (identity-tagged, used for native cooldown). */
    public ItemStack itemStack() {
        return itemStack;
    }

    public String itemId() {
        return itemId;
    }

    /** The raw Bukkit event, or null if the activator wasn't event-driven (e.g. a tick). */
    public Event event() {
        return event;
    }

    /** The trigger's natural target (e.g. the entity hit); may be null. */
    public Object eventTarget() {
        return eventTarget;
    }

    /** The natural target as a LivingEntity, or null if it isn't one. */
    public LivingEntity targetLiving() {
        return eventTarget instanceof LivingEntity living ? living : null;
    }

    /** The live item registry — for actions that build/give other ItemSmith items (e.g. give_custom_item). */
    public ItemRegistry registry() {
        return registry;
    }

    public Map<String, Object> variables() {
        return variables;
    }
}
