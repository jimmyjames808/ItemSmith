package mastrjimbo.itemsmith.drops;

import java.util.Locale;

/**
 * Whether a {@link BlockDrop} cares about the breaking tool's Silk Touch: {@code ANY} always drops,
 * {@code REQUIRE} only with Silk Touch (harvest-style), {@code FORBID} only without (ore-style).
 */
public enum SilkTouchPolicy {
    ANY,
    REQUIRE,
    FORBID;

    /** Lenient parse of the {@code silk-touch} YAML value; defaults to {@link #ANY}. */
    public static SilkTouchPolicy from(String s) {
        if (s == null) return ANY;
        return switch (s.toLowerCase(Locale.ROOT)) {
            case "require", "required", "true", "yes" -> REQUIRE;
            case "forbid", "forbidden", "false", "no" -> FORBID;
            default -> ANY;
        };
    }

    /** True if a break with (or without) Silk Touch is allowed to drop under this policy. */
    public boolean allows(boolean hasSilkTouch) {
        return switch (this) {
            case ANY -> true;
            case REQUIRE -> hasSilkTouch;
            case FORBID -> !hasSilkTouch;
        };
    }
}
