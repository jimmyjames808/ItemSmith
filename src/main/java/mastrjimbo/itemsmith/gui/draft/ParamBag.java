package mastrjimbo.itemsmith.gui.draft;

import mastrjimbo.itemsmith.param.ParamValues;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The mutable counterpart of {@link ParamValues}: a live key→value map the GUI editors write into.
 * Seeded from a component's parsed values on hydrate, lowered back to an immutable {@link ParamValues}
 * on save. Value types match the engine's ({@code Integer}/{@code Double}/{@code Boolean}/{@code String}/
 * {@code Material}/{@code PotionEffectType}/{@code List<String>}).
 */
public final class ParamBag {

    private final Map<String, Object> values;

    public ParamBag() {
        this.values = new LinkedHashMap<>();
    }

    public ParamBag(ParamValues from) {
        this.values = new LinkedHashMap<>(from.asMap());
    }

    public Object get(String key) {
        return values.get(key);
    }

    public void set(String key, Object value) {
        values.put(key, value);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public Map<String, Object> raw() {
        return values;
    }

    /** An immutable snapshot for lowering into the engine model. */
    public ParamValues toValues() {
        return new ParamValues(new LinkedHashMap<>(values));
    }
}
