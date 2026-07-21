package mastrjimbo.itemsmith.component.condition.identity;

import java.util.Locale;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.GameMode;

/** Passes while the caster is in the given game mode. */
public final class GamemodeIsCondition implements Condition {

    public static final String ID = "gamemode_is";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("mode", ParamType.ENUM, "survival")
                    .label("Game Mode").options("survival", "creative", "adventure", "spectator")
                    .desc("Game mode the caster must be in."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.CONDITION;
    }

    @Override
    public String displayName() {
        return "Gamemode Is";
    }

    @Override
    public String description() {
        return "True while the caster is in a specific game mode.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        final GameMode gm;
        try {
            gm = GameMode.valueOf(params.getString("mode", "survival").trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return false;
        }
        return ctx.player().getGameMode() == gm;
    }
}
