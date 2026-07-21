package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Drops the target's main-hand item on the ground and empties the hand.
 * No-op for non-living targets, or targets with no equipment/empty hand.
 */
public final class DisarmAction implements Action {

    public static final String ID = "disarm";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Disarm"; }
    @Override public String description() { return "Drops the target's held item and empties its hand."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        EntityEquipment eq = living.getEquipment();
        if (eq == null) return;
        ItemStack held = eq.getItemInMainHand();
        if (held != null && !held.getType().isAir()) {
            living.getWorld().dropItemNaturally(living.getLocation(), held);
            eq.setItemInMainHand(null);
        }
    }
}
