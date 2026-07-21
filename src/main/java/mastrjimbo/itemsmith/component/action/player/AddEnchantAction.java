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
 * Adds an enchantment to the triggering item at an arbitrary level, bypassing
 * vanilla level caps and conflict rules. No-op if there is no triggering item
 * or the enchant name doesn't resolve.
 */
public final class AddEnchantAction implements Action {

    public static final String ID = "add_enchant";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("enchant", ParamType.ENCHANTMENT, "sharpness")
                    .label("Enchant").desc("Vanilla enchantment id, e.g. sharpness."))
            .add(ParamDef.of("level", ParamType.INT, 1)
                    .label("Level").min(1).desc("Enchantment level to apply."))
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
        return "Add Enchant";
    }

    @Override
    public String description() {
        return "Adds an enchantment to the triggering item.";
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
        int level = Math.max(1, params.getInt("level", 1));
        itemStack.addUnsafeEnchantment(e, level);
    }
}
