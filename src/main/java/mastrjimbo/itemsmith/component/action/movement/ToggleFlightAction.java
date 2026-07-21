package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;

/** Toggles whether the caster is allowed to fly. */
public final class ToggleFlightAction implements Action {

    public static final String ID = "toggle_flight";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("enabled", ParamType.BOOLEAN, true)
                    .label("Enabled").desc("Whether the caster may fly."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Toggle Flight"; }
    @Override public String description() { return "Grants or revokes the caster's ability to fly."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        boolean enabled = params.getBool("enabled", true);
        player.setAllowFlight(enabled);
        if (!enabled) player.setFlying(false);
    }
}
