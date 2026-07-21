package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Text;

/**
 * Sends a chat line approximately centered in the default chat width, by left-padding
 * with spaces based on the raw text length (a rough approximation, not a pixel-accurate
 * MiniMessage-aware centering).
 */
public final class SendCenteredMessageAction implements Action {

    public static final String ID = "send_centered_message";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("text", ParamType.MINIMESSAGE, "")
                    .label("Text").desc("MiniMessage text to center in chat."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Send Centered Message"; }
    @Override public String description() { return "Sends a roughly centered chat line to the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String text = params.getString("text", "");
        if (text.isBlank()) return;
        int pad = Math.max(0, (40 - text.length()) / 2);
        ctx.player().sendMessage(Text.chat(" ".repeat(pad) + text));
    }
}
