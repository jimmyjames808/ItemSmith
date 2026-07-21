package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

import java.util.List;

/** The caster's own location — a single point where the player is standing. */
public final class LocationOfSelfTargeter implements Targeter {

    public static final String ID = "location_of_self";

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
        return "Location of Self";
    }

    @Override
    public String description() {
        return "The caster's own location.";
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        return List.of(ctx.player().getLocation());
    }
}
