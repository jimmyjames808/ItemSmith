package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.GameMode;

import java.util.Locale;

/** Switches the caster's game mode. No-op if the configured mode name is invalid. */
public final class SetGamemodeAction implements Action {

    public static final String ID = "set_gamemode";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("mode", ParamType.ENUM, "survival")
                    .label("Game Mode").options("survival", "creative", "adventure", "spectator")
                    .desc("Game mode to switch the caster to."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Set Game Mode"; }
    @Override public String description() { return "Switches the caster's game mode."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String mode = params.getString("mode", "survival");
        try {
            ctx.player().setGameMode(GameMode.valueOf(mode.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException ignored) {
            // invalid game mode name — no-op
        }
    }
}
