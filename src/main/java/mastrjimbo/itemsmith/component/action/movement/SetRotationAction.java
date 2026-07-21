package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;

/** Sets the caster's yaw and pitch directly. */
public final class SetRotationAction implements Action {

    public static final String ID = "set_rotation";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("yaw", ParamType.DOUBLE, 0.0).label("Yaw").desc("Facing angle, 0-360."))
            .add(ParamDef.of("pitch", ParamType.DOUBLE, 0.0).label("Pitch").desc("Up/down angle, -90 to 90."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Set Rotation"; }
    @Override public String description() { return "Sets the caster's yaw and pitch."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        float yaw = (float) params.getDouble("yaw", 0.0);
        float pitch = (float) params.getDouble("pitch", 0.0);
        player.setRotation(yaw, pitch);
    }
}
