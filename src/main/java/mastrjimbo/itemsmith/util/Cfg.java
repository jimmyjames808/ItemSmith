package mastrjimbo.itemsmith.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Map;

/**
 * Turns a raw YAML map (as returned by {@code getMapList}) into a
 * {@link ConfigurationSection}, so list-of-maps entries — abilities, conditions,
 * actions — can be read through the same typed section API as everything else.
 * Nested maps become nested sections recursively.
 */
public final class Cfg {

    private Cfg() {
    }

    public static ConfigurationSection wrap(Map<?, ?> map) {
        MemoryConfiguration cfg = new MemoryConfiguration();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> nested) {
                cfg.createSection(key, nested);
            } else {
                cfg.set(key, value);
            }
        }
        return cfg;
    }
}
