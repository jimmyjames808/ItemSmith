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
 * Passes when a numeric stat on the trigger item is above a threshold — the gate that makes
 * leveling/evolution work: put this in a stronger ability's {@code conditions:} so it only unlocks
 * once the stat crosses the line. A non-numeric or unset stat reads as 0.
 */
public final class StatAboveCondition implements Condition {

    public static final String ID = "stat_above";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("name", ParamType.STRING, "stat")
                    .label("Stat").desc("Which stat to read."))
            .add(ParamDef.of("amount", ParamType.DOUBLE, 0.0)
                    .label("Amount").desc("Passes when the stat is greater than this."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.CONDITION; }
    @Override public String displayName() { return "Stat Above"; }
    @Override public String description() { return "True when an item stat is above this value."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return false;
        return ctx.registry().getStatNumber(stack, params.getString("name", "stat").trim(), 0)
                > params.getDouble("amount", 0.0);
    }
}
