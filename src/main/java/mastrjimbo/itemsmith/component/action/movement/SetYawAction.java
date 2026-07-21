package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;

/** Sets only the caster's yaw, keeping their current pitch. */
public final class SetYawAction implements Action {

    public static final String ID = "set_yaw";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("yaw", ParamType.DOUBLE, 0.0).label("Yaw").desc("Facing angle, 0-360."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Set Yaw"; }
    @Override public String description() { return "Sets the caster's yaw, keeping their pitch."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        float yaw = (float) params.getDouble("yaw", 0.0);
        player.setRotation(yaw, player.getLocation().getPitch());
    }
}
