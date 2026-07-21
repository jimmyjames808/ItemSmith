package mastrjimbo.itemsmith.recipe;

import org.bukkit.Material;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A parsed recipe that yields a custom item. A sealed hierarchy so one item can carry several recipes
 * of different families (an item may be craftable <i>and</i> smeltable). Each variant is a small record
 * and knows its own YAML {@code type} string; {@link RecipeManager} pattern-switches over them to build
 * the matching Bukkit recipe, and {@link mastrjimbo.itemsmith.store.ItemSerializer} does the same to
 * write them back.
 *
 * <p>M6 covers the Bukkit-native families (crafting shaped/shapeless, the smelting family, smithing
 * transform, stonecutter). Brewing and anvil have no Bukkit {@code Recipe} API and are a post-launch
 * event-based pass — the {@code brewing}/{@code anvil} type names are reserved but unsupported for now.
 */
public sealed interface RecipeSpec
        permits RecipeSpec.Shaped, RecipeSpec.Shapeless, RecipeSpec.Cooking,
                RecipeSpec.Smithing, RecipeSpec.Stonecutting {

    /** The YAML {@code type} discriminator for this recipe (e.g. {@code "shaped"}, {@code "blasting"}). */
    String type();

    /** Shaped crafting: {@code shape} rows (1-3) map letters to materials via {@code ingredients}. */
    record Shaped(List<String> shape, Map<Character, Material> ingredients) implements RecipeSpec {
        public String type() {
            return "shaped";
        }
    }

    /** Shapeless crafting: any arrangement of the flat {@code materials} list. */
    record Shapeless(List<Material> materials) implements RecipeSpec {
        public String type() {
            return "shapeless";
        }
    }

    /** The smelting family (furnace/blast/smoker/campfire): a single {@code input} + experience + cook time. */
    record Cooking(Kind kind, Material input, float experience, int cookTime) implements RecipeSpec {
        public String type() {
            return kind.id();
        }

        /** The four cooking stations, each with its YAML id and vanilla-typical default cook time (ticks). */
        public enum Kind {
            FURNACE("furnace", 200),
            BLASTING("blasting", 100),
            SMOKING("smoking", 100),
            CAMPFIRE("campfire", 600);

            private final String id;
            private final int defaultCookTime;

            Kind(String id, int defaultCookTime) {
                this.id = id;
                this.defaultCookTime = defaultCookTime;
            }

            public String id() {
                return id;
            }

            public int defaultCookTime() {
                return defaultCookTime;
            }

            /** Resolves a YAML type string to a Kind, or null if it isn't a cooking type. */
            public static Kind from(String type) {
                if (type == null) return null;
                String t = type.toLowerCase(Locale.ROOT);
                for (Kind k : values()) {
                    if (k.id.equals(t)) return k;
                }
                return null;
            }
        }
    }

    /** Smithing transform: {@code template} + {@code base} + {@code addition} at a smithing table. */
    record Smithing(Material template, Material base, Material addition) implements RecipeSpec {
        public String type() {
            return "smithing";
        }
    }

    /** Stonecutter: a single {@code input} block. */
    record Stonecutting(Material input) implements RecipeSpec {
        public String type() {
            return "stonecutting";
        }
    }
}
