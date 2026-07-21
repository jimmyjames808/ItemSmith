package mastrjimbo.itemsmith.component.condition.meta;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;

/**
 * Wraps another condition and negates its result — the runtime side of the universal
 * {@code invert: true} flag any condition may carry (read in {@code ItemParser.parseConditions}).
 * All metadata (id/category/schema/...) delegates to the inner condition so the config still reads
 * as that condition; only {@link #test} flips. A thrown check still propagates to the shared gate,
 * which fail-closes it — so {@code invert} never turns an error into a pass.
 */
public final class InvertingCondition implements Condition {

    private final Condition inner;

    public InvertingCondition(Condition inner) {
        this.inner = inner;
    }

    /**
     * The wrapped (un-negated) condition. Lets the serializer and the GUI recover the original
     * condition + its params to re-emit {@code invert: true}, and lets the draft editor round-trip
     * the negation flag without loss.
     */
    public Condition inner() {
        return inner;
    }

    @Override
    public String id() {
        return inner.id();
    }

    @Override
    public String category() {
        return inner.category();
    }

    @Override
    public String displayName() {
        return "Not " + inner.displayName();
    }

    @Override
    public String description() {
        return inner.description();
    }

    @Override
    public ParamSchema schema() {
        return inner.schema();
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return !inner.test(ctx, target, params);
    }
}
