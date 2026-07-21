package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Replaces the triggering item's lore with the given lines, each MiniMessage
 * formatted. No-op if there is no triggering item.
 */
public final class SetItemLoreAction implements Action {

    public static final String ID = "set_item_lore";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("lore", ParamType.STRING_LIST, List.of())
                    .label("Lore").desc("Lore lines, MiniMessage formatted."))
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
        return "Set Item Lore";
    }

    @Override
    public String description() {
        return "Replaces the triggering item's lore.";
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
        List<String> lore = params.getStringList("lore");
        m.lore(lore.stream().map(Text::item).toList());
        itemStack.setItemMeta(m);
    }
}
