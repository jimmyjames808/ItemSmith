package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;

/**
 * Drops a freshly built instance of another ItemSmith item on the ground at the
 * caster's feet. No-op if the referenced item id is unknown.
 */
public final class DropCustomItemAction implements Action {

    public static final String ID = "drop_custom_item";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("item", ParamType.ITEM_REF, "")
                    .label("Item").desc("Id of the ItemSmith item to drop."))
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
        return "Drop Custom Item";
    }

    @Override
    public String description() {
        return "Drops another ItemSmith item on the ground at the caster's feet.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String item = params.getString("item", "");
        ItemStack s = ctx.registry().build(item);
        if (s == null) return;
        int amount = Math.max(1, params.getInt("amount", 1));
        s.setAmount(amount);
        ctx.player().getWorld().dropItemNaturally(ctx.player().getLocation(), s);
    }
}
