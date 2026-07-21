package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Text;

/** Sends a MiniMessage-formatted action bar message to the caster. */
public final class SendActionbarAction implements Action {

    public static final String ID = "send_actionbar";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("text", ParamType.MINIMESSAGE, "")
                    .label("Text").desc("MiniMessage text to show on the action bar."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Send Action Bar"; }
    @Override public String description() { return "Shows a message on the caster's action bar."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String text = params.getString("text", "");
        if (text.isBlank()) return;
        ctx.player().sendActionBar(Text.chat(text));
    }
}
