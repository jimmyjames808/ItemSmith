package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Appends one MiniMessage-formatted line to the triggering item's existing
 * lore (creating the lore list if it doesn't have one). No-op if there is no
 * triggering item.
 */
public final class AddLoreLineAction implements Action {

    public static final String ID = "add_lore_line";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("line", ParamType.MINIMESSAGE, "")
                    .label("Line").desc("Lore line to append, MiniMessage formatted."))
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
        return "Add Lore Line";
    }

    @Override
    public String description() {
        return "Appends a line to the triggering item's lore.";
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
        String line = params.getString("line", "");
        List<Component> lore = m.lore();
        lore = lore == null ? new ArrayList<>() : new ArrayList<>(lore);
        lore.add(Text.item(line));
        m.lore(lore);
        itemStack.setItemMeta(m);
    }
}
