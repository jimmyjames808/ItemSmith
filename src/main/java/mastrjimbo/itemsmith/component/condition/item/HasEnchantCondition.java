package mastrjimbo.itemsmith.component.condition.item;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
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

/** Passes when the trigger item carries a given enchantment at or above a minimum level. */
public final class HasEnchantCondition implements Condition {

    public static final String ID = "has_enchant";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("enchant", ParamType.ENCHANTMENT, "sharpness")
                    .label("Enchantment").desc("Enchantment key (e.g. sharpness, unbreaking)."))
            .add(ParamDef.of("min_level", ParamType.INT, 1)
                    .label("Minimum Level").min(1)
                    .desc("The enchantment must be at least this level."))
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
        return "Has Enchant";
    }

    @Override
    public String description() {
        return "True when the item has an enchantment at or above a minimum level.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack it = ctx.itemStack() != null ? ctx.itemStack() : ctx.player().getInventory().getItemInMainHand();
        if (it == null || it.getType().isAir()) return false;
        Enchantment e = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(params.getString("enchant", "sharpness").toLowerCase(Locale.ROOT)));
        if (e == null) return false;
        return it.getEnchantmentLevel(e) >= params.getInt("min_level", 1);
    }
}
