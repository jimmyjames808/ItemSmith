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

/**
 * Dashes the caster by a velocity vector. By default the vector is relative to where the player is
 * facing — {@code forward} (z) goes the way they look, {@code strafe} (x) to their right, {@code up}
 * (y) straight up — which is what a dash should feel like. Set {@code relative: false} to use raw
 * world-axis x/y/z instead.
 */
public final class CustomDashAction implements Action {

    public static final String ID = "custom_dash";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("z", ParamType.DOUBLE, 1.0).label("Forward").desc("Velocity in the look direction (relative)."))
            .add(ParamDef.of("x", ParamType.DOUBLE, 0.0).label("Strafe").desc("Velocity to the player's right (relative)."))
            .add(ParamDef.of("y", ParamType.DOUBLE, 0.4).label("Up").desc("Vertical velocity."))
            .add(ParamDef.of("relative", ParamType.BOOLEAN, true)
                    .label("Relative to facing").desc("If false, x/y/z are raw world axes instead."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Custom Dash"; }
    @Override public String description() { return "Dashes the caster by a vector (relative to facing by default)."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double x = params.getDouble("x", 0.0);
        double y = params.getDouble("y", 0.4);
        double z = params.getDouble("z", 1.0);
        if (!params.getBool("relative", true)) {
            player.setVelocity(new Vector(x, y, z));
            return;
        }
        // Facing-relative: build a horizontal forward + right basis from the look direction.
        Vector forward = player.getLocation().getDirection().setY(0);
        if (forward.lengthSquared() < 1.0e-6) forward = new Vector(0, 0, 1);
        forward.normalize();
        double fx = forward.getX();
        double fz = forward.getZ();
        // right = forward rotated -90° about Y = (-fz, 0, fx)
        player.setVelocity(new Vector(fx * z - fz * x, y, fz * z + fx * x));
    }
}
