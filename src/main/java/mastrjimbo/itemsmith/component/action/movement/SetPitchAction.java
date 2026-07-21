package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;

/** Sets only the caster's pitch, keeping their current yaw. */
public final class SetPitchAction implements Action {

    public static final String ID = "set_pitch";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("pitch", ParamType.DOUBLE, 0.0).label("Pitch").desc("Up/down angle, -90 to 90."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Set Pitch"; }
    @Override public String description() { return "Sets the caster's pitch, keeping their yaw."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        float pitch = (float) params.getDouble("pitch", 0.0);
        player.setRotation(player.getLocation().getYaw(), pitch);
    }
}
