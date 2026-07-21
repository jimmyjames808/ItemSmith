package mastrjimbo.itemsmith.gate;

import java.util.Locale;

/**
 * What happens to a charged item when its charge counter reaches zero.
 *
 * <ul>
 *   <li>{@code CONSUME} — remove one from the stack (vanilla "used it up" intuition). Default.</li>
 *   <li>{@code BREAK} — remove one from the stack and play the item-break effect (a wand "snaps").</li>
 *   <li>{@code KEEP_INERT} — leave the item at 0 charges; further gated uses fail the charge check until
 *       an {@code add_charges}/{@code set_charges} recharge (rechargeable talisman).</li>
 * </ul>
 */
public enum DepletionPolicy {
    CONSUME,
    BREAK,
    KEEP_INERT;

    /** Lenient parse; unknown/blank falls back to {@link #CONSUME}. */
    public static DepletionPolicy from(String raw) {
        if (raw == null) return CONSUME;
        try {
            return valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return CONSUME;
        }
    }
}
