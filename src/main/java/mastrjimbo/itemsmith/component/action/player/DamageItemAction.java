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
 * Adds a flat amount to the triggering item's damage value. No-op if there is
 * no triggering item or its meta isn't {@link Damageable}.
 */
public final class DamageItemAction implements Action {

    public static final String ID = "damage_item";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.INT, 10)
                    .label("Amount").min(0).desc("Damage to add."))
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
        return "Damage Item";
    }

    @Override
    public String description() {
        return "Adds damage to the triggering item.";
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
        int amount = params.getInt("amount", 10);
        d.setDamage(d.getDamage() + amount);
        itemStack.setItemMeta(d);
    }
}
