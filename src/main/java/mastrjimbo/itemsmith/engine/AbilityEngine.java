package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.gate.GateCheck;
import mastrjimbo.itemsmith.gate.GateEvaluator;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Runs the ability pipeline. A listener resolves the relevant custom item and
 * calls {@link #fireItem}; the engine then, for every ability bound to that
 * activator, gates on cooldown, checks conditions, resolves targets and runs
 * actions. This is the one place the Activator to Conditions to Targeter to
 * Actions flow is expressed, so every activator/action reuses it as-is.
 */
public final class AbilityEngine {

    private final Plugin plugin;
    private final ItemRegistry registry;
    private final CooldownManager cooldowns;
    private final GateEvaluator gates;
    private final Logger logger;
    private final ActionExecutor executor;
    /** Players whose ability is currently executing (main thread) — the re-entrancy guard. */
    private final Set<UUID> firing = new HashSet<>();

    public AbilityEngine(Plugin plugin, ItemRegistry registry, CooldownManager cooldowns,
                         GateEvaluator gates, Logger logger) {
        this.plugin = plugin;
        this.registry = registry;
        this.cooldowns = cooldowns;
        this.gates = gates;
        this.logger = logger;
        this.executor = new ActionExecutor(logger);
    }

    /**
     * Convenience entry point for listeners: if {@code item} is one of ours,
     * build a context and fire {@code activatorId}. A no-op for null/foreign items.
     *
     * @param item   the item that triggered the event (used for identity + cooldown);
     *               may be a loose stack not in the player's inventory
     * @param target the trigger's natural target (entity/block), or null
     */
    public void fireItem(String activatorId, Player player, ItemStack item, Event event, Object target) {
        if (item == null) return;
        String id = registry.idOf(item);
        if (id == null) return;
        fire(activatorId, new AbilityContext(plugin, player, item, id, event, target, registry));
    }

    /**
     * Fires all abilities on the context's item bound to {@code activatorId}.
     * Safe to call for any event; if the item has no matching ability it is a no-op.
     */
    public void fire(String activatorId, AbilityContext ctx) {
        CustomItem item = registry.get(ctx.itemId());
        if (item == null) return;

        // Re-entrancy guard: while an ability's actions run, their side effects — e.g. a damage /
        // damage_nearby action that deals damage attributed to the caster — must NOT re-trigger this
        // (or any) ability for the same player, or player_hit_entity would loop back on itself.
        UUID uuid = ctx.player().getUniqueId();
        if (!firing.add(uuid)) return;
        try {
            List<Ability> abilities = item.abilities();
            for (int i = 0; i < abilities.size(); i++) {
                Ability ability = abilities.get(i);
                if (!ability.activatorId().equals(activatorId)) continue;

                // Per-ability cooldown gate: each ability has its own native cooldown group key, so
                // abilities on the same item are throttled independently and per-player.
                NamespacedKey cooldownKey = ability.cooldownSeconds() > 0
                        ? new NamespacedKey(plugin, "cd_" + ctx.itemId() + "_a" + i) : null;
                if (cooldownKey != null && !cooldowns.ready(ctx.player(), cooldownKey)) continue;

                // Ability-level gate sees the trigger's natural target (pre-resolution; may be null).
                if (!Conditions.allPass(ability.conditions(), ctx, ctx.eventTarget(), logger)) continue;

                // M4 gate: verify permission/region/cooldown-group/charges/cost with NO mutation...
                GateCheck check = gates.check(ability.gate(), ctx);
                if (!check.passed()) {
                    gates.notifyDenied(ability.gate(), ctx, check); // optional deny message; silent by default
                    continue;                                        // ability skipped, nothing consumed
                }
                // ...then commit every cost/charge/group-cooldown now, at start (async-safe).
                gates.commit(ability.gate(), ctx);

                List<Object> targets = resolveTargets(ability, ctx);
                for (Object target : targets) {
                    // The executor runs the action tree (leaf actions + delay/repeat/if/random flow).
                    // It may finish asynchronously (delays), but the ability has "fired" now.
                    executor.run(ability.actions(), ctx, target, null);
                }

                // Start this ability's cooldown at fire-start, and best-effort the item's client sweep
                // (the per-item visual group ItemBuilder set — only one ability can animate at a time).
                if (cooldownKey != null) {
                    int ticks = (int) Math.round(ability.cooldownSeconds() * 20);
                    cooldowns.trigger(ctx.player(), cooldownKey, ticks);
                    cooldowns.trigger(ctx.player(), new NamespacedKey(plugin, "cd_" + ctx.itemId()), ticks);
                }
            }
        } finally {
            firing.remove(uuid);
        }
    }

    private List<Object> resolveTargets(Ability ability, AbilityContext ctx) {
        Configured<Targeter> t = ability.targeter();
        try {
            return t.definition().resolve(ctx, t.params());
        } catch (RuntimeException e) {
            logger.log(Level.WARNING, "Targeter '" + t.definition().id() + "' on item '"
                    + ctx.itemId() + "' threw; no targets.", e);
            return List.of();
        }
    }

}
