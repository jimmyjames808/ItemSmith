package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;

/**
 * Deals armor- and enchantment-ignoring "true" damage by subtracting directly from the
 * target's health, playing the vanilla hurt effect for feedback. No-op for non-living targets.
 */
public final class TrueDamageAction implements Action {

    public static final String ID = "true_damage";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 4.0)
                    .label("Damage").min(0).desc("Health subtracted directly, ignoring armor/resistance."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "True Damage"; }
    @Override public String description() { return "Deals armor-ignoring damage straight to the target's health."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        double amount = params.getDouble("amount", 4.0);
        if (amount <= 0) return;
        living.setHealth(Math.max(0, living.getHealth() - amount));
        living.playEffect(EntityEffect.HURT);
    }
}
