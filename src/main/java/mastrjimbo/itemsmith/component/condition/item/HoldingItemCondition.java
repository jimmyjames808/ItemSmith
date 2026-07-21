package mastrjimbo.itemsmith.component.condition.item;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Material;

/** Passes when the caster is holding the given material in their main hand. */
public final class HoldingItemCondition implements Condition {

    public static final String ID = "holding_item";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, null)
                    .label("Material").desc("Material that must be held in the main hand."))
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
        return "Holding Item";
    }

    @Override
    public String description() {
        return "True when the caster holds a given material in their main hand.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Material mat = params.getMaterial("material");
        if (mat == null) return false;
        return ctx.player().getInventory().getItemInMainHand().getType() == mat;
    }
}
