package mastrjimbo.itemsmith.component.condition.time;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster's world time falls within an inclusive tick range. */
public final class TimeOfDayCondition implements Condition {

    public static final String ID = "time_of_day";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("min", ParamType.INT, 0)
                    .label("Min Time").min(0).max(24000).desc("Earliest world tick (0-24000), inclusive."))
            .add(ParamDef.of("max", ParamType.INT, 24000)
                    .label("Max Time").min(0).max(24000).desc("Latest world tick (0-24000), inclusive."))
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
        return "Time Of Day";
    }

    @Override
    public String description() {
        return "True while the world time is within a tick range.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        long t = ctx.player().getWorld().getTime();
        return t >= params.getInt("min", 0) && t <= params.getInt("max", 24000);
    }
}
