package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;

import java.util.List;

/** The location of the trigger's natural target — where the hit entity/block/point is. */
public final class LocationOfTargetTargeter implements Targeter {

    public static final String ID = "location_of_target";

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
        return "Location of Target";
    }

    @Override
    public String description() {
        return "The location of the trigger's natural target.";
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        Location l = Targets.location(ctx.eventTarget());
        return l == null ? List.of() : List.of(l);
    }
}
