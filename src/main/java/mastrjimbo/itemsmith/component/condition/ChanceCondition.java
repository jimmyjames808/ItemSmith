package mastrjimbo.itemsmith.component.condition;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Passes with a configurable probability (0.0–1.0). A {@code chance} of 0.25
 * means the ability's actions run roughly one hit in four. The first real
 * condition, included in M0 to prove the condition-gate stage of the pipeline.
 */
public final class ChanceCondition implements Condition {

    public static final String ID = "chance";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("chance", ParamType.DOUBLE, 1.0)
                    .label("Chance (0-1)").range(0, 1).desc("Probability the ability fires (1.0 = always)."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.META;
    }

    @Override
    public String displayName() {
        return "Chance";
    }

    @Override
    public String description() {
        return "Only fires a fraction of the time.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        double chance = params.getDouble("chance", 1.0);
        if (chance >= 1.0) return true;
        if (chance <= 0.0) return false;
        return ThreadLocalRandom.current().nextDouble() < chance;
    }
}
