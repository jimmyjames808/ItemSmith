package mastrjimbo.itemsmith.component.condition.health;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster's absorption (yellow hearts) is above the given value (in HP points). */
public final class AbsorptionAboveCondition implements Condition {

    public static final String ID = "absorption_above";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("value", ParamType.DOUBLE, 0.0)
                    .label("Absorption").min(0).desc("Passes when the caster's absorption (yellow hearts) is above this."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.CONDITION;
    }

    @Override
    public String displayName() {
        return "Absorption Above";
    }

    @Override
    public String description() {
        return "True while the caster's absorption hearts are over a threshold.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getAbsorptionAmount() > params.getDouble("value", 0.0);
    }
}
