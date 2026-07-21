package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.drops.DropSources;
import mastrjimbo.itemsmith.gate.DepletionPolicy;
import mastrjimbo.itemsmith.loot.LootInjection;
import mastrjimbo.itemsmith.recipe.RecipeSpec;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.List;

/**
 * A fully-parsed custom item: its base material, resource-pack binding
 * (item-model or legacy CustomModelData), display text, any recipes that yield it,
 * charge counter (M4), and its list of {@link Ability} pipelines. Cooldowns are
 * per-ability (see {@link Ability#cooldownSeconds()}), not per-item.
 *
 * <p>An item may carry several {@link RecipeSpec recipes} of different families (craftable
 * <i>and</i> smeltable, or several routes) — {@code recipes} is empty when the item has none.
 * {@code itemModel}, {@code customModelData} and {@code charges}/
 * {@code maxCharges} may be null (null charges = the item has no charge counter).
 * The charge counter is a single PDC integer (see {@link ItemBuilder}); abilities
 * spend it via their gate's {@code charge-cost}, lore shows it via {@code <charges>}/
 * {@code <max_charges>} tokens, and {@code durabilityBar} optionally mirrors it onto
 * the vanilla durability bar. This is an immutable definition; {@link ItemBuilder}
 * turns it into a tagged {@link org.bukkit.inventory.ItemStack}.
 */
public record CustomItem(
        String id,
        Material material,
        NamespacedKey itemModel,
        Integer customModelData,
        String name,
        List<String> lore,
        List<RecipeSpec> recipes,
        List<Ability> abilities,
        Integer charges,
        Integer maxCharges,
        DepletionPolicy onDepletion,
        boolean durabilityBar,
        DropSources drops,
        LootInjection loot
) {
}
