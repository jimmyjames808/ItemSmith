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
 * Resets a persistent stat on the trigger item back to the value the item definition declares for it
 * (or "0" if the definition declares none) — the "clear the counter / cool it down / respec" primitive
 * that undoes any accumulated {@code add_stat}/{@code multiply_stat} changes. No-op if the ability has
 * no item stack.
 */
public final class ResetStatAction implements Action {

    public static final String ID = "reset_stat";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("name", ParamType.STRING, "stat")
                    .label("Stat").desc("Which stat to reset to its declared initial value."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ITEM; }
    @Override public String displayName() { return "Reset Stat"; }
    @Override public String description() { return "Resets a stat on the trigger item to its declared initial value."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return;
        String name = params.getString("name", "stat").trim();
        if (name.isEmpty()) return;
        StatHooks.mutate(ctx, stack, name, () -> ctx.registry().resetStat(stack, name));
    }
}
