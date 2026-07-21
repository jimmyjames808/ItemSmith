package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** Launches the caster toward the target (or straight ahead if there is none) in an arcing leap. */
public final class LeapAction implements Action {

    public static final String ID = "leap";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("power", ParamType.DOUBLE, 1.2)
                    .label("Power").min(0).desc("Horizontal launch strength."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Leap"; }
    @Override public String description() { return "Launches the caster toward the target in an arc."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double power = params.getDouble("power", 1.2);
        Location playerLoc = player.getLocation();
        Location targetLoc = Targets.location(target);
        Vector dir = targetLoc != null
                ? targetLoc.toVector().subtract(playerLoc.toVector())
                : playerLoc.getDirection();
        if (dir.lengthSquared() < 1.0e-6) dir = playerLoc.getDirection();
        dir.normalize().multiply(power).setY(0.4);
        player.setVelocity(dir);
    }
}
