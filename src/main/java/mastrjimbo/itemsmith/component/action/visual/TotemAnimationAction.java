package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Player;

/** Plays the totem-of-undying resurrection animation on the target (or the caster if not a player). */
public final class TotemAnimationAction implements Action {

    public static final String ID = "totem_animation";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Totem Animation"; }
    @Override public String description() { return "Plays the totem-of-undying resurrection animation."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (target instanceof Player tp) {
            tp.playEffect(EntityEffect.TOTEM_RESURRECT);
        } else {
            ctx.player().playEffect(EntityEffect.TOTEM_RESURRECT);
        }
    }
}
