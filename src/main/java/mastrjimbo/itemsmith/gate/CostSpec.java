package mastrjimbo.itemsmith.gate;

import org.bukkit.Material;

import java.util.List;

/**
 * The per-use price of an ability: any combination of Vault money, XP (levels or
 * raw points), hunger/saturation, and consumed item ingredients. Every field is
 * checked before the ability runs and consumed atomically (all-or-nothing) once
 * it does — see {@link GateEvaluator}.
 */
public record CostSpec(
        double money,
        int xpLevels,
        int xpPoints,
        double hunger,
        List<ItemCost> items
) {
    public static final CostSpec NONE = new CostSpec(0, 0, 0, 0, List.of());

    /** One ingredient requirement: {@code amount} of {@code material} in the caster's inventory. */
    public record ItemCost(Material material, int amount) {
    }

    public boolean isNone() {
        return money <= 0 && xpLevels <= 0 && xpPoints <= 0 && hunger <= 0 && items.isEmpty();
    }
}
