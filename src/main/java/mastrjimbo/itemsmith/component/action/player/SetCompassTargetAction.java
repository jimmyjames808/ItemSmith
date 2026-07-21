package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;

/** Points the caster's compass at the target's location. No-op if the target has no location. */
public final class SetCompassTargetAction implements Action {

    public static final String ID = "set_compass_target";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Set Compass Target"; }
    @Override public String description() { return "Points the caster's compass at the target's location."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location l = Targets.location(target);
        if (l != null) ctx.player().setCompassTarget(l);
    }
}
