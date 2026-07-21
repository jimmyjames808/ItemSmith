package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.WeatherType;

/** Overrides the caster's personal client-side weather (clear or downfall), independent of the world weather. */
public final class SetPlayerWeatherAction implements Action {

    public static final String ID = "set_player_weather";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("weather", ParamType.ENUM, "clear")
                    .options("clear", "rain")
                    .label("Weather").desc("The personal weather to show the caster."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Set Player Weather"; }
    @Override public String description() { return "Overrides the caster's personal weather view, independent of the world."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String weather = params.getString("weather", "clear");
        ctx.player().setPlayerWeather("rain".equals(weather) ? WeatherType.DOWNFALL : WeatherType.CLEAR);
    }
}
