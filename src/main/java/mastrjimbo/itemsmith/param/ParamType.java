package mastrjimbo.itemsmith.param;

/**
 * The set of value kinds a component parameter can hold.
 *
 * <p>This enum is the single shared vocabulary of the engine: the YAML
 * (de)serializer coerces raw config into these types, and the (future) GUI
 * renders an editor per type. Because both sides key off the same enum they can
 * never drift. Only add a value here when both a coercion rule
 * ({@link ParamCodec}) and, eventually, a GUI editor exist for it.
 */
public enum ParamType {
    /** Whole number. */
    INT,
    /** Decimal number. */
    DOUBLE,
    /** true / false. */
    BOOLEAN,
    /** Plain text, no formatting. */
    STRING,
    /** MiniMessage-formatted text (names, lore, messages). */
    MINIMESSAGE,
    /** One choice from a fixed list ({@link ParamDef#enumOptions()}). */
    ENUM,
    /** A Bukkit {@link org.bukkit.Material}. */
    MATERIAL,
    /** A mob or player head / skull — a heads-only picker; resolved and stored as a Material. */
    HEAD,
    /** A potion {@link org.bukkit.potion.PotionEffectType}. */
    EFFECT,
    /** A namespaced sound key, kept as a string until played. */
    SOUND,
    /** A particle name, kept as a string until spawned. */
    PARTICLE,
    /** The id of another ItemSmith item. */
    ITEM_REF,
    /** An {@link org.bukkit.entity.EntityType} key, kept as a string. */
    ENTITY_TYPE,
    /** An {@link org.bukkit.enchantments.Enchantment} key, kept as a string. */
    ENCHANTMENT,
    /** A biome key, kept as a string. */
    BIOME,
    /** A world name, kept as a string. */
    WORLD,
    /** A list of plain strings. */
    STRING_LIST
}
