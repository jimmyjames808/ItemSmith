package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Location;

import java.util.List;

/** A single location a set distance ahead of the caster's eyes along their look direction. */
public final class LookingDirectionTargeter implements Targeter {

    public static final String ID = "looking_direction";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("distance", ParamType.DOUBLE, 5.0)
                    .label("Distance").desc("How many blocks ahead of the eyes to place the point."))
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
        return "Looking Direction";
    }

    @Override
    public String description() {
        return "A point a set distance ahead of the caster's eyes along their look direction.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        Location eye = ctx.player().getEyeLocation();
        Location p = eye.clone().add(eye.getDirection().multiply(params.getDouble("distance", 5)));
        return List.of(p);
    }
}
