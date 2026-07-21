package mastrjimbo.itemsmith.component.action;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Applies a potion effect to the target living entity. Duration is authored in
 * seconds (friendlier than ticks) and converted here. No-ops for non-living
 * targets such as blocks.
 */
public final class PotionEffectAction implements Action {

    public static final String ID = "potion_effect";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("effect", ParamType.EFFECT, null)
                    .label("Effect").desc("Vanilla effect id, e.g. poison, wither, slowness."))
            .add(ParamDef.of("duration", ParamType.INT, 5)
                    .label("Duration (seconds)").min(1).desc("How long the effect lasts."))
            .add(ParamDef.of("amplifier", ParamType.INT, 0)
                    .label("Level (0 = I)").min(0).desc("0 = level I, 1 = level II, ..."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.EFFECTS;
    }

    @Override
    public String displayName() {
        return "Potion Effect";
    }

    @Override
    public String description() {
        return "Applies a potion effect to the target.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (!(target instanceof LivingEntity living)) return;
        PotionEffectType type = params.getEffect("effect");
        if (type == null) return;
        int durationTicks = Math.max(1, params.getInt("duration", 5)) * 20;
        int amplifier = Math.max(0, params.getInt("amplifier", 0));
        living.addPotionEffect(new PotionEffect(type, durationTicks, amplifier));
    }
}
