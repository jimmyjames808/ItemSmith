package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** A ring of evenly-spaced points around the caster or target — for circular effects. */
public final class RingTargeter implements Targeter {

    public static final String ID = "ring";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.DOUBLE, 3.0)
                    .label("Radius").min(0).desc("Radius of the ring."))
            .add(ParamDef.of("points", ParamType.INT, 8)
                    .label("Points").min(1).desc("How many points around the ring."))
            .add(ParamDef.of("relative_to", ParamType.ENUM, "self")
                    .label("Relative to").options("self", "target")
                    .desc("Center point: the caster or the trigger's target."))
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
        return "Ring of Points";
    }

    @Override
    public String description() {
        return "A ring of evenly-spaced points around the caster or target.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        String relativeTo = params.getString("relative_to", "self").trim().toLowerCase(Locale.ROOT);

        Location center;
        if (relativeTo.equals("target")) {
            center = Targets.location(ctx.eventTarget());
            if (center == null) return List.of();
        } else {
            center = ctx.player().getLocation();
        }

        List<Object> out = new ArrayList<>();
        int n = params.getInt("points", 8);
        double r = params.getDouble("radius", 3);
        for (int i = 0; i < n; i++) {
            double a = 2 * Math.PI * i / n;
            out.add(center.clone().add(r * Math.cos(a), 0, r * Math.sin(a)));
        }
        return out;
    }
}
