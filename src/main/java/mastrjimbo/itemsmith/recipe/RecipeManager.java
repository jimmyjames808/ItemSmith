package mastrjimbo.itemsmith.recipe;

import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Registers — and, on reload, cleanly re-registers — the recipes for custom items. An item may carry
 * several {@link RecipeSpec recipes}; each is registered under its own indexed key ({@code recipe_<id>_<i>})
 * so they never collide, and {@link #keyFor(String, int)} hands that key back for recipe-book discovery.
 */
public final class RecipeManager {

    private final Plugin plugin;
    private final ItemRegistry registry;
    private final Logger logger;
    private final List<NamespacedKey> registered = new ArrayList<>();

    public RecipeManager(Plugin plugin, ItemRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
        this.logger = plugin.getLogger();
    }

    public void registerAll() {
        unregisterAll();
        for (CustomItem item : registry.all()) {
            List<RecipeSpec> specs = item.recipes();
            if (specs == null || specs.isEmpty()) continue;

            ItemStack result = registry.build(item.id());
            if (result == null) continue;

            for (int i = 0; i < specs.size(); i++) {
                RecipeSpec spec = specs.get(i);
                if (spec == null) continue;
                NamespacedKey key = keyFor(item.id(), i);
                try {
                    Bukkit.addRecipe(build(key, spec, result));
                    registered.add(key);
                } catch (RuntimeException e) {
                    logger.warning("Could not register recipe #" + i + " for '" + item.id() + "': " + e.getMessage());
                }
            }
        }
    }

    public void unregisterAll() {
        for (NamespacedKey key : registered) {
            Bukkit.removeRecipe(key);
        }
        registered.clear();
    }

    /** The key a given item's recipe registers under — used to unlock it in a player's recipe book. */
    public NamespacedKey keyFor(String itemId, int index) {
        return new NamespacedKey(plugin, "recipe_" + itemId.toLowerCase(Locale.ROOT) + "_" + index);
    }

    /** The keys that actually registered for {@code itemId} (Bukkit-native recipes only), in order. */
    public List<NamespacedKey> registeredKeysFor(String itemId) {
        String prefix = "recipe_" + itemId.toLowerCase(Locale.ROOT) + "_";
        List<NamespacedKey> out = new ArrayList<>();
        for (NamespacedKey key : registered) {
            if (key.getKey().startsWith(prefix)) out.add(key);
        }
        return out;
    }

    private Recipe build(NamespacedKey key, RecipeSpec spec, ItemStack result) {
        return switch (spec) {
            case RecipeSpec.Shaped s -> {
                ShapedRecipe recipe = new ShapedRecipe(key, result);
                recipe.shape(s.shape().toArray(new String[0]));
                for (Map.Entry<Character, Material> entry : s.ingredients().entrySet()) {
                    recipe.setIngredient(entry.getKey(), entry.getValue());
                }
                yield recipe;
            }
            case RecipeSpec.Shapeless s -> {
                ShapelessRecipe recipe = new ShapelessRecipe(key, result);
                for (Material material : s.materials()) {
                    recipe.addIngredient(material);
                }
                yield recipe;
            }
            case RecipeSpec.Cooking c -> {
                RecipeChoice input = new RecipeChoice.MaterialChoice(c.input());
                yield switch (c.kind()) {
                    case FURNACE -> new FurnaceRecipe(key, result, input, c.experience(), c.cookTime());
                    case BLASTING -> new BlastingRecipe(key, result, input, c.experience(), c.cookTime());
                    case SMOKING -> new SmokingRecipe(key, result, input, c.experience(), c.cookTime());
                    case CAMPFIRE -> new CampfireRecipe(key, result, input, c.experience(), c.cookTime());
                };
            }
            case RecipeSpec.Smithing s -> new SmithingTransformRecipe(key, result,
                    new RecipeChoice.MaterialChoice(s.template()),
                    new RecipeChoice.MaterialChoice(s.base()),
                    new RecipeChoice.MaterialChoice(s.addition()));
            case RecipeSpec.Stonecutting s -> new StonecuttingRecipe(key, result,
                    new RecipeChoice.MaterialChoice(s.input()));
        };
    }
}
