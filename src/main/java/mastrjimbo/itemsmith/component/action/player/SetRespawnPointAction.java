package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Sets the caster's respawn point to their current location, with the "bed spawn" message shown. */
public final class SetRespawnPointAction implements Action {

    public static final String ID = "set_respawn_point";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Set Respawn Point"; }
    @Override public String description() { return "Sets the caster's respawn point to their current location."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ctx.player().setRespawnLocation(ctx.player().getLocation(), true);
    }
}
