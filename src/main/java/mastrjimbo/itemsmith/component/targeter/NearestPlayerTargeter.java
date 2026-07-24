package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.util.Targets;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

/** The single closest other player to the caster within a radius. */
public final class NearestPlayerTargeter implements Targeter {

    public static final String ID = "nearest_player";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.DOUBLE, 20.0)
                    .label("Radius").min(0).desc("How far around the caster to search."))
            .add(ParamDef.of("relative_to", ParamType.ENUM, "self")
                    .label("Relative to").options("self", "target")
                    .desc("Center point: the caster (self) or the trigger's target (e.g. an impact point)."))
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
        return "Nearest Player";
    }

    @Override
    public String description() {
        return "The single closest other player to the caster within a radius.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        double radius = params.getDouble("radius", 20.0);

        Location center = Targets.center(ctx, params);
        if (center == null) return List.of();
        if (center.getWorld() == null) return List.of();

        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(e instanceof Player) || e.equals(ctx.player())) continue;
            double dist = e.getLocation().distance(center);
            if (dist < closestDist) {
                closestDist = dist;
                closest = e;
            }
        }
        return closest == null ? List.of() : List.of(closest);
    }
}
