package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Locale;

/** Launches a real firework rocket from the target's location that flies and detonates naturally. */
public final class SpawnFireworkAction implements Action {

    public static final String ID = "spawn_firework";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("color", ParamType.STRING, "blue")
                    .label("Color").desc("Firework effect color."))
            .add(ParamDef.of("shape", ParamType.ENUM, "BALL")
                    .options("BALL", "BALL_LARGE", "STAR", "BURST", "CREEPER")
                    .label("Shape").desc("Firework effect shape."))
            .add(ParamDef.of("power", ParamType.INT, 1)
                    .label("Power").min(0).desc("Flight duration/height before it detonates."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Spawn Firework"; }
    @Override public String description() { return "Launches a firework rocket that flies up from the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location l = Targets.location(target);
        if (l == null) l = ctx.player().getLocation();
        World w = l.getWorld();
        if (w == null) return;
        String color = params.getString("color", "blue");
        String type = params.getString("shape", "BALL");
        int power = params.getInt("power", 1);

        Firework fw = (Firework) w.spawnEntity(l, EntityType.FIREWORK_ROCKET);
        // (A firework's launch/blast sounds are positional world sounds Bukkit can't mute — inherent.)
        fw.getPersistentDataContainer().set(
                new org.bukkit.NamespacedKey(ctx.plugin(), mastrjimbo.itemsmith.listener.FireworkListener.COSMETIC_KEY),
                org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder().withColor(parseColor(color)).with(parseType(type)).build());
        fm.setPower(Math.max(0, power));
        fw.setFireworkMeta(fm);
    }

    private static FireworkEffect.Type parseType(String type) {
        try {
            return FireworkEffect.Type.valueOf(type.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return FireworkEffect.Type.BALL;
        }
    }

    private static Color parseColor(String name) {
        return switch (name.trim().toLowerCase(Locale.ROOT)) {
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            case "green" -> Color.GREEN;
            case "yellow" -> Color.YELLOW;
            case "white" -> Color.WHITE;
            case "purple" -> Color.PURPLE;
            case "orange" -> Color.ORANGE;
            default -> Color.RED;
        };
    }
}
