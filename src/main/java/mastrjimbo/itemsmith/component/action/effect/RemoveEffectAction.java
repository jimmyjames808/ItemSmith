package mastrjimbo.itemsmith.component.action.effect;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

/**
 * Removes a single named potion effect from the target living entity, if present.
 * No-op for non-living targets or if the effect isn't active.
 */
public final class RemoveEffectAction implements Action {

    public static final String ID = "remove_effect";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("effect", ParamType.EFFECT, null)
                    .label("Effect").desc("Vanilla effect id to strip, e.g. poison, slowness."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.EFFECTS; }
    @Override public String displayName() { return "Remove Effect"; }
    @Override public String description() { return "Removes a named potion effect from the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        PotionEffectType type = params.getEffect("effect");
        if (type == null) return;
        living.removePotionEffect(type);
    }
}
