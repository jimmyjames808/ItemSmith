package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

/** Passes when the target has a given potion effect. Fail-closed when there is no living target or unknown effect. */
public final class TargetHasEffectCondition implements Condition {

    public static final String ID = "target_has_effect";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("effect", ParamType.EFFECT, null)
                    .label("Effect").desc("Vanilla effect id the target must have, e.g. speed, poison."))
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
        return "Target Has Effect";
    }

    @Override
    public String description() {
        return "True when the target has the given potion effect.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity le = Targets.living(target);
        PotionEffectType t = params.getEffect("effect");
        if (le == null || t == null) return false;
        return le.hasPotionEffect(t);
    }
}
