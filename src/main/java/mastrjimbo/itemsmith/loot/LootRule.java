package mastrjimbo.itemsmith.loot;

import org.bukkit.NamespacedKey;

import java.util.List;
import java.util.Locale;

/**
 * One "inject this custom item into these loot tables" rule. {@code tables} are match patterns against a
 * generated table's key — an exact key ({@code chests/simple_dungeon}), a namespaced key
 * ({@code minecraft:gameplay/fishing/fish}), or a prefix / {@code *} wildcard ({@code chests/},
 * {@code entities/}). {@code chance} is rolled per generation; {@code min}/{@code max} is the stack count.
 */
public record LootRule(List<String> tables, double chance, int min, int max) {

    /** True if any of this rule's patterns matches the given loot-table key. */
    public boolean matches(NamespacedKey key) {
        String full = key.toString();                 // e.g. minecraft:chests/simple_dungeon
        String path = key.getKey();                   // e.g. chests/simple_dungeon
        for (String pattern : tables) {
            String p = pattern.toLowerCase(Locale.ROOT).trim();
            if (p.endsWith("*")) p = p.substring(0, p.length() - 1);
            if (p.isEmpty()) continue;
            if (path.equals(p) || path.startsWith(p) || full.equals(p) || full.startsWith(p)) {
                return true;
            }
        }
        return false;
    }
}
