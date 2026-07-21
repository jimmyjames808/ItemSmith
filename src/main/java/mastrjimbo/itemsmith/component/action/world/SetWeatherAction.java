package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.World;

/** Sets the caster's world weather to clear, rain, or thunder. */
public final class SetWeatherAction implements Action {

    public static final String ID = "set_weather";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("weather", ParamType.ENUM, "clear")
                    .options("clear", "rain", "thunder")
                    .label("Weather").desc("The world weather state to set."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Set Weather"; }
    @Override public String description() { return "Changes the caster's world weather to clear, rain, or thunder."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        World w = ctx.player().getWorld();
        String weather = params.getString("weather", "clear");
        switch (weather) {
            case "rain" -> {
                w.setStorm(true);
                w.setThundering(false);
            }
            case "thunder" -> {
                w.setStorm(true);
                w.setThundering(true);
            }
            default -> {
                w.setStorm(false);
                w.setThundering(false);
            }
        }
    }
}
