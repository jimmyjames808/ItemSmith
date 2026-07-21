package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Launches the target with an upward and (optionally) forward push, the forward
 * direction taken from the caster's look direction. No-op for non-entity targets.
 */
public final class LaunchEntityAction implements Action {

    public static final String ID = "launch_entity";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("up", ParamType.DOUBLE, 1.0)
                    .label("Upward force").desc("Vertical velocity added to the target."))
            .add(ParamDef.of("forward", ParamType.DOUBLE, 0.0)
                    .label("Forward force").desc("Horizontal velocity added along the caster's look direction."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Launch Entity"; }
    @Override public String description() { return "Launches the target upward and/or forward."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null) return;
        double up = params.getDouble("up", 1.0);
        double forward = params.getDouble("forward", 0.0);
        Vector vel = entity.getVelocity();
        vel.add(new Vector(0, up, 0));
        if (forward != 0 && ctx.player() != null) {
            Vector look = ctx.player().getLocation().getDirection();
            look.setY(0);
            if (look.lengthSquared() > 1.0e-6) {
                look.normalize().multiply(forward);
                vel.add(look);
            }
        }
        entity.setVelocity(vel);
    }
}
