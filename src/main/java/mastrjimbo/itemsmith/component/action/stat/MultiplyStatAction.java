package mastrjimbo.itemsmith.component.action.stat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;

/**
 * Multiplies a numeric persistent stat on the trigger item by a factor (a non-numeric or unset stat
 * counts as 0). Handy for scaling — a {@code factor} above 1 grows the stat, below 1 decays it, and
 * a negative factor flips its sign. No-op if the ability has no item stack.
 */
public final class MultiplyStatAction implements Action {

    public static final String ID = "multiply_stat";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("name", ParamType.STRING, "stat")
                    .label("Stat").desc("Which stat to change (a-z, 0-9, _)."))
            .add(ParamDef.of("factor", ParamType.DOUBLE, 1.0)
                    .label("Factor").desc("What to multiply the stat by."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ITEM; }
    @Override public String displayName() { return "Multiply Stat"; }
    @Override public String description() { return "Multiplies a numeric stat on the trigger item by a factor."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return;
        String name = params.getString("name", "stat").trim();
        if (name.isEmpty()) return;
        StatHooks.mutate(ctx, stack, name, () -> ctx.registry().multiplyStat(stack, name, params.getDouble("factor", 1.0)));
    }
}
