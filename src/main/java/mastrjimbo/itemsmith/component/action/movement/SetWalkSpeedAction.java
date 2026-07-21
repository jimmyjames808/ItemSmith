package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;

/** Sets the caster's walk speed, clamped to the vanilla-legal range. */
public final class SetWalkSpeedAction implements Action {

    public static final String ID = "set_walk_speed";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("speed", ParamType.DOUBLE, 0.2)
                    .label("Speed").range(-1, 1).desc("Walk speed (-1 to 1; vanilla default is 0.2)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Set Walk Speed"; }
    @Override public String description() { return "Sets the caster's walking speed."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double speed = params.getDouble("speed", 0.2);
        player.setWalkSpeed((float) Math.max(-1, Math.min(1, speed)));
    }
}
