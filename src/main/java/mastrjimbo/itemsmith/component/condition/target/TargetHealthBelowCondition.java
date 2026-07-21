package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.LivingEntity;

/** Passes when the target's health is below a threshold. Fail-closed when there is no living target. */
public final class TargetHealthBelowCondition implements Condition {

    public static final String ID = "target_health_below";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("value", ParamType.DOUBLE, 10.0)
                    .label("Health").min(0).desc("Target health (in half-heart points) must be below this."))
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
        return "Target Health Below";
    }

    @Override
    public String description() {
        return "True when the target's health is below the value.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity le = Targets.living(target);
        if (le == null) return false;
        return le.getHealth() < params.getDouble("value", 10);
    }
}
