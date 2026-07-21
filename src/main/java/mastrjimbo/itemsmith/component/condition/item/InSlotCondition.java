package mastrjimbo.itemsmith.component.condition.item;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes when the caster's selected hotbar slot matches the given index (0-8). */
public final class InSlotCondition implements Condition {

    public static final String ID = "in_slot";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("slot", ParamType.INT, 0)
                    .label("Slot").range(0, 8)
                    .desc("The hotbar slot index that must be selected (0-8)."))
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
        return "In Slot";
    }

    @Override
    public String description() {
        return "True when the caster's selected hotbar slot matches an index.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getInventory().getHeldItemSlot() == params.getInt("slot", 0);
    }
}
