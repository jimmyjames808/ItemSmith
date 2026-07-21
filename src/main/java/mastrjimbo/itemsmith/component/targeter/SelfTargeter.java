package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

import java.util.List;

/** Targets the caster themselves — actions apply to the player who triggered the ability. */
public final class SelfTargeter implements Targeter {

    public static final String ID = "self";

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
        return "Self";
    }

    @Override
    public String description() {
        return "The player holding the item.";
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        return List.of(ctx.player());
    }
}
