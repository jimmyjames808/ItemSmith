package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** Pushes the target entity along the caster's look direction. No-op for non-entity targets. */
public final class PropelAction implements Action {

    public static final String ID = "propel";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("strength", ParamType.DOUBLE, 1.0)
                    .label("Strength").min(0).desc("Push strength along the caster's look direction."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Propel"; }
    @Override public String description() { return "Pushes the target along the caster's look direction."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        Entity entity = Targets.entity(target);
        if (entity == null) return;
        double strength = params.getDouble("strength", 1.0);
        Vector dir = player.getLocation().getDirection().multiply(strength);
        entity.setVelocity(entity.getVelocity().add(dir));
    }
}
