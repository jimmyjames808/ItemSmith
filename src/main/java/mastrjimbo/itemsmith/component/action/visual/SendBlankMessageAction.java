package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import net.kyori.adventure.text.Component;

/** Sends a number of empty chat lines to the caster (handy for spacing out other messages). */
public final class SendBlankMessageAction implements Action {

    public static final String ID = "send_blank_message";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("count", ParamType.INT, 1)
                    .label("Count").range(1, 10).desc("How many blank lines to send."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Send Blank Message"; }
    @Override public String description() { return "Sends one or more empty chat lines to the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        int count = params.getInt("count", 1);
        for (int i = 0; i < count; i++) {
            ctx.player().sendMessage(Component.empty());
        }
    }
}
