package mastrjimbo.itemsmith.component.condition.identity;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/** Passes while the caster has the given potion effect at or above a minimum amplifier. */
public final class HasPotionEffectCondition implements Condition {

    public static final String ID = "has_potion_effect";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("effect", ParamType.EFFECT, null)
                    .label("Effect").desc("Vanilla effect id the caster must have, e.g. speed, poison."))
            .add(ParamDef.of("min_amplifier", ParamType.INT, 0)
                    .label("Min Amplifier").min(0).desc("Minimum amplifier (0 = level I) required."))
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
        return "Has Potion Effect";
    }

    @Override
    public String description() {
        return "True while the caster has a potion effect at or above a minimum amplifier.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        PotionEffectType t = params.getEffect("effect");
        if (t == null) return false;
        PotionEffect pe = ctx.player().getPotionEffect(t);
        return pe != null && pe.getAmplifier() >= params.getInt("min_amplifier", 0);
    }
}
