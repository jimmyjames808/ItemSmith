package mastrjimbo.itemsmith.component.condition.time;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster's world is on the given moon phase (0-7). */
public final class MoonPhaseCondition implements Condition {

    public static final String ID = "moon_phase";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("phase", ParamType.INT, 0)
                    .label("Moon Phase").range(0, 7).desc("Moon phase to match (0 = full, 4 = new)."))
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
        return "Moon Phase";
    }

    @Override
    public String description() {
        return "True while the world is on a specific moon phase.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        int p = (int) ((ctx.player().getWorld().getFullTime() / 24000L) % 8L);
        return p == params.getInt("phase", 0);
    }
}
