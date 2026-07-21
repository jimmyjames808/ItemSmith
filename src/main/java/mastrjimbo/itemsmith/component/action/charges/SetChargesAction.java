package mastrjimbo.itemsmith.component.action.charges;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;

/** Sets the trigger item's charges to a fixed value (clamped to its max). */
public final class SetChargesAction implements Action {

    public static final String ID = "set_charges";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.INT, 1)
                    .label("Amount").min(0).desc("Charges to set the item to."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ITEM; }
    @Override public String displayName() { return "Set Charges"; }
    @Override public String description() { return "Sets the trigger item's charge counter."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return;
        ctx.registry().setCharges(stack, params.getInt("amount", 1));
    }
}
