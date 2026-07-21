package mastrjimbo.itemsmith.util;

import org.bukkit.Particle;

import java.util.Locale;

/** Resolves a config particle name to a {@link Particle}, tolerant of case and a {@code minecraft:} prefix. */
public final class Particles {

    private Particles() {
    }

    /** @return the matching particle, or null if the name is blank/unknown. */
    public static Particle resolve(String name) {
        if (name == null || name.isBlank()) return null;
        String norm = name.trim().toLowerCase(Locale.ROOT);
        if (norm.startsWith("minecraft:")) norm = norm.substring("minecraft:".length());
        try {
            return Particle.valueOf(norm.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
