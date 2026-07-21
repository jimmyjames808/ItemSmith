package mastrjimbo.itemsmith.param;

import java.util.List;

/**
 * The declaration of a single parameter on a component (activator, condition,
 * targeter or action): its config key, human label, {@link ParamType}, default
 * value, optional numeric bounds / enum options, whether it is an "advanced"
 * option (hidden until the user flips the GUI into advanced mode), and a short
 * help line.
 *
 * <p>Build these with the fluent {@link #of} factory + {@code with*} methods so
 * call sites stay readable, e.g.
 * <pre>ParamDef.of("amount", ParamType.DOUBLE, 1.0).label("Bonus damage").min(0)</pre>
 */
public record ParamDef(
        String key,
        String label,
        ParamType type,
        Object defaultValue,
        boolean advanced,
        List<String> enumOptions,
        Double min,
        Double max,
        String description
) {
    /** Starts a definition with sensible empty metadata; refine with the {@code with*} methods. */
    public static ParamDef of(String key, ParamType type, Object defaultValue) {
        return new ParamDef(key, key, type, defaultValue, false, List.of(), null, null, "");
    }

    public ParamDef label(String label) {
        return new ParamDef(key, label, type, defaultValue, advanced, enumOptions, min, max, description);
    }

    public ParamDef desc(String description) {
        return new ParamDef(key, label, type, defaultValue, advanced, enumOptions, min, max, description);
    }

    /** Marks this parameter as advanced (progressive-disclosure hidden by default in the GUI). */
    public ParamDef markAdvanced() {
        return new ParamDef(key, label, type, defaultValue, true, enumOptions, min, max, description);
    }

    public ParamDef options(String... options) {
        return new ParamDef(key, label, type, defaultValue, advanced, List.of(options), min, max, description);
    }

    public ParamDef min(double min) {
        return new ParamDef(key, label, type, defaultValue, advanced, enumOptions, min, max, description);
    }

    public ParamDef max(double max) {
        return new ParamDef(key, label, type, defaultValue, advanced, enumOptions, min, max, description);
    }

    public ParamDef range(double min, double max) {
        return new ParamDef(key, label, type, defaultValue, advanced, enumOptions, min, max, description);
    }
}
