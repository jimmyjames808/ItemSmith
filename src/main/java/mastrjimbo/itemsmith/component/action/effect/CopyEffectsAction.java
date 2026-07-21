package mastrjimbo.itemsmith.component.action.effect;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Copies every active potion effect from the caster onto the target living
 * entity (e.g. "share my buffs"). No-op if the caster is unknown or the target
 * isn't living.
 */
public final class CopyEffectsAction implements Action {

    public static final String ID = "copy_effects";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.EFFECTS; }
    @Override public String displayName() { return "Copy Effects"; }
    @Override public String description() { return "Copies the caster's active potion effects onto the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        Player caster = ctx.player();
        if (caster == null) return;
        for (PotionEffect pe : caster.getActivePotionEffects()) {
            living.addPotionEffect(pe);
        }
    }
}
