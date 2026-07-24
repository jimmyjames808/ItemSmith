package mastrjimbo.itemsmith.param;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The resolved parameter values for one configured component instance: a plain
 * map of key to already-coerced value (produced by {@link ParamCodec}). Typed
 * getters cast/convert on the way out. A missing key returns the supplied
 * fallback, but in practice {@link ParamCodec#read} pre-fills every schema key
 * with its default, so components can read without defensive checks.
 *
 * <p><b>Stat tokens.</b> A String value may embed a {@code <stat:NAME>} token
 * (optionally with a trailing scale, e.g. {@code <stat:level>*2} or
 * {@code <stat:power>+1}). {@link #resolve(AbilityContext)} substitutes those
 * tokens against the triggering item's live stats <em>when the action/condition
 * runs</em>, so params can scale with an item. The engine calls {@code resolve}
 * once per run (in {@code ActionExecutor} and {@code Conditions}); components
 * keep reading through the ordinary typed getters and never see the raw token.
 */
public final class ParamValues {

    /**
     * Matches a {@code <stat:NAME>} token with an optional trailing scale.
     * Group 1 = stat name; group 2 = operator ({@code * / + -}); group 3 = operand.
     * Groups 2/3 are absent for a bare token. The scale group is optional and
     * backtracks cleanly, so {@code <stat:a>+<stat:b>} reads as two bare tokens.
     */
    private static final Pattern STAT_TOKEN =
            Pattern.compile("<stat:([A-Za-z0-9_]+)>(?:\\s*([*/+\\-])\\s*([0-9]+(?:\\.[0-9]+)?))?");

    private final Map<String, Object> values;

    public ParamValues(Map<String, Object> values) {
        this.values = values;
    }

    /** True if {@code raw} embeds at least one {@code <stat:...>} token; cheap substring pre-check. */
    public static boolean containsToken(String raw) {
        return raw != null && raw.contains("<stat:");
    }

    public Object raw(String key) {
        return values.get(key);
    }

    public boolean has(String key) {
        return values.get(key) != null;
    }

    public int getInt(String key, int fallback) {
        Object v = values.get(key);
        if (v instanceof Number n) return n.intValue();
        if (v instanceof String s) {
            try {
                return (int) Double.parseDouble(s.trim());
            } catch (NumberFormatException e) {
                return fallback;
            }
        }
        return fallback;
    }

    public double getDouble(String key, double fallback) {
        Object v = values.get(key);
        if (v instanceof Number n) return n.doubleValue();
        if (v instanceof String s) {
            try {
                return Double.parseDouble(s.trim());
            } catch (NumberFormatException e) {
                return fallback;
            }
        }
        return fallback;
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

    /**
     * Returns a copy of these values with every {@code <stat:...>} token in a String
     * (or in the elements of a String list) substituted against the triggering item's
     * live stats. Returns {@code this} unchanged when no value carries a token, so the
     * common tokenless case allocates nothing.
     *
     * <p>A bare {@code <stat:NAME>} becomes the stat's stored value; a scaled token
     * ({@code <stat:NAME>*2}) reads the stat as a number and applies the arithmetic.
     * When the stat is unset (or the ability has no item stack, e.g. a projectile-hit)
     * the token resolves to {@code "0"} — a safe default that parses in numeric reads
     * and reads as {@code "0"} in string reads.
     */
    public ParamValues resolve(AbilityContext ctx) {
        Map<String, Object> out = null;
        for (Map.Entry<String, Object> e : values.entrySet()) {
            Object resolved = resolveValue(e.getValue(), ctx);
            if (resolved != e.getValue()) {
                if (out == null) out = new LinkedHashMap<>(values);
                out.put(e.getKey(), resolved);
            }
        }
        return out == null ? this : new ParamValues(out);
    }

    /** Resolves tokens in one value; returns the same instance when nothing changes. */
    private static Object resolveValue(Object v, AbilityContext ctx) {
        if (v instanceof String s) {
            return containsToken(s) ? substitute(s, ctx) : v;
        }
        if (v instanceof List<?> list) {
            List<Object> copy = null;
            for (int i = 0; i < list.size(); i++) {
                Object el = list.get(i);
                if (el instanceof String es && containsToken(es)) {
                    if (copy == null) copy = new ArrayList<>(list);
                    copy.set(i, substitute(es, ctx));
                }
            }
            return copy == null ? v : copy;
        }
        return v;
    }

    /** Replaces every {@code <stat:...>} token in {@code raw} against {@code ctx}'s item stats. */
    private static String substitute(String raw, AbilityContext ctx) {
        ItemStack stack = ctx.itemStack();
        ItemRegistry reg = ctx.registry();
        Matcher m = STAT_TOKEN.matcher(raw);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            String name = m.group(1);
            String op = m.group(2);
            String replacement;
            if (op == null) {
                String value = stack != null ? reg.getStat(stack, name) : null;
                replacement = (value == null || value.isBlank()) ? "0" : value.trim();
            } else {
                double base = stack != null ? reg.getStatNumber(stack, name, 0) : 0;
                double operand = Double.parseDouble(m.group(3));
                double result = switch (op) {
                    case "*" -> base * operand;
                    case "/" -> operand != 0 ? base / operand : 0;
                    case "+" -> base + operand;
                    case "-" -> base - operand;
                    default -> base;
                };
                replacement = ItemRegistry.formatNumber(result);
            }
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
