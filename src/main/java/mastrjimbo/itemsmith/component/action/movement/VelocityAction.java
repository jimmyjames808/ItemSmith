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

/** Sets, or adds, the caster's velocity to (x, y, z). */
public final class VelocityAction implements Action {

    public static final String ID = "velocity";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("x", ParamType.DOUBLE, 0.0).label("X").desc("Velocity X component."))
            .add(ParamDef.of("y", ParamType.DOUBLE, 0.0).label("Y").desc("Velocity Y component."))
            .add(ParamDef.of("z", ParamType.DOUBLE, 0.0).label("Z").desc("Velocity Z component."))
            .add(ParamDef.of("add", ParamType.BOOLEAN, false)
                    .label("Add to current").desc("If true, adds to the caster's existing velocity instead of replacing it."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Velocity"; }
    @Override public String description() { return "Sets or adds to the caster's velocity."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double x = params.getDouble("x", 0.0);
        double y = params.getDouble("y", 0.0);
        double z = params.getDouble("z", 0.0);
        boolean add = params.getBool("add", false);
        Vector vec = new Vector(x, y, z);
        player.setVelocity(add ? player.getVelocity().add(vec) : vec);
    }
}
