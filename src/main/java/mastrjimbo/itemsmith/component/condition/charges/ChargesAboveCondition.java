package mastrjimbo.itemsmith.component.condition.charges;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;

/** Passes when the trigger item has more than the given number of charges. */
public final class ChargesAboveCondition implements Condition {

    public static final String ID = "charges_above";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.INT, 0)
                    .label("Amount").min(0).desc("Passes when charges are above this."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.CONDITION; }
    @Override public String displayName() { return "Charges Above"; }
    @Override public String description() { return "True when the item has more than this many charges."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return false;
        return ctx.registry().charges(stack) > params.getInt("amount", 0);
    }
}
