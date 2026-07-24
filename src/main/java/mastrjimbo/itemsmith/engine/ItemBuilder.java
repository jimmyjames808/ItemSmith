package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.gate.DepletionPolicy;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Turns a {@link CustomItem} definition into a live, identity-tagged
 * {@link ItemStack}, and reads that identity back off a stack. The identity is a
 * PersistentDataContainer string that survives inventories, drops and restarts —
 * the robust way to recognise our items at runtime.
 *
 * <p>M4 adds a persistent charge counter (two PDC ints, {@code charges}/{@code max_charges})
 * with read/write/decrement helpers, a depletion policy, and lore that re-renders
 * {@code <charges>}/{@code <max_charges>} tokens from the immutable definition on every
 * change so the displayed count can't drift.
 */
public final class ItemBuilder {

    private final Plugin plugin;
    private final NamespacedKey idKey;
    private final NamespacedKey chargesKey;
    private final NamespacedKey maxChargesKey;

    public ItemBuilder(Plugin plugin) {
        this.plugin = plugin;
        this.idKey = new NamespacedKey(plugin, "item_id");
        this.chargesKey = new NamespacedKey(plugin, "charges");
        this.maxChargesKey = new NamespacedKey(plugin, "max_charges");
    }

    @SuppressWarnings("deprecation") // setCustomModelData(Integer) is the intentional legacy-pack fallback
    public ItemStack build(CustomItem item) {
        ItemStack stack = new ItemStack(item.material());
        ItemMeta meta = stack.getItemMeta();

        if (item.itemModel() != null) {
            meta.setItemModel(item.itemModel());
        }
        if (item.customModelData() != null) {
            meta.setCustomModelData(item.customModelData());
        }
        if (item.name() != null && !item.name().isBlank()) {
            meta.displayName(Text.item(item.name()));
        }
        boolean hasStats = item.stats() != null && !item.stats().isEmpty();
        if ((item.lore() != null && !item.lore().isEmpty()) || item.charges() != null || hasStats) {
            meta.lore(renderLore(item, item.charges(), item.stats()));
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(idKey, PersistentDataType.STRING, item.id());

        if (item.charges() != null) {
            int max = item.maxCharges() != null ? item.maxCharges() : item.charges();
            pdc.set(chargesKey, PersistentDataType.INTEGER, item.charges());
            pdc.set(maxChargesKey, PersistentDataType.INTEGER, max);
            reflectDurabilityBar(item, meta, item.charges(), max);
        }

        // Seed persistent stats with their declared initial values. From here they live on this
        // physical stack and only the set_stat/add_stat actions change them.
        if (item.stats() != null) {
            item.stats().forEach((name, value) -> pdc.set(statKey(name), PersistentDataType.STRING, value));
        }

        // Cooldowns are per-ability now, but the client sweep is per-item: give the item its own
        // cooldown group so a fired ability greys out only this item (not all of its base material).
        // The engine drives the actual per-fire duration via player.setCooldown at runtime.
        double maxCooldown = 0;
        for (Ability ability : item.abilities()) {
            maxCooldown = Math.max(maxCooldown, ability.cooldownSeconds());
        }
        if (maxCooldown > 0) {
            UseCooldownComponent cooldown = meta.getUseCooldown();
            cooldown.setCooldownSeconds((float) maxCooldown);
            cooldown.setCooldownGroup(new NamespacedKey(plugin, "cd_" + item.id().toLowerCase(Locale.ROOT)));
            meta.setUseCooldown(cooldown);
        }

        stack.setItemMeta(meta);
        return stack;
    }

    /** Reads the ItemSmith id off a stack, or null if it isn't one of ours. */
    public String idOf(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;
        return stack.getItemMeta().getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
    }

    // --- Charges (M4) -------------------------------------------------------

    /** Current charges on a stack, or 0 if it has no counter. */
    public int getCharges(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return 0;
        Integer v = stack.getItemMeta().getPersistentDataContainer().get(chargesKey, PersistentDataType.INTEGER);
        return v == null ? 0 : v;
    }

    /** Sets charges (clamped 0..max), re-renders lore and durability bar. No depletion side-effect. */
    public void setCharges(ItemStack stack, int value, CustomItem def) {
        if (stack == null || !stack.hasItemMeta() || def == null || def.charges() == null) return;
        ItemMeta meta = stack.getItemMeta();
        int max = def.maxCharges() != null ? def.maxCharges() : def.charges();
        int v = Math.max(0, Math.min(max, value));
        meta.getPersistentDataContainer().set(chargesKey, PersistentDataType.INTEGER, v);
        meta.getPersistentDataContainer().set(maxChargesKey, PersistentDataType.INTEGER, max);
        meta.lore(renderLore(def, v, currentStats(meta, def)));
        reflectDurabilityBar(def, meta, v, max);
        stack.setItemMeta(meta);
    }

    /**
     * Spends {@code by} charges. Re-renders lore/bar and, if the counter hits zero, applies the
     * item's {@link DepletionPolicy}. Returns true when the stack was depleted to 0.
     */
    public boolean decrementCharges(ItemStack stack, int by, CustomItem def) {
        if (stack == null || !stack.hasItemMeta() || def == null || def.charges() == null) return false;
        ItemMeta meta = stack.getItemMeta();
        int max = def.maxCharges() != null ? def.maxCharges() : def.charges();
        int v = Math.max(0, getCharges(stack) - Math.max(0, by));
        meta.getPersistentDataContainer().set(chargesKey, PersistentDataType.INTEGER, v);
        meta.getPersistentDataContainer().set(maxChargesKey, PersistentDataType.INTEGER, max);
        meta.lore(renderLore(def, v, currentStats(meta, def)));
        reflectDurabilityBar(def, meta, v, max);
        stack.setItemMeta(meta);

        if (v <= 0) {
            applyDepletion(stack, def.onDepletion());
            return true;
        }
        return false;
    }

    // --- Persistent stats -----------------------------------------------------

    private NamespacedKey statKey(String name) {
        return new NamespacedKey(plugin, "stat_" + name);
    }

    /** A stat's current value on a stack, or "" if it has none. */
    public String getStat(ItemStack stack, String name) {
        if (stack == null || !stack.hasItemMeta() || name == null || name.isEmpty()) return "";
        String v = stack.getItemMeta().getPersistentDataContainer().get(statKey(name), PersistentDataType.STRING);
        return v == null ? "" : v;
    }

    /** Writes a stat's value and re-renders lore so any {@code <stat:name>} token stays in sync. */
    public void setStat(ItemStack stack, String name, String value, CustomItem def) {
        if (stack == null || !stack.hasItemMeta() || name == null || name.isEmpty()) return;
        ItemMeta meta = stack.getItemMeta();
        meta.getPersistentDataContainer().set(statKey(name), PersistentDataType.STRING, value == null ? "" : value);
        if (def != null) meta.lore(renderLore(def, def.charges() != null ? getCharges(stack) : null, currentStats(meta, def)));
        stack.setItemMeta(meta);
    }

    /** The live value of every stat the definition declares, read from the stack's PDC (falling back to the seed). */
    private Map<String, String> currentStats(ItemMeta meta, CustomItem def) {
        if (def == null || def.stats() == null || def.stats().isEmpty()) return Map.of();
        Map<String, String> out = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, String> e : def.stats().entrySet()) {
            String v = meta.getPersistentDataContainer().get(statKey(e.getKey()), PersistentDataType.STRING);
            out.put(e.getKey(), v == null ? e.getValue() : v);
        }
        return out;
    }

    private void applyDepletion(ItemStack stack, DepletionPolicy policy) {
        DepletionPolicy p = policy == null ? DepletionPolicy.CONSUME : policy;
        switch (p) {
            case CONSUME, BREAK -> stack.setAmount(Math.max(0, stack.getAmount() - 1));
            case KEEP_INERT -> { /* leave at 0; the charge-cost check blocks further use */ }
        }
    }

    private List<Component> renderLore(CustomItem item, Integer charges, Map<String, String> stats) {
        int max = item.maxCharges() != null ? item.maxCharges() : (item.charges() != null ? item.charges() : 0);
        List<Component> out = new ArrayList<>();
        boolean hasToken = false;
        List<String> lore = item.lore() != null ? item.lore() : List.of();
        for (String line : lore) {
            if (line.contains("<charges>") || line.contains("<max_charges>")) hasToken = true;
            String s = line;
            if (charges != null) {
                s = s.replace("<charges>", String.valueOf(charges))
                        .replace("<max_charges>", String.valueOf(max));
            }
            // <stat:name> shows the stat's current value; re-rendered on every set_stat so it can't drift.
            if (stats != null) {
                for (Map.Entry<String, String> e : stats.entrySet()) {
                    s = s.replace("<stat:" + e.getKey() + ">", e.getValue());
                }
            }
            out.add(Text.item(s));
        }
        // Auto-indicator: if the item has a charge counter but the author placed no <charges> token and
        // isn't mirroring it onto the durability bar, append a visible "Charges: N/M" line so charge
        // tracking is always shown (otherwise the counter changes invisibly and looks like it isn't working).
        if (charges != null && !hasToken && !item.durabilityBar()) {
            out.add(Text.item("<gray>Charges: <white>" + charges + "<gray>/<white>" + max));
        }
        return out;
    }

    /** Mirrors the charge ratio onto the vanilla durability bar when the item opts in. */
    private void reflectDurabilityBar(CustomItem item, ItemMeta meta, int charges, int max) {
        if (!item.durabilityBar() || max <= 0 || !(meta instanceof Damageable d)) return;
        short matMax = item.material().getMaxDurability();
        if (matMax <= 0) return; // material has no durability bar to reflect onto
        int damage = matMax - Math.round(matMax * (charges / (float) max));
        d.setDamage(Math.max(0, Math.min(matMax, damage)));
    }

    /**
     * Copies a custom item's identity (id + display name + lore + charge counter) from one stack
     * onto another, so it survives a vanilla transformation into a different item type. No-op if
     * {@code from} isn't one of ours.
     */
    public void copyIdentity(ItemStack from, ItemStack to) {
        if (to == null) return;
        String id = idOf(from);
        if (id == null) return;
        ItemMeta fromMeta = from.getItemMeta();
        ItemMeta toMeta = to.getItemMeta();
        if (toMeta == null) return;
        if (fromMeta.hasDisplayName()) toMeta.displayName(fromMeta.displayName());
        if (fromMeta.hasLore()) toMeta.lore(fromMeta.lore());
        PersistentDataContainer fromPdc = fromMeta.getPersistentDataContainer();
        PersistentDataContainer toPdc = toMeta.getPersistentDataContainer();
        toPdc.set(idKey, PersistentDataType.STRING, id);
        Integer ch = fromPdc.get(chargesKey, PersistentDataType.INTEGER);
        Integer mx = fromPdc.get(maxChargesKey, PersistentDataType.INTEGER);
        if (ch != null) toPdc.set(chargesKey, PersistentDataType.INTEGER, ch);
        if (mx != null) toPdc.set(maxChargesKey, PersistentDataType.INTEGER, mx);
        to.setItemMeta(toMeta);
    }
}
