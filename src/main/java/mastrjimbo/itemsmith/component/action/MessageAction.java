package mastrjimbo.itemsmith.component.action;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.entity.Player;

/**
 * Sends a MiniMessage-formatted chat message. If the target is a player it goes
 * to them; otherwise it goes to the caster. Useful on its own and as the
 * simplest way to prove the self/target targeter paths in testing.
 */
public final class MessageAction implements Action {

    public static final String ID = "message";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("text", ParamType.MINIMESSAGE, "")
                    .label("Message").desc("MiniMessage text to send."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.PLAYER;
    }

    @Override
    public String displayName() {
        return "Send Message";
    }

    @Override
    public String description() {
        return "Sends a chat message to the player.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String text = params.getString("text", "");
        if (text.isBlank()) return;
        Player recipient = target instanceof Player p ? p : ctx.player();
        recipient.sendMessage(Text.chat(text));
    }
}
