package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes when the caster is in the world with the given name. */
public final class WorldIsCondition implements Condition {

    public static final String ID = "world_is";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("world", ParamType.WORLD, "world")
                    .label("World").desc("Name of the world the caster must be in."))
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
        return "World Is";
    }

    @Override
    public String description() {
        return "True when the caster is in the named world.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getWorld().getName().equalsIgnoreCase(params.getString("world", "world"));
    }
}
