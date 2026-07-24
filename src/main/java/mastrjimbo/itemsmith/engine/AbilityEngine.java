package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.gate.GateCheck;
import mastrjimbo.itemsmith.gate.GateEvaluator;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Activators;
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
     * Runs the {@code stat_reached} hook for a rising crossing of {@code stat} on {@code stack}, from
     * {@code oldValue} to {@code newValue}. Called by the {@code set_stat}/{@code add_stat} actions
     * right after they change a stat; a no-op unless the change actually rose and some
     * {@code stat_reached} ability's threshold sits in {@code (oldValue, newValue]}.
     *
     * <p><b>Re-entrancy:</b> the stat change happens <i>inside</i> an already-firing ability, so the
     * player's uuid is in {@link #firing} and an immediate re-fire would be swallowed by the guard.
     * We therefore defer the hook to the next server tick, when the originating ability has finished
     * and the guard is clear. The pre/post values are captured now and carried into the deferred fire,
     * so the one-shot crossing is decided from the values at change time — never re-read from the item
     * (which may have changed again by next tick). This keeps the hook a clean rising-edge trigger.
     */
    public void fireStatReached(Player player, ItemStack stack, String stat, double oldValue, double newValue) {
        if (stack == null || newValue <= oldValue) return; // only an upward change can cross a threshold
        String id = registry.idOf(stack);
        if (id == null) return;
        CustomItem item = registry.get(id);
        if (item == null) return;

        StatCrossing crossing = new StatCrossing(stat, oldValue, newValue);
        // Skip scheduling entirely unless at least one stat_reached ability actually matches this crossing.
        boolean matched = false;
        for (Ability ability : item.abilities()) {
            if (ability.activatorId().equals(Activators.STAT_REACHED) && crossing.matches(ability.activatorParams())) {
                matched = true;
                break;
            }
        }
        if (!matched) return;

        plugin.getServer().getScheduler().runTask(plugin, () -> fire(Activators.STAT_REACHED,
                new AbilityContext(plugin, player, stack, id, null, null, registry), crossing));
    }

    /**
     * Fires all abilities on the context's item bound to {@code activatorId}.
     * Safe to call for any event; if the item has no matching ability it is a no-op.
     */
    public void fire(String activatorId, AbilityContext ctx) {
        fire(activatorId, ctx, null);
    }

    /**
     * Fires matching abilities. When {@code crossing} is non-null this is a {@code stat_reached}
     * activation and each candidate ability is additionally filtered so only those whose
     * {@code stat}+{@code value} params match the rising crossing run (see {@link StatCrossing}).
     */
    private void fire(String activatorId, AbilityContext ctx, StatCrossing crossing) {
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
                // stat_reached: run only the abilities whose stat+value threshold this change crossed.
                if (crossing != null && !crossing.matches(ability.activatorParams())) continue;
                final int abilityIndex = i; // captured for the deferred FINE log below

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
                // Resolve targets BEFORE committing anything: an ability whose targeter finds no one
                // must not consume cost or start its cooldown. Charging for a no-op is undiagnosable
                // ("nothing happened but it's on cooldown"), so treat empty as "did not fire".
                List<Object> targets = resolveTargets(ability, ctx);
                if (targets.isEmpty()) {
                    logger.log(Level.FINE, () -> "Ability #" + abilityIndex + " on '" + ctx.itemId()
                            + "' resolved no targets; skipped (no cost, no cooldown).");
                    continue;
                }

                // ...then commit every cost/charge/group-cooldown now, at start (async-safe).
                gates.commit(ability.gate(), ctx);

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

    /**
     * A single rising stat crossing: the stat changed from {@code oldValue} to {@code newValue}. Used
     * to select the {@code stat_reached} abilities to run — one matches when it watches this stat and
     * its threshold sits in {@code (oldValue, newValue]}, i.e. the value was below it and is now at or
     * above it. This is what makes the hook a once-per-crossing rising edge rather than a level check.
     */
    private record StatCrossing(String stat, double oldValue, double newValue) {
        boolean matches(ParamValues activatorParams) {
            if (!stat.equalsIgnoreCase(activatorParams.getString("stat", "stat").trim())) return false;
            double threshold = activatorParams.getDouble("value", 0);
            return oldValue < threshold && newValue >= threshold;
        }
    }

}
