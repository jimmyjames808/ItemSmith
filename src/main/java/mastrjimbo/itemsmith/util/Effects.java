package mastrjimbo.itemsmith.util;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

/** Resolves a config potion-effect name to a {@link PotionEffectType}, tolerant of legacy names. */
public final class Effects {

    private Effects() {
    }

    /** Looks up by modern registry key first (e.g. {@code poison}), then falls back to legacy names. */
    @SuppressWarnings("deprecation") // getByName is the intentional legacy fallback
    public static PotionEffectType type(String name) {
        if (name == null || name.isBlank()) return null;
        String normalized = name.toLowerCase(Locale.ROOT).trim();
        PotionEffectType type = Registry.EFFECT.get(NamespacedKey.minecraft(normalized));
        if (type == null) {
            type = PotionEffectType.getByName(name);
        }
        return type;
    }
}
