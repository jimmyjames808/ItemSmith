package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/** Points sampled along the caster's line of sight — a ray of locations for beams. */
public final class LineTargeter implements Targeter {

    public static final String ID = "line";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("distance", ParamType.DOUBLE, 10.0)
                    .label("Distance").min(0).desc("How far the line extends from the eyes."))
            .add(ParamDef.of("step", ParamType.DOUBLE, 1.0)
                    .label("Step").min(0.1).desc("Spacing between sampled points, in blocks."))
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
        return "Line of Sight";
    }

    @Override
    public String description() {
        return "Points sampled along the caster's line of sight.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        double distance = params.getDouble("distance", 10.0);
        double step = params.getDouble("step", 1.0);

        Location eye = ctx.player().getEyeLocation();
        Vector dir = eye.getDirection().normalize();
        List<Object> out = new ArrayList<>();
        for (double d = step; d <= distance; d += step) {
            out.add(eye.clone().add(dir.clone().multiply(d)));
        }
        return out;
    }
}
