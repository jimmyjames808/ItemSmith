package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes when the light level at the caster's block is above the given level. */
public final class LightAboveCondition implements Condition {

    public static final String ID = "light_above";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("level", ParamType.INT, 7)
                    .label("Light Level").range(0, 15)
                    .desc("Passes when the light level at the caster is above this (0-15)."))
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
        return "Light Above";
    }

    @Override
    public String description() {
        return "True when the light level at the caster is above a threshold.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getLocation().getBlock().getLightLevel() > params.getInt("level", 7);
    }
}
