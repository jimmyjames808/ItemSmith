package mastrjimbo.itemsmith.component.action.charges;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;

/** Adds charges to the trigger item (clamped to its max). Recharges a KEEP_INERT item. */
public final class AddChargesAction implements Action {

    public static final String ID = "add_charges";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.INT, 1)
                    .label("Amount").desc("Charges to add (negative removes)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ITEM; }
    @Override public String displayName() { return "Add Charges"; }
    @Override public String description() { return "Adds charges to the trigger item, up to its maximum."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return;
        int current = ctx.registry().charges(stack);
        ctx.registry().setCharges(stack, current + params.getInt("amount", 1));
    }
}
