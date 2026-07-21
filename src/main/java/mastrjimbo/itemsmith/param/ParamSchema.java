package mastrjimbo.itemsmith.param;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An ordered set of {@link ParamDef}s describing every parameter a component
 * accepts. Each activator/condition/targeter/action returns one from its
 * {@code schema()} method; the serializer and GUI iterate it generically, which
 * is what lets the engine scale to hundreds of components without hand-written
 * parsers or menus.
 */
public final class ParamSchema {

    /** A schema with no parameters, for components that need no configuration. */
    public static final ParamSchema EMPTY = new ParamSchema(List.of());

    private final List<ParamDef> defs;
    private final Map<String, ParamDef> byKey;

    private ParamSchema(List<ParamDef> defs) {
        this.defs = List.copyOf(defs);
        Map<String, ParamDef> map = new LinkedHashMap<>();
        for (ParamDef def : this.defs) {
            map.put(def.key(), def);
        }
        this.byKey = map;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Convenience for a schema whose params are already assembled. */
    public static ParamSchema of(ParamDef... defs) {
        return new ParamSchema(List.of(defs));
    }

    public List<ParamDef> defs() {
        return defs;
    }

    public ParamDef get(String key) {
        return byKey.get(key);
    }

    public boolean isEmpty() {
        return defs.isEmpty();
    }

    public static final class Builder {
        private final List<ParamDef> defs = new ArrayList<>();

        public Builder add(ParamDef def) {
            defs.add(def);
            return this;
        }

        public Builder add(String key, ParamType type, Object defaultValue) {
            return add(ParamDef.of(key, type, defaultValue));
        }

        public ParamSchema build() {
            return new ParamSchema(defs);
        }
    }
}
