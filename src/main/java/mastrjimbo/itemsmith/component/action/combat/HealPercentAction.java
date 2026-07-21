package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

/**
 * Heals the target for a percentage of its maximum health, capped at that max.
 * No-op for non-living targets.
 */
public final class HealPercentAction implements Action {

    public static final String ID = "heal_percent";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("percent", ParamType.DOUBLE, 0.25)
                    .label("Percent of max health").range(0, 1).desc("Fraction of the target's max health restored."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Heal Percent"; }
    @Override public String description() { return "Restores health equal to a percentage of the target's max health."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        double percent = params.getDouble("percent", 0.25);
        if (percent <= 0) return;
        AttributeInstance maxAttr = living.getAttribute(Attribute.MAX_HEALTH);
        double max = maxAttr != null ? maxAttr.getValue() : 20.0;
        double amount = percent * max;
        living.setHealth(Math.min(max, living.getHealth() + amount));
    }
}
