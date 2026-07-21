package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Fully repairs the triggering item by zeroing its damage value. No-op if there
 * is no triggering item or its meta isn't {@link Damageable}.
 */
public final class RepairItemAction implements Action {

    public static final String ID = "repair_item";

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
        return "Repair Item";
    }

    @Override
    public String description() {
        return "Fully repairs the triggering item.";
    }

    @Override
    public ParamSchema schema() {
        return ParamSchema.EMPTY;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack itemStack = ctx.itemStack();
        if (itemStack == null) return;
        ItemMeta meta = itemStack.getItemMeta();
        if (!(meta instanceof Damageable d)) return;
        d.setDamage(0);
        itemStack.setItemMeta(d);
    }
}
