package mastrjimbo.itemsmith.component.condition.health;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster's current health is below the given value (in half-heart HP points). */
public final class HealthBelowCondition implements Condition {

    public static final String ID = "health_below";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("value", ParamType.DOUBLE, 10.0)
                    .label("Health").min(0).desc("Passes when the caster's health is below this (20 = full)."))
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
        return "Health Below";
    }

    @Override
    public String description() {
        return "True while the caster's health is under a threshold.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getHealth() < params.getDouble("value", 10.0);
    }
}
