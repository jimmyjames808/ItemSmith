package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.World;

import java.util.Locale;

/** Passes when the caster's world weather matches the selected state (clear, rain or thunder). */
public final class WeatherIsCondition implements Condition {

    public static final String ID = "weather_is";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("weather", ParamType.ENUM, "clear")
                    .label("Weather").options("clear", "rain", "thunder")
                    .desc("Which weather state must be active in the caster's world."))
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
        return "Weather Is";
    }

    @Override
    public String description() {
        return "True when the world's weather matches the chosen state.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        World w = ctx.player().getWorld();
        String want = params.getString("weather", "clear");
        boolean rain = w.hasStorm();
        boolean thunder = w.isThundering();
        switch (want.toLowerCase(Locale.ROOT)) {
            case "clear":
                return !rain && !thunder;
            case "rain":
                return rain && !thunder;
            case "thunder":
                return thunder;
            default:
                return false;
        }
    }
}
