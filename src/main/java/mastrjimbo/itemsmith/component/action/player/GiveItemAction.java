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

/**
 * Gives the caster a stack of a vanilla material, added straight into their
 * inventory (Bukkit drops it at their feet if there's no room).
 */
public final class GiveItemAction implements Action {

    public static final String ID = "give_item";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, "DIAMOND")
                    .label("Material").desc("Vanilla item to give."))
            .add(ParamDef.of("amount", ParamType.INT, 1)
                    .label("Amount").min(1).desc("Stack size to give."))
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
        return "Give Item";
    }

    @Override
    public String description() {
        return "Gives the caster a vanilla item.";
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
        ctx.player().getInventory().addItem(new ItemStack(material, amount));
    }
}
