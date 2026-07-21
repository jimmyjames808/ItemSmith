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
 * Sets the base value of the target's MAX_HEALTH attribute. Note this does not itself
 * change current health — vanilla clamps current health down only if it now exceeds the
 * new max. No-op for non-living targets or when the attribute is unavailable.
 */
public final class SetMaxHealthAction implements Action {

    public static final String ID = "set_max_health";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 20.0)
                    .label("Max health").min(1).desc("New base value for the MAX_HEALTH attribute."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Set Max Health"; }
    @Override public String description() { return "Sets the target's maximum health attribute."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        double amount = params.getDouble("amount", 20.0);
        if (amount < 1) return;
        AttributeInstance maxAttr = living.getAttribute(Attribute.MAX_HEALTH);
        if (maxAttr == null) return;
        maxAttr.setBaseValue(amount);
    }
}
