package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/** Removes a stack of a vanilla material from the caster's inventory, if present. */
public final class ConsumeItemAction implements Action {

    public static final String ID = "consume_item";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, "DIAMOND")
                    .label("Material").desc("Vanilla item to consume."))
            .add(ParamDef.of("amount", ParamType.INT, 1)
                    .label("Amount").min(1).desc("Amount to remove."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.PLAYER;
    }

    @Override
    public String displayName() {
        return "Consume Item";
    }

    @Override
    public String description() {
        return "Removes a vanilla item from the caster's inventory.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Material material = params.getMaterial("material");
        if (material == null) return;
        int amount = Math.max(1, params.getInt("amount", 1));
        ctx.player().getInventory().removeItem(new ItemStack(material, amount));
    }
}
