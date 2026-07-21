package mastrjimbo.itemsmith.component.action.command;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Text;

/**
 * Broadcasts a MiniMessage-formatted line to every player on the server.
 * Supports the {@code {player}} and {@code {item}} placeholders.
 */
public final class BroadcastAction implements Action {

    public static final String ID = "broadcast";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("text", ParamType.MINIMESSAGE, "")
                    .label("Message").desc("MiniMessage text to broadcast. {player} and {item} are substituted."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMMAND; }
    @Override public String displayName() { return "Broadcast"; }
    @Override public String description() { return "Broadcasts a message to the whole server."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String text = params.getString("text", "")
                .replace("{player}", ctx.player().getName())
                .replace("{item}", ctx.itemId());
        if (text.isBlank()) return;
        ctx.plugin().getServer().broadcast(Text.chat(text));
    }
}
