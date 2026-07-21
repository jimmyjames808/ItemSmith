package mastrjimbo.itemsmith.component.condition.item;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/** Passes when the trigger item's remaining durability is above the given value. */
public final class DurabilityAboveCondition implements Condition {

    public static final String ID = "durability_above";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("value", ParamType.INT, 100)
                    .label("Durability").min(0)
                    .desc("Passes when remaining durability is above this."))
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
        return "Durability Above";
    }

    @Override
    public String description() {
        return "True when the item's remaining durability is above a threshold.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack it = ctx.itemStack() != null ? ctx.itemStack() : ctx.player().getInventory().getItemInMainHand();
        if (it == null || it.getType().isAir()) return false;
        short max = it.getType().getMaxDurability();
        if (max <= 0) return false;
        int dmg = (it.getItemMeta() instanceof Damageable d) ? d.getDamage() : 0;
        int remaining = max - dmg;
        return remaining > params.getInt("value", 100);
    }
}
