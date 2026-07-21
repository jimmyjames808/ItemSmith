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

/** Teleports the caster forward, along their look direction, by a fixed distance. */
public final class BlinkAction implements Action {

    public static final String ID = "blink";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("distance", ParamType.DOUBLE, 5.0)
                    .label("Distance").min(0).desc("How far forward to blink."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Blink"; }
    @Override public String description() { return "Teleports the caster forward a short distance."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        double distance = params.getDouble("distance", 5.0);
        Location to = player.getEyeLocation().add(player.getLocation().getDirection().multiply(distance));
        player.teleport(to);
    }
}
