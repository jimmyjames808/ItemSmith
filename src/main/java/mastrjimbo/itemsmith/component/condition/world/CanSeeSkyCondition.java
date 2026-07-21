package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Location;

/** Passes when the caster has open sky above (no solid blocks overhead). */
public final class CanSeeSkyCondition implements Condition {

    public static final String ID = "can_see_sky";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.CONDITION;
    }

    @Override
    public String displayName() {
        return "Can See Sky";
    }

    @Override
    public String description() {
        return "True when the caster has open sky directly above.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Location l = ctx.player().getLocation();
        return l.getBlockY() >= l.getWorld().getHighestBlockYAt(l);
    }
}
