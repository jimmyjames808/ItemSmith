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

import java.util.ArrayList;
import java.util.List;

/** Every entity (living or not) near the caster, optionally capped to a maximum count. */
public final class NearbyEntitiesTargeter implements Targeter {

    public static final String ID = "nearby_entities";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.DOUBLE, 5.0)
                    .label("Radius").min(0).desc("How far around the caster to reach."))
            .add(ParamDef.of("max", ParamType.INT, 0)
                    .label("Max targets").min(0).desc("Cap on how many entities to return (0 = unlimited)."))
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
        return "Nearby Entities";
    }

    @Override
    public String description() {
        return "All entities within a radius of the caster, up to an optional maximum.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        double radius = params.getDouble("radius", 5.0);
        int max = params.getInt("max", 0);

        Location center = ctx.player().getLocation();
        if (center.getWorld() == null) return List.of();

        List<Object> out = new ArrayList<>();
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (e.equals(ctx.player())) continue;
            out.add(e);
            if (max > 0 && out.size() >= max) break;
        }
        return out;
    }
}
