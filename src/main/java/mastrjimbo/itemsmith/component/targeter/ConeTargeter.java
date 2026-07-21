package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/** Entities in a cone in front of the caster — a directional area selector. */
public final class ConeTargeter implements Targeter {

    public static final String ID = "cone";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("range", ParamType.DOUBLE, 8.0)
                    .label("Range").min(0).desc("How far the cone reaches."))
            .add(ParamDef.of("angle", ParamType.DOUBLE, 45.0)
                    .label("Angle").desc("Half-cone tolerance, in degrees."))
            .add(ParamDef.of("living_only", ParamType.BOOLEAN, true)
                    .label("Living only").desc("Only include living entities (mobs/players)."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.TARGETER;
    }

    @Override
    public String displayName() {
        return "In Cone";
    }

    @Override
    public String description() {
        return "Entities in a cone in front of the caster.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        double range = params.getDouble("range", 8.0);
        boolean livingOnly = params.getBool("living_only", true);

        Location eye = ctx.player().getEyeLocation();
        Vector dir = eye.getDirection().normalize();
        double halfRad = Math.toRadians(params.getDouble("angle", 45));

        List<Object> out = new ArrayList<>();
        for (Entity e : ctx.player().getWorld().getNearbyEntities(ctx.player().getLocation(), range, range, range)) {
            if (e.equals(ctx.player())) continue;
            if (livingOnly && !(e instanceof LivingEntity)) continue;
            Vector to = e.getLocation().toVector().subtract(eye.toVector());
            if (to.lengthSquared() < 1e-6) continue;
            if (dir.angle(to) <= halfRad) out.add(e);
        }
        return out;
    }
}
