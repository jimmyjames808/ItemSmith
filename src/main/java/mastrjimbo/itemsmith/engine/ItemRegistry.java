package mastrjimbo.itemsmith.engine;

import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The live set of loaded {@link CustomItem} definitions, keyed by id. Loaded
 * from the per-item YAML store and consulted by the command, recipe manager and
 * listeners. Building a tagged stack is delegated to {@link ItemBuilder}.
 */
public final class ItemRegistry {

    private final ItemBuilder builder;
    private Map<String, CustomItem> items = new LinkedHashMap<>();

    public ItemRegistry(ItemBuilder builder) {
        this.builder = builder;
    }

    /** Replaces the current set with the supplied definitions (preserving order). */
    public void replaceAll(Map<String, CustomItem> loaded) {
        this.items = new LinkedHashMap<>(loaded);
    }

    /** Builds a fresh tagged stack for the id, or null if unknown. */
    public ItemStack build(String id) {
        CustomItem item = items.get(id);
        return item == null ? null : builder.build(item);
    }

    public String idOf(ItemStack stack) {
        return builder.idOf(stack);
    }

    /** Copies a custom item's identity from one stack onto another (e.g. through a bucket transform). */
    public void copyIdentity(ItemStack from, ItemStack to) {
        builder.copyIdentity(from, to);
    }

    // --- Charge counter (M4) — thin delegators so components use ctx.registry() ---

    /** Current charges on a stack, or 0 if it has no counter. */
    public int charges(ItemStack stack) {
        return builder.getCharges(stack);
    }

    /** Sets a stack's charges (clamped to its definition's max), re-rendering lore/bar. */
    public void setCharges(ItemStack stack, int value) {
        CustomItem def = get(idOf(stack));
        if (def != null) builder.setCharges(stack, value, def);
    }

    /** Spends {@code by} charges; returns true if the item depleted to 0 (policy applied). */
    public boolean decrementCharges(ItemStack stack, int by) {
        CustomItem def = get(idOf(stack));
        return def != null && builder.decrementCharges(stack, by, def);
    }

    // --- Persistent stats — thin delegators so components use ctx.registry() ---

    /** A stat's current value on a stack, or "" if unset. */
    public String getStat(ItemStack stack, String name) {
        return builder.getStat(stack, name);
    }

    /** Reads a stat as a number, or {@code fallback} if it's unset or non-numeric. */
    public double getStatNumber(ItemStack stack, String name, double fallback) {
        String v = builder.getStat(stack, name);
        if (v == null || v.isBlank()) return fallback;
        try {
            return Double.parseDouble(v.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /** Writes a stat's value, re-rendering lore. No-op if the stack isn't one of ours. */
    public void setStat(ItemStack stack, String name, String value) {
        CustomItem def = get(idOf(stack));
        if (def != null) builder.setStat(stack, name, value, def);
    }

    /** Adds {@code delta} to a numeric stat (non-numeric reads as 0), storing the result without a trailing {@code .0}. */
    public void addStat(ItemStack stack, String name, double delta) {
        setStat(stack, name, formatNumber(getStatNumber(stack, name, 0) + delta));
    }

    /** Formats a stat number: whole values without a decimal ("3"), otherwise the plain double ("2.5"). */
    public static String formatNumber(double value) {
        return value == Math.rint(value) && !Double.isInfinite(value)
                ? Long.toString((long) value)
                : Double.toString(value);
    }

    public CustomItem get(String id) {
        return items.get(id);
    }

    public Set<String> ids() {
        return items.keySet();
    }

    public Collection<CustomItem> all() {
        return items.values();
    }
}
