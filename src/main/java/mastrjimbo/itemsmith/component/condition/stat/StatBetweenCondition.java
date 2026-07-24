package mastrjimbo.itemsmith.component.condition.stat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;

/**
 * Passes when a numeric stat on the trigger item sits within an inclusive range ({@code min <= stat
 * <= max}) — the "in the sweet spot" gate that rounds out the above/below/equals set, e.g. an ability
 * that only fires while a charge stat is mid-range. A non-numeric or unset stat reads as 0.
 */
public final class StatBetweenCondition implements Condition {

    public static final String ID = "stat_between";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("name", ParamType.STRING, "stat")
                    .label("Stat").desc("Which stat to read."))
            .add(ParamDef.of("min", ParamType.DOUBLE, 0.0)
                    .label("Min").desc("Lowest value that still passes (inclusive)."))
            .add(ParamDef.of("max", ParamType.DOUBLE, 0.0)
                    .label("Max").desc("Highest value that still passes (inclusive)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.CONDITION; }
    @Override public String displayName() { return "Stat Between"; }
    @Override public String description() { return "True when an item stat is within an inclusive range."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return false;
        double value = ctx.registry().getStatNumber(stack, params.getString("name", "stat").trim(), 0);
        return value >= params.getDouble("min", 0.0) && value <= params.getDouble("max", 0.0);
    }
}
