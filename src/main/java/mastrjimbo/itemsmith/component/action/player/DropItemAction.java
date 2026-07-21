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

/** Drops a stack of a vanilla material on the ground at the caster's feet. */
public final class DropItemAction implements Action {

    public static final String ID = "drop_item";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, "DIAMOND")
                    .label("Material").desc("Vanilla item to drop."))
            .add(ParamDef.of("amount", ParamType.INT, 1)
                    .label("Amount").min(1).desc("Stack size to drop."))
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
        return "Drop Item";
    }

    @Override
    public String description() {
        return "Drops a vanilla item on the ground at the caster's feet.";
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
        ctx.player().getWorld().dropItemNaturally(ctx.player().getLocation(), new ItemStack(material, amount));
    }
}
