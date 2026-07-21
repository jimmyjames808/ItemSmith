package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Adjusts the triggering item's damage value by a relative amount, clamped at
 * zero. Positive amounts damage the item, negative amounts repair it. No-op if
 * there is no triggering item or its meta isn't {@link Damageable}.
 */
public final class ModifyDurabilityAction implements Action {

    public static final String ID = "modify_durability";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.INT, -10)
                    .label("Amount").desc("Damage delta; positive damages, negative repairs."))
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
        return "Modify Durability";
    }

    @Override
    public String description() {
        return "Adjusts the triggering item's damage by a relative amount.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack itemStack = ctx.itemStack();
        if (itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof Damageable d)) return;
        int amount = params.getInt("amount", -10);
        d.setDamage(Math.max(0, d.getDamage() + amount));
        itemStack.setItemMeta(d);
    }
}
