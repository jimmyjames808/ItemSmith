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

/** Flings the target away from the caster with a horizontal push plus a little lift. */
public final class KnockbackAction implements Action {

    public static final String ID = "knockback";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("strength", ParamType.DOUBLE, 1.0)
                    .label("Strength").min(0).desc("Horizontal push strength."))
            .add(ParamDef.of("lift", ParamType.DOUBLE, 0.4)
                    .label("Lift").desc("Upward component added to the push."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Knockback"; }
    @Override public String description() { return "Pushes the target away from the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null || ctx.player() == null) return;
        double strength = params.getDouble("strength", 1.0);
        double lift = params.getDouble("lift", 0.4);
        Location from = ctx.player().getLocation();
        Vector dir = entity.getLocation().toVector().subtract(from.toVector());
        if (dir.lengthSquared() < 1.0e-6) dir = from.getDirection();
        dir.setY(0);
        if (dir.lengthSquared() < 1.0e-6) return;
        dir.normalize().multiply(strength).setY(lift);
        entity.setVelocity(entity.getVelocity().add(dir));
    }
}
