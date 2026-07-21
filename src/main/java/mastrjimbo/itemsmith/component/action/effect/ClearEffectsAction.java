package mastrjimbo.itemsmith.component.action.effect;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

/**
 * Strips every active potion effect from the target living entity — a full
 * cleanse. No-op for non-living targets.
 */
public final class ClearEffectsAction implements Action {

    public static final String ID = "clear_effects";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.EFFECTS; }
    @Override public String displayName() { return "Clear Effects"; }
    @Override public String description() { return "Removes all active potion effects from the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        for (PotionEffect pe : living.getActivePotionEffects()) {
            living.removePotionEffect(pe.getType());
        }
    }
}
