package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

/**
 * Sets the triggering item's resource-pack item model key (the modern
 * replacement for custom model data). No-op if there is no triggering item or
 * the configured model string isn't a valid namespaced key.
 */
public final class SetItemModelAction implements Action {

    public static final String ID = "set_item_model";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("model", ParamType.STRING, "")
                    .label("Model").desc("Namespaced item model key, e.g. myplugin:sword."))
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
        return "Set Item Model";
    }

    @Override
    public String description() {
        return "Sets the triggering item's resource-pack model key.";
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
        String model = params.getString("model", "");
        NamespacedKey key = NamespacedKey.fromString(model.toLowerCase(Locale.ROOT));
        if (key == null) return;
        m.setItemModel(key);
        itemStack.setItemMeta(m);
    }
}
