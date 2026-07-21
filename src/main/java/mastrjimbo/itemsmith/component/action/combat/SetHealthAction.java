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
 * Sets the target's health outright, clamped to {@code [0, MAX_HEALTH]}. No-op for
 * non-living targets.
 */
public final class SetHealthAction implements Action {

    public static final String ID = "set_health";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 20.0)
                    .label("Health").min(0).desc("Health to set (clamped to the target's max health)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Set Health"; }
    @Override public String description() { return "Sets the target's health to an exact value."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        double amount = params.getDouble("amount", 20.0);
        AttributeInstance maxAttr = living.getAttribute(Attribute.MAX_HEALTH);
        double max = maxAttr != null ? maxAttr.getValue() : 20.0;
        living.setHealth(Math.max(0, Math.min(max, amount)));
    }
}
