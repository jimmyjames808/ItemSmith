package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Plays the caster's main-hand swing animation. */
public final class SwingHandAction implements Action {

    public static final String ID = "swing_hand";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Swing Hand"; }
    @Override public String description() { return "Plays the caster's main-hand swing animation."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ctx.player().swingMainHand();
    }
}
