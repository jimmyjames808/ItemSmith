package mastrjimbo.itemsmith.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

/** MiniMessage helpers shared across the plugin. */
public final class Text {

    public static final MiniMessage MINI = MiniMessage.miniMessage();

    private Text() {
    }

    /** Deserializes MiniMessage and disables the default italic that vanilla adds to renamed items. */
    public static Component item(String miniMessage) {
        return MINI.deserialize(miniMessage).decoration(TextDecoration.ITALIC, false);
    }

    /** Deserializes MiniMessage for chat/messages (leaves decorations untouched). */
    public static Component chat(String miniMessage) {
        return MINI.deserialize(miniMessage);
    }
}
