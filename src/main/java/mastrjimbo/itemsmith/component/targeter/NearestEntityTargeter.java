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

import java.util.List;

/** The single closest entity to the caster within a radius. */
public final class NearestEntityTargeter implements Targeter {

    public static final String ID = "nearest_entity";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.DOUBLE, 20.0)
                    .label("Radius").min(0).desc("How far around the caster to search."))
            .add(ParamDef.of("living_only", ParamType.BOOLEAN, true)
                    .label("Living only").desc("Only consider living entities (mobs/players)."))
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
        return "Nearest Entity";
    }

    @Override
    public String description() {
        return "The single closest entity to the caster within a radius.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        double radius = params.getDouble("radius", 20.0);
        boolean livingOnly = params.getBool("living_only", true);

        Location center = ctx.player().getLocation();
        if (center.getWorld() == null) return List.of();

        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (e.equals(ctx.player())) continue;
            if (livingOnly && !(e instanceof LivingEntity)) continue;
            double dist = e.getLocation().distance(center);
            if (dist < closestDist) {
                closestDist = dist;
                closest = e;
            }
        }
        return closest == null ? List.of() : List.of(closest);
    }
}
