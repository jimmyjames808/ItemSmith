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

import java.util.ArrayList;
import java.util.List;

/** Every entity within a radius of the caster — the basic area-of-effect selector. */
public final class RadiusTargeter implements Targeter {

    public static final String ID = "radius";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.DOUBLE, 4.0)
                    .label("Radius").min(0).desc("How far around the caster to reach."))
            .add(ParamDef.of("living_only", ParamType.BOOLEAN, true)
                    .label("Living only").desc("Only include living entities (mobs/players)."))
            .add(ParamDef.of("include_self", ParamType.BOOLEAN, false)
                    .label("Include self").desc("Also affect the caster."))
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
        return "In Radius";
    }

    @Override
    public String description() {
        return "All entities within a radius of the caster.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        double radius = params.getDouble("radius", 4.0);
        boolean livingOnly = params.getBool("living_only", true);
        boolean includeSelf = params.getBool("include_self", false);

        Location center = ctx.player().getLocation();
        if (center.getWorld() == null) return List.of();

        List<Object> out = new ArrayList<>();
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (livingOnly && !(e instanceof LivingEntity)) continue;
            if (!includeSelf && e.equals(ctx.player())) continue;
            out.add(e);
        }
        return out;
    }
}
