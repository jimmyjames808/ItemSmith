package mastrjimbo.itemsmith.gate;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import org.bukkit.Location;

/**
 * Opt-in land-protection guard for block-modifying actions. An action calls
 * {@link #mayEdit} before editing a block; it returns true (allow) unless the
 * firing ability opted in via {@code region.respect-claims: true} — which makes
 * {@link GateEvaluator#commit} stash {@link #VAR} on the context — in which case it
 * consults the {@code ProtectionHook}. With the flag unset (the default) it is a
 * pure pass-through, so existing block behavior is preserved.
 */
public final class Protect {

    /** Context-variable key set when the ability wants its block actions to respect claims. */
    public static final String VAR = "itemsmith:respect_claims";

    private Protect() {
    }

    public static boolean mayEdit(AbilityContext ctx, Location loc) {
        if (loc == null) return true;
        if (!Boolean.TRUE.equals(ctx.variables().get(VAR))) return true;
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return true;
        return plugin.protection().canBuild(ctx.player(), loc);
    }
}
