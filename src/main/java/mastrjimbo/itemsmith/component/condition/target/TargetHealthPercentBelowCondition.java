package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

/** Passes when the target's health is below a percentage of its maximum. Fail-closed when there is no living target. */
public final class TargetHealthPercentBelowCondition implements Condition {

    public static final String ID = "target_health_percent_below";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("percent", ParamType.DOUBLE, 50.0)
                    .label("Health %").range(0, 100).desc("Target health as a percent of max must be below this."))
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
        return "Target Health Percent Below";
    }

    @Override
    public String description() {
        return "True when the target's health percent is below the value.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity le = Targets.living(target);
        if (le == null) return false;
        AttributeInstance a = le.getAttribute(Attribute.MAX_HEALTH);
        if (a == null) return false;
        return le.getHealth() / a.getValue() * 100.0 < params.getDouble("percent", 50);
    }
}
