package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.Entity;

/** Passes when the target is within a distance of the caster. Fail-closed when there is no entity target or different world. */
public final class TargetDistanceBelowCondition implements Condition {

    public static final String ID = "target_distance_below";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("distance", ParamType.DOUBLE, 5.0)
                    .label("Distance").min(0).desc("Target must be closer than this many blocks to the caster."))
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
        return "Target Distance Below";
    }

    @Override
    public String description() {
        return "True when the target is within the distance of the caster.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Entity e = Targets.entity(target);
        if (e == null) return false;
        if (e.getWorld() != ctx.player().getWorld()) return false;
        return e.getLocation().distance(ctx.player().getLocation()) < params.getDouble("distance", 5);
    }
}
