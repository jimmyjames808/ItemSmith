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

import java.util.ArrayList;
import java.util.List;

/** Every other player within a radius of the caster. */
public final class NearbyPlayersTargeter implements Targeter {

    public static final String ID = "nearby_players";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.DOUBLE, 8.0)
                    .label("Radius").min(0).desc("How far around the caster to reach."))
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
        return "Nearby Players";
    }

    @Override
    public String description() {
        return "All other players within a radius of the caster.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        double radius = params.getDouble("radius", 8.0);

        Location center = Targets.center(ctx, params);
        if (center == null) return List.of();
        if (center.getWorld() == null) return List.of();

        List<Object> out = new ArrayList<>();
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (e instanceof Player && !e.equals(ctx.player())) {
                out.add(e);
            }
        }
        return out;
    }
}
