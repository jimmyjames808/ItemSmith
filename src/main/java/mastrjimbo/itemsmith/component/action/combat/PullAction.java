package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/** Pulls the target toward the caster — the mirror image of {@link KnockbackAction}. */
public final class PullAction implements Action {

    public static final String ID = "pull";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("strength", ParamType.DOUBLE, 1.0)
                    .label("Strength").min(0).desc("Horizontal pull strength."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Pull"; }
    @Override public String description() { return "Pulls the target toward the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null || ctx.player() == null) return;
        double strength = params.getDouble("strength", 1.0);
        if (strength <= 0) return;
        Location from = ctx.player().getLocation();
        Vector dir = from.toVector().subtract(entity.getLocation().toVector());
        dir.setY(0);
        if (dir.lengthSquared() < 1.0e-6) return;
        dir.normalize().multiply(strength);
        entity.setVelocity(entity.getVelocity().add(dir));
    }
}
