package mastrjimbo.itemsmith.param;

import mastrjimbo.itemsmith.util.Effects;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Converts between raw config and typed {@link ParamValues}, driven entirely by
 * a {@link ParamSchema}. This is the only place coercion rules live, so the YAML
 * store and the GUI editors always agree on how a {@link ParamType} maps to a
 * stored value.
 */
public final class ParamCodec {

    private final Consumer<String> warn;

    /** @param warn sink for human-readable parse warnings (e.g. plugin logger). */
    public ParamCodec(Consumer<String> warn) {
        this.warn = warn;
    }

    /**
     * Reads every parameter declared by {@code schema} out of {@code section}
     * (which may be null), coercing each per its {@link ParamType} and falling
     * back to the declared default when absent or invalid.
     */
    public ParamValues read(ParamSchema schema, ConfigurationSection section, String where) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (ParamDef def : schema.defs()) {
            Object value = (section != null && section.contains(def.key()))
                    ? coerce(def, section, where)
                    : def.defaultValue();
            out.put(def.key(), value);
        }
        return new ParamValues(out);
    }

    private Object coerce(ParamDef def, ConfigurationSection s, String where) {
        String key = def.key();
        switch (def.type()) {
            case INT -> {
                return clampInt(s.getInt(key), def);
            }
            case DOUBLE -> {
                return clampDouble(s.getDouble(key), def);
            }
            case BOOLEAN -> {
                return s.getBoolean(key);
            }
            case STRING, MINIMESSAGE, SOUND, PARTICLE, ITEM_REF, ENTITY_TYPE, ENCHANTMENT, BIOME, WORLD -> {
                return s.getString(key, String.valueOf(def.defaultValue()));
            }
            case ENUM -> {
                String raw = s.getString(key, String.valueOf(def.defaultValue()));
                if (raw != null && !def.enumOptions().isEmpty()
                        && def.enumOptions().stream().noneMatch(o -> o.equalsIgnoreCase(raw))) {
                    warn.accept(where + ": '" + raw + "' is not a valid option for '" + key
                            + "' (allowed: " + String.join(", ", def.enumOptions()) + "); using default.");
                    return def.defaultValue();
                }
                return raw;
            }
            case MATERIAL, HEAD -> {
                String raw = s.getString(key, String.valueOf(def.defaultValue()));
                if (raw == null || raw.isBlank()) {
                    return def.defaultValue(); // blank = optional/unset — not an error
                }
                Material m = Material.matchMaterial(raw);
                if (m == null) {
                    warn.accept(where + ": unknown material '" + raw + "' for '" + key + "'.");
                    return def.defaultValue();
                }
                return m;
            }
            case EFFECT -> {
                PotionEffectType t = Effects.type(s.getString(key, ""));
                if (t == null) {
                    warn.accept(where + ": unknown potion effect '" + s.getString(key) + "' for '" + key + "'.");
                    return def.defaultValue();
                }
                return t;
            }
            case STRING_LIST -> {
                return s.getStringList(key);
            }
            default -> {
                return def.defaultValue();
            }
        }
    }

    private Object clampInt(int value, ParamDef def) {
        if (def.min() != null && value < def.min()) value = (int) Math.ceil(def.min());
        if (def.max() != null && value > def.max()) value = (int) Math.floor(def.max());
        return value;
    }

    private Object clampDouble(double value, ParamDef def) {
        if (def.min() != null) value = Math.max(def.min(), value);
        if (def.max() != null) value = Math.min(def.max(), value);
        return value;
    }

    /**
     * Writes typed values back into a config section for saving. Materials and
     * effects are stored by their lowercase key so files stay human-readable.
     */
    public void write(ParamSchema schema, ParamValues values, ConfigurationSection out) {
        for (ParamDef def : schema.defs()) {
            Object v = values.raw(def.key());
            if (v == null) continue;
            out.set(def.key(), toStorable(v));
        }
    }

    private Object toStorable(Object v) {
        if (v instanceof Material m) return m.getKey().getKey();
        if (v instanceof PotionEffectType t) return t.getKey().getKey().toLowerCase(Locale.ROOT);
        return v;
    }
}
