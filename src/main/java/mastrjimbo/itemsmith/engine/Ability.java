package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.gate.Gate;
import mastrjimbo.itemsmith.param.ParamValues;

import java.util.List;

/**
 * One trigger-to-effects rule on an item: when {@code activatorId} fires, if
 * every condition passes and its {@link Gate} allows (permission / region / cost /
 * charges / cooldown group), resolve targets with {@code targeter} and run each
 * action against each target. An item may carry many abilities (unlike the free
 * tier of the competitors, which caps them).
 *
 * <p>{@code cooldownSeconds} is this ability's own reuse cooldown (0 = none). Each ability is gated
 * independently on a per-player native cooldown group; firing it shows the vanilla grey-out sweep on
 * the item (best-effort, since a stack has one visual). This is distinct from the {@link Gate}'s
 * shared, named {@code cooldown-group}, which coordinates cooldowns across different items.
 */
public record Ability(
        String activatorId,
        ParamValues activatorParams,
        List<Configured<Condition>> conditions,
        Configured<Targeter> targeter,
        List<ActionNode> actions,
        Gate gate,
        double cooldownSeconds
) {
}
