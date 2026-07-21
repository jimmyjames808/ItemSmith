package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/** Teleports the caster to an offset from their current location. */
public final class TeleportRelativeAction implements Action {

    public static final String ID = "teleport_relative";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("x", ParamType.DOUBLE, 0.0).label("X Offset").desc("Blocks to shift east/west."))
            .add(ParamDef.of("y", ParamType.DOUBLE, 0.0).label("Y Offset").desc("Blocks to shift up/down."))
            .add(ParamDef.of("z", ParamType.DOUBLE, 0.0).label("Z Offset").desc("Blocks to shift north/south."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Teleport Relative"; }
    @Override public String description() { return "Teleports the caster by an offset from their current position."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double x = params.getDouble("x", 0.0);
        double y = params.getDouble("y", 0.0);
        double z = params.getDouble("z", 0.0);
        Location loc = player.getLocation().clone().add(x, y, z);
        player.teleport(loc);
    }
}
