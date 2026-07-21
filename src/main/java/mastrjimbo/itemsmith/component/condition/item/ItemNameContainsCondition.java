package mastrjimbo.itemsmith.component.condition.item;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

/** Passes when the trigger item's display name contains the given text (case-insensitive). */
public final class ItemNameContainsCondition implements Condition {

    public static final String ID = "item_name_contains";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("text", ParamType.STRING, "")
                    .label("Text").desc("Substring to look for in the item's display name."))
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
        return "Item Name Contains";
    }

    @Override
    public String description() {
        return "True when the item's display name contains a substring.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack it = ctx.itemStack() != null ? ctx.itemStack() : ctx.player().getInventory().getItemInMainHand();
        if (it == null || it.getType().isAir()) return false;
        ItemMeta m = it.getItemMeta();
        if (m == null || !m.hasDisplayName()) return false;
        String plain = PlainTextComponentSerializer.plainText().serialize(m.displayName());
        return plain.toLowerCase(Locale.ROOT).contains(params.getString("text", "").toLowerCase(Locale.ROOT));
    }
}
