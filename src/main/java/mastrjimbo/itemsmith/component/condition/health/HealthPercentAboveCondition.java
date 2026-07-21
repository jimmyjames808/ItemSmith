package mastrjimbo.itemsmith.component.condition.health;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster's health is above the given percentage of their maximum health. */
public final class HealthPercentAboveCondition implements Condition {

    public static final String ID = "health_percent_above";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("percent", ParamType.DOUBLE, 50.0)
                    .label("Percent").range(0, 100).desc("Passes when the caster's health percentage is above this."))
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
        return "Health Percent Above";
    }

    @Override
    public String description() {
        return "True while the caster's health is over a percentage of their max.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        AttributeInstance a = ctx.player().getAttribute(Attribute.MAX_HEALTH);
        if (a == null) return false;
        double max = a.getValue();
        if (max <= 0) return false;
        double percent = params.getDouble("percent", 50.0);
        return ctx.player().getHealth() / max * 100.0 > percent;
    }
}
