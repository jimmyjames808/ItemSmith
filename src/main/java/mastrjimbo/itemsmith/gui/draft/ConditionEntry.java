package mastrjimbo.itemsmith.gui.draft;

import mastrjimbo.itemsmith.component.condition.meta.InvertingCondition;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.engine.Configured;

/**
 * A mutable condition binding in the editor: the (always un-wrapped) condition definition, its
 * parameters, and an {@code inverted} flag. Keeping the negation as a boolean here — rather than a
 * live {@link InvertingCondition} wrapper — is what makes the {@code invert: true} flag round-trip
 * losslessly and lets the editor toggle it in place.
 */
public final class ConditionEntry {

    private Condition def;
    private final ParamBag params;
    private boolean inverted;

    public ConditionEntry(Condition def, ParamBag params, boolean inverted) {
        this.def = def;
        this.params = params;
        this.inverted = inverted;
    }

    /** Recovers the un-wrapped condition + its inverted flag from a parsed {@link Configured}. */
    public static ConditionEntry hydrate(Configured<Condition> configured) {
        Condition def = configured.definition();
        if (def instanceof InvertingCondition inv) {
            return new ConditionEntry(inv.inner(), new ParamBag(configured.params()), true);
        }
        return new ConditionEntry(def, new ParamBag(configured.params()), false);
    }

    /** Re-wraps in an {@link InvertingCondition} when inverted, mirroring {@code ItemParser}. */
    public Configured<Condition> toConfigured() {
        Condition d = inverted ? new InvertingCondition(def) : def;
        return new Configured<>(d, params.toValues());
    }

    public Condition def() {
        return def;
    }

    public void setDef(Condition def) {
        this.def = def;
    }

    public ParamBag params() {
        return params;
    }

    public boolean inverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
}
