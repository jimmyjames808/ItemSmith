package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;

/** Boosts the caster forward in their look direction, elytra-style. */
public final class FireworkBoostAction implements Action {

    public static final String ID = "firework_boost";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("strength", ParamType.DOUBLE, 1.5)
                    .label("Strength").min(0).desc("Boost strength in the look direction."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Firework Boost"; }
    @Override public String description() { return "Boosts the caster forward, elytra-style."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double strength = params.getDouble("strength", 1.5);
        player.setVelocity(player.getLocation().getDirection().multiply(strength));
    }
}
