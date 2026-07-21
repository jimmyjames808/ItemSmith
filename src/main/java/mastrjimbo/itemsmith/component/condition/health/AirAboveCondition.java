package mastrjimbo.itemsmith.component.condition.health;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster's remaining air (in ticks) is above the given value. */
public final class AirAboveCondition implements Condition {

    public static final String ID = "air_above";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("ticks", ParamType.INT, 0)
                    .label("Air Ticks").min(0).desc("Passes when the caster's remaining air (in ticks) is above this."))
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
        return "Air Above";
    }

    @Override
    public String description() {
        return "True while the caster's remaining air is over a threshold.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getRemainingAir() > params.getInt("ticks", 0);
    }
}
