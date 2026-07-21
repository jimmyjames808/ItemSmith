package mastrjimbo.itemsmith.component.condition.item;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;

/** Passes when the caster is wearing the given material in a chosen armor slot. */
public final class WearingCondition implements Condition {

    public static final String ID = "wearing";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, null)
                    .label("Material").desc("Material that must be worn in the chosen slot."))
            .add(ParamDef.of("slot", ParamType.ENUM, "head")
                    .label("Slot").options("head", "chest", "legs", "feet")
                    .desc("Which armor slot to check."))
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
        return "Wearing";
    }

    @Override
    public String description() {
        return "True when the caster wears a given material in an armor slot.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Material mat = params.getMaterial("material");
        if (mat == null) return false;
        PlayerInventory inv = ctx.player().getInventory();
        ItemStack armor;
        switch (params.getString("slot", "head").trim().toLowerCase(Locale.ROOT)) {
            case "chest" -> armor = inv.getChestplate();
            case "legs" -> armor = inv.getLeggings();
            case "feet" -> armor = inv.getBoots();
            default -> armor = inv.getHelmet();
        }
        return armor != null && armor.getType() == mat;
    }
}
