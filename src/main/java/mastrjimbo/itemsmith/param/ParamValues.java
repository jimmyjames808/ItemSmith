package mastrjimbo.itemsmith.param;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

/**
 * The resolved parameter values for one configured component instance: a plain
 * map of key to already-coerced value (produced by {@link ParamCodec}). Typed
 * getters cast/convert on the way out. A missing key returns the supplied
 * fallback, but in practice {@link ParamCodec#read} pre-fills every schema key
 * with its default, so components can read without defensive checks.
 */
public final class ParamValues {

    private final Map<String, Object> values;

    public ParamValues(Map<String, Object> values) {
        this.values = values;
    }

    public Object raw(String key) {
        return values.get(key);
    }

    public boolean has(String key) {
        return values.get(key) != null;
    }

    public int getInt(String key, int fallback) {
        return values.get(key) instanceof Number n ? n.intValue() : fallback;
    }

    public double getDouble(String key, double fallback) {
        return values.get(key) instanceof Number n ? n.doubleValue() : fallback;
    }

    public boolean getBool(String key, boolean fallback) {
        return values.get(key) instanceof Boolean b ? b : fallback;
    }

    public String getString(String key, String fallback) {
        Object v = values.get(key);
        return v != null ? String.valueOf(v) : fallback;
    }

    public Material getMaterial(String key) {
        return values.get(key) instanceof Material m ? m : null;
    }

    public PotionEffectType getEffect(String key) {
        return values.get(key) instanceof PotionEffectType t ? t : null;
    }

    /** Resolves a {@code PARTICLE} param (stored as a string) to a {@link org.bukkit.Particle}, or null. */
    public org.bukkit.Particle getParticle(String key) {
        return mastrjimbo.itemsmith.util.Particles.resolve(getString(key, null));
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key) {
        return values.get(key) instanceof List<?> list ? (List<String>) list : List.of();
    }

    public Map<String, Object> asMap() {
        return values;
    }
}
