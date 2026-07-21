package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Sets the triggering item's legacy custom model data integer (for resource
 * pack overrides). No-op if there is no triggering item.
 */
public final class SetCustomModelDataAction implements Action {

    public static final String ID = "set_custom_model_data";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("value", ParamType.INT, 0)
                    .label("Value").desc("Custom model data integer."))
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
        return "Set Custom Model Data";
    }

    @Override
    public String description() {
        return "Sets the triggering item's custom model data.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack itemStack = ctx.itemStack();
        if (itemStack == null) return;
        ItemMeta m = itemStack.getItemMeta();
        int value = params.getInt("value", 0);
        m.setCustomModelData(value);
        itemStack.setItemMeta(m);
    }
}
