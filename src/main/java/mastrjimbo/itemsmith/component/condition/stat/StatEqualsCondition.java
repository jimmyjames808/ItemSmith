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
 * Passes when a stat on the trigger item equals a given value, compared as text. Works for string
 * stats (e.g. {@code bound_mob == zombie}) and for exact numbers stored as text ({@code level == 3}).
 * An unset stat compares as "".
 */
public final class StatEqualsCondition implements Condition {

    public static final String ID = "stat_equals";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("name", ParamType.STRING, "stat")
                    .label("Stat").desc("Which stat to read."))
            .add(ParamDef.of("value", ParamType.STRING, "")
                    .label("Value").desc("Passes when the stat equals this text."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.CONDITION; }
    @Override public String displayName() { return "Stat Equals"; }
    @Override public String description() { return "True when an item stat equals this value."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return false;
        return ctx.registry().getStat(stack, params.getString("name", "stat").trim())
                .equals(params.getString("value", ""));
    }
}
