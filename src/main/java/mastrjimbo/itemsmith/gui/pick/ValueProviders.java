package mastrjimbo.itemsmith.gui.pick;

import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.param.ParamType;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Supplies the selectable option list (value + a representative icon {@link ItemStack}) for the
 * registry-backed parameter types, so {@link RegistryValuePickerScreen} can render a chest of choices
 * instead of asking the user to type a raw id. Icons are matched as closely as the vanilla item set
 * allows — effects render as the matching splash potion, entities as their spawn egg (or mob head where
 * one exists), biomes as a signature block/sapling, worlds by their environment. The engine's own
 * resolvers turn the picked key back into the object.
 */
public final class ValueProviders {

    /** One selectable value plus the item shown for it in the chest. */
    public record Option(String value, ItemStack icon) {
    }

    private ValueProviders() {
    }

    /** True if this type is chosen from a chest picker rather than typed into a dialog. */
    public static boolean hasPicker(ParamType type) {
        return switch (type) {
            case EFFECT, MATERIAL, HEAD, PARTICLE, ITEM_REF, SOUND, ENTITY_TYPE, ENCHANTMENT, BIOME, WORLD -> true;
            default -> false;
        };
    }

    public static List<Option> options(ParamType type, ItemRegistry registry) {
        List<Option> out = switch (type) {
            case EFFECT -> effects();
            case MATERIAL -> materials();
            case HEAD -> heads();
            case PARTICLE -> particles();
            case ITEM_REF -> itemRefs(registry);
            case SOUND -> sounds();
            case ENTITY_TYPE -> entities();
            case ENCHANTMENT -> enchantments();
            case BIOME -> biomes();
            case WORLD -> worlds();
            default -> new ArrayList<>();
        };
        out.sort(Comparator.comparing(Option::value));
        // Heads are optional — offer a "none" choice first so the GUI can clear back to the particle.
        if (type == ParamType.HEAD) {
            out.add(0, new Option("none", new ItemStack(Material.BARRIER)));
        }
        return out;
    }

    // --- effects: real splash potion where brewable, else colour-tinted ------------------------------

    private static List<Option> effects() {
        Map<PotionEffectType, PotionType> brewable = brewablePotions();
        List<Option> out = new ArrayList<>();
        for (PotionEffectType type : Registry.EFFECT) {
            out.add(new Option(type.getKey().getKey(), effectIcon(type, brewable.get(type))));
        }
        return out;
    }

    private static ItemStack effectIcon(PotionEffectType type, PotionType brewable) {
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        if (brewable != null) {
            meta.setBasePotionType(brewable);
        } else {
            Color color = type.getColor();
            if (color != null) meta.setColor(color);
        }
        potion.setItemMeta(meta);
        return potion;
    }

    private static Map<PotionEffectType, PotionType> brewablePotions() {
        Map<PotionEffectType, PotionType> map = new HashMap<>();
        for (PotionType potion : Registry.POTION) {
            PotionEffectType effect = potion.getEffectType();
            if (effect == null) continue;
            String key = potion.getKey().getKey();
            if (key.startsWith("long_") || key.startsWith("strong_")) {
                map.putIfAbsent(effect, potion);
            } else {
                map.put(effect, potion);
            }
        }
        return map;
    }

    // --- entities: mob head where one exists, else spawn egg, else a matched fallback ---------------

    private static List<Option> entities() {
        List<Option> out = new ArrayList<>();
        for (EntityType type : EntityType.values()) {
            if (type == EntityType.UNKNOWN) continue;
            out.add(new Option(type.getKey().getKey(), new ItemStack(entityIcon(type))));
        }
        return out;
    }

    private static Material entityIcon(EntityType type) {
        Material head = switch (type) {
            case ZOMBIE -> Material.ZOMBIE_HEAD;
            case SKELETON -> Material.SKELETON_SKULL;
            case WITHER_SKELETON -> Material.WITHER_SKELETON_SKULL;
            case CREEPER -> Material.CREEPER_HEAD;
            case PIGLIN -> Material.PIGLIN_HEAD;
            case ENDER_DRAGON -> Material.DRAGON_HEAD;
            case PLAYER -> Material.PLAYER_HEAD;
            default -> null;
        };
        if (head != null) return head;
        String key = type.getKey().getKey();
        Material egg = Material.matchMaterial(key + "_spawn_egg");
        if (egg != null) return egg;
        return nonMobIcon(key);
    }

    /** Non-mob entities (projectiles, vehicles, displays…) matched by key, so no removed enum refs. */
    private static Material nonMobIcon(String key) {
        if (key.contains("arrow")) return Material.ARROW;
        if (key.contains("trident")) return Material.TRIDENT;
        if (key.contains("boat")) return Material.OAK_BOAT;
        if (key.contains("minecart")) return Material.MINECART;
        if (key.contains("fireball")) return Material.FIRE_CHARGE;
        if (key.contains("wither_skull")) return Material.WITHER_SKELETON_SKULL;
        if (key.contains("snowball")) return Material.SNOWBALL;
        if (key.contains("ender_pearl")) return Material.ENDER_PEARL;
        if (key.contains("experience")) return Material.EXPERIENCE_BOTTLE;
        if (key.contains("tnt")) return Material.TNT;
        if (key.contains("falling_block")) return Material.SAND;
        if (key.contains("lightning")) return Material.LIGHTNING_ROD;
        if (key.contains("firework")) return Material.FIREWORK_ROCKET;
        if (key.contains("item_frame")) return Material.ITEM_FRAME;
        if (key.contains("painting")) return Material.PAINTING;
        if (key.contains("armor_stand")) return Material.ARMOR_STAND;
        if (key.contains("fishing")) return Material.FISHING_ROD;
        if (key.contains("potion")) return Material.SPLASH_POTION;
        if (key.contains("leash")) return Material.LEAD;
        if (key.contains("end_crystal")) return Material.END_CRYSTAL;
        if (key.contains("effect_cloud")) return Material.DRAGON_BREATH;
        if (key.contains("display") || key.equals("marker") || key.equals("interaction")) return Material.ARMOR_STAND;
        if (key.equals("item")) return Material.APPLE;
        return Material.EGG;
    }

    // --- enchantments / biomes / worlds / sounds ----------------------------------------------------

    private static List<Option> enchantments() {
        List<Option> out = new ArrayList<>();
        for (Enchantment enchant : Registry.ENCHANTMENT) {
            out.add(new Option(enchant.getKey().getKey(), new ItemStack(Material.ENCHANTED_BOOK)));
        }
        return out;
    }

    private static List<Option> biomes() {
        List<Option> out = new ArrayList<>();
        for (Biome biome : Registry.BIOME) {
            out.add(new Option(biome.getKey().getKey(), new ItemStack(biomeIcon(biome.getKey().getKey()))));
        }
        return out;
    }

    /** A signature block/sapling per biome, matched by keywords (specific tests before general ones). */
    private static Material biomeIcon(String key) {
        if (key.contains("ocean") || key.contains("river")) return Material.WATER_BUCKET;
        if (key.contains("badlands")) return Material.RED_SAND;
        if (key.contains("desert")) return Material.SAND;
        if (key.contains("beach") || key.contains("shore")) return Material.SAND;
        if (key.contains("bamboo")) return Material.BAMBOO;
        if (key.contains("jungle")) return Material.JUNGLE_SAPLING;
        if (key.contains("cherry")) return Material.CHERRY_SAPLING;
        if (key.contains("birch")) return Material.BIRCH_SAPLING;
        if (key.contains("dark_forest")) return Material.DARK_OAK_SAPLING;
        if (key.contains("mangrove")) return Material.MANGROVE_PROPAGULE;
        if (key.contains("savanna")) return Material.ACACIA_SAPLING;
        if (key.contains("taiga") || key.contains("grove") || key.contains("pine") || key.contains("spruce")) {
            return Material.SPRUCE_SAPLING;
        }
        if (key.contains("swamp")) return Material.LILY_PAD;
        if (key.contains("mushroom") || key.contains("mooshroom")) return Material.RED_MUSHROOM_BLOCK;
        if (key.contains("forest") || key.contains("meadow") || key.contains("flower") || key.contains("plains")) {
            return key.contains("plains") ? Material.GRASS_BLOCK : Material.OAK_SAPLING;
        }
        if (key.contains("nether") || key.contains("crimson") || key.contains("warped")
                || key.contains("basalt") || key.contains("soul")) {
            return Material.NETHERRACK;
        }
        if (key.contains("end") || key.contains("void")) return Material.END_STONE;
        if (key.contains("snow") || key.contains("frozen") || key.contains("ice") || key.contains("cold")) {
            return Material.SNOW_BLOCK;
        }
        if (key.contains("peak") || key.contains("mountain") || key.contains("hill")
                || key.contains("stony") || key.contains("windswept")) {
            return Material.STONE;
        }
        if (key.contains("cave") || key.contains("lush") || key.contains("dripstone") || key.contains("deep")) {
            return Material.MOSS_BLOCK;
        }
        return Material.GRASS_BLOCK;
    }

    private static List<Option> worlds() {
        List<Option> out = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            out.add(new Option(world.getName(), new ItemStack(worldIcon(world.getEnvironment()))));
        }
        return out;
    }

    private static Material worldIcon(World.Environment environment) {
        return switch (environment) {
            case NETHER -> Material.NETHERRACK;
            case THE_END -> Material.END_STONE;
            default -> Material.GRASS_BLOCK;
        };
    }

    private static List<Option> sounds() {
        List<Option> out = new ArrayList<>();
        for (Sound sound : Registry.SOUNDS) {
            String key = sound.getKey().getKey();
            Material icon = key.startsWith("music") || key.contains("record") ? Material.JUKEBOX : Material.NOTE_BLOCK;
            out.add(new Option(key, new ItemStack(icon)));
        }
        return out;
    }

    // --- materials / particles / item refs ----------------------------------------------------------

    private static List<Option> materials() {
        List<Option> out = new ArrayList<>();
        for (Material material : Material.values()) {
            if (!material.isItem() || material.isLegacy() || material.isAir()) continue;
            ItemStack stack = new ItemStack(material);
            if (stack.getItemMeta() == null) continue; // a handful of materials carry no meta; skip them
            out.add(new Option(material.getKey().getKey(), stack));
        }
        return out;
    }

    /** Mob and player heads / skulls only — for head-visual pickers (shoot_projectile, particle heads). */
    private static List<Option> heads() {
        List<Option> out = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isLegacy() || !material.isItem()) continue;
            String key = material.getKey().getKey();
            if (!key.endsWith("_head") && !key.endsWith("_skull")) continue;
            out.add(new Option(key, new ItemStack(material)));
        }
        return out;
    }

    /** Placeable blocks only (that also have an item form to show) — for the block-drop picker. */
    public static List<Option> blocks() {
        List<Option> out = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isLegacy() || material.isAir() || !material.isBlock() || !material.isItem()) continue;
            ItemStack stack = new ItemStack(material);
            if (stack.getItemMeta() == null) continue;
            out.add(new Option(material.getKey().getKey(), stack));
        }
        out.sort(Comparator.comparing(Option::value));
        return out;
    }

    private static List<Option> particles() {
        List<Option> out = new ArrayList<>();
        for (Particle particle : Registry.PARTICLE_TYPE) {
            out.add(new Option(particle.getKey().getKey(), new ItemStack(Material.FIREWORK_STAR)));
        }
        return out;
    }

    /**
     * All vanilla loot tables (chest / fishing / entity / archaeology / …) as pickable options, keyed by
     * their path (e.g. {@code chests/simple_dungeon}) so the value matches a {@code LootRule} pattern.
     */
    public static List<Option> lootTables() {
        List<Option> out = new ArrayList<>();
        for (LootTables table : LootTables.values()) {
            String key = table.getKey().getKey();
            out.add(new Option(key, new ItemStack(lootTableIcon(key))));
        }
        out.sort(Comparator.comparing(Option::value));
        return out;
    }

    private static Material lootTableIcon(String key) {
        if (key.startsWith("chests/")) return Material.CHEST;
        if (key.contains("fishing")) return Material.FISHING_ROD;
        if (key.startsWith("entities/")) return Material.ZOMBIE_HEAD;
        if (key.startsWith("archaeology/")) return Material.BRUSH;
        if (key.startsWith("blocks/")) return Material.OAK_LEAVES;
        if (key.startsWith("gameplay/")) return Material.EMERALD;
        if (key.startsWith("shearing/")) return Material.SHEARS;
        return Material.PAPER;
    }

    private static List<Option> itemRefs(ItemRegistry registry) {
        List<Option> out = new ArrayList<>();
        for (String id : registry.ids()) {
            CustomItem def = registry.get(id);
            Material icon = def != null && def.material().isItem() ? def.material() : Material.PAPER;
            out.add(new Option(id, new ItemStack(icon)));
        }
        return out;
    }
}
