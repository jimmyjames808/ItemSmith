package mastrjimbo.itemsmith.engine;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared evaluation of a condition list. Conditions form a flat AND-gate: every one must pass, and
 * any that throws is treated as failed (fail-closed). Both the ability-level gate
 * ({@link AbilityEngine}) and the inline {@code if} gate ({@code IfAction}) route through here, so
 * they can never drift; the only difference is the {@code target} each hands the conditions.
 */
public final class Conditions {

    private Conditions() {
    }

    /**
     * @param conditions the AND-list to evaluate (empty passes trivially).
     * @param target     the object target-relative conditions test against (see {@link Condition#test}).
     * @param logger     used to report a throwing condition; may be null to stay quiet.
     * @return true only if every condition passes.
     */
    public static boolean allPass(List<Configured<Condition>> conditions,
                                  AbilityContext ctx, Object target, Logger logger) {
        for (Configured<Condition> c : conditions) {
            try {
                // Resolve <stat:...> tokens against the trigger item's live stats before the check.
                if (!c.definition().test(ctx, target, c.params().resolve(ctx))) return false;
            } catch (RuntimeException e) {
                if (logger != null) {
                    logger.log(Level.WARNING, "Condition '" + c.definition().id() + "' on item '"
                            + ctx.itemId() + "' threw; treating as failed.", e);
                }
                return false;
            }
        }
        return true;
    }
}
