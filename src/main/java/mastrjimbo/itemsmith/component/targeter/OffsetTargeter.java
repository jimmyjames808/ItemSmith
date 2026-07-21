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
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Locale;

/** A single point offset from the caster or target, in world axes or the caster's facing frame. */
public final class OffsetTargeter implements Targeter {

    public static final String ID = "offset";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("dx", ParamType.DOUBLE, 0.0)
                    .label("Offset X / Right").desc("World X, or rightward distance in look space."))
            .add(ParamDef.of("dy", ParamType.DOUBLE, 0.0)
                    .label("Offset Y / Up").desc("Vertical offset (up)."))
            .add(ParamDef.of("dz", ParamType.DOUBLE, 0.0)
                    .label("Offset Z / Forward").desc("World Z, or forward distance in look space."))
            .add(ParamDef.of("relative_to", ParamType.ENUM, "self")
                    .label("Relative to").options("self", "target")
                    .desc("Origin point: the caster or the trigger's target."))
            .add(ParamDef.of("space", ParamType.ENUM, "world")
                    .label("Space").options("world", "look")
                    .desc("World axes, or the caster's facing frame (forward/right/up)."))
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
        return "Offset Point";
    }

    @Override
    public String description() {
        return "A point offset from the caster or target.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        double dx = params.getDouble("dx", 0.0);
        double dy = params.getDouble("dy", 0.0);
        double dz = params.getDouble("dz", 0.0);
        String relativeTo = params.getString("relative_to", "self").trim().toLowerCase(Locale.ROOT);
        String space = params.getString("space", "world").trim().toLowerCase(Locale.ROOT);

        Location origin;
        if (relativeTo.equals("target")) {
            origin = Targets.location(ctx.eventTarget());
            if (origin == null) return List.of();
        } else {
            origin = ctx.player().getLocation();
        }

        if (space.equals("look")) {
            Vector dir = ctx.player().getEyeLocation().getDirection();
            double fx = dir.getX();
            double fz = dir.getZ();
            Vector f = new Vector(fx, 0, fz);
            if (f.lengthSquared() < 1e-6) {
                f = new Vector(0, 0, 1);
            } else {
                f.normalize();
            }
            Vector r = new Vector(-f.getZ(), 0, f.getX());
            Location result = origin.clone().add(f.multiply(dz)).add(r.multiply(dx));
            result.add(0, dy, 0);
            return List.of(result);
        }

        return List.of(origin.clone().add(dx, dy, dz));
    }
}
