package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** Sets the caster's velocity backward, opposite their look direction. */
public final class DashBackwardAction implements Action {

    public static final String ID = "dash_backward";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("strength", ParamType.DOUBLE, 1.5)
                    .label("Strength").min(0).desc("How hard to dash backward."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Dash Backward"; }
    @Override public String description() { return "Dashes the caster backward, opposite their look direction."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double strength = params.getDouble("strength", 1.5);
        Vector dir = player.getLocation().getDirection().multiply(-strength);
        player.setVelocity(dir);
    }
}
