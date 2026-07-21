package mastrjimbo.itemsmith.component.condition.health;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster's food (hunger) level is above the given value (0-20). */
public final class FoodAboveCondition implements Condition {

    public static final String ID = "food_above";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("value", ParamType.INT, 10)
                    .label("Food").range(0, 20).desc("Passes when the caster's food level is above this (20 = full)."))
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
        return "Food Above";
    }

    @Override
    public String description() {
        return "True while the caster's hunger is over a threshold.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getFoodLevel() > params.getInt("value", 10);
    }
}
