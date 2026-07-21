package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.EntityEffect;
import org.bukkit.entity.LivingEntity;

/** Plays the vanilla "hurt" animation/sound on the target (or the caster, if the target isn't a living entity). */
public final class HurtAnimationAction implements Action {

    public static final String ID = "hurt_animation";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Hurt Animation"; }
    @Override public String description() { return "Plays the hurt animation and sound on the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (target instanceof LivingEntity le) {
            le.playEffect(EntityEffect.HURT);
        } else {
            ctx.player().playEffect(EntityEffect.HURT);
        }
    }
}
