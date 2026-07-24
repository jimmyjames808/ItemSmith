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
 * Adds to a numeric persistent stat on the trigger item (a non-numeric or unset stat counts as 0).
 * The classic "level up / count a kill / heat up" primitive; pass a negative amount to subtract.
 * No-op if the ability has no item stack.
 */
public final class AddStatAction implements Action {

    public static final String ID = "add_stat";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("name", ParamType.STRING, "stat")
                    .label("Stat").desc("Which stat to change (a-z, 0-9, _)."))
            .add(ParamDef.of("amount", ParamType.DOUBLE, 1.0)
                    .label("Amount").desc("How much to add (negative subtracts)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ITEM; }
    @Override public String displayName() { return "Add Stat"; }
    @Override public String description() { return "Adds to a numeric stat on the trigger item."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return;
        String name = params.getString("name", "stat").trim();
        if (name.isEmpty()) return;
        ctx.registry().addStat(stack, name, params.getDouble("amount", 1.0));
    }
}
