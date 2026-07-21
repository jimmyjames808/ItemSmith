package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * Removes an enchantment from the triggering item, if present. No-op if there
 * is no triggering item or the enchant name doesn't resolve.
 */
public final class RemoveEnchantAction implements Action {

    public static final String ID = "remove_enchant";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("enchant", ParamType.ENCHANTMENT, "sharpness")
                    .label("Enchant").desc("Vanilla enchantment id, e.g. sharpness."))
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
        return "Remove Enchant";
    }

    @Override
    public String description() {
        return "Removes an enchantment from the triggering item.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack itemStack = ctx.itemStack();
        if (itemStack == null) return;
        String enchant = params.getString("enchant", "sharpness");
        Enchantment e = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(enchant.trim().toLowerCase(Locale.ROOT)));
        if (e == null) return;
        itemStack.removeEnchantment(e);
    }
}
