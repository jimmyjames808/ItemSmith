package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes when the caster's Y coordinate is below the given value. */
public final class YBelowCondition implements Condition {

    public static final String ID = "y_below";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("value", ParamType.DOUBLE, 64.0)
                    .label("Y Level").desc("Passes when the caster's Y coordinate is below this."))
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
        return "Y Below";
    }

    @Override
    public String description() {
        return "True when the caster is below a given height.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getLocation().getY() < params.getDouble("value", 64);
    }
}
