package mastrjimbo.itemsmith.gui.form;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Consumer;

/**
 * The value-entry abstraction the screens depend on — deliberately free of any
 * {@code io.papermc.paper.dialog.*} type so the experimental native Dialog API stays isolated behind
 * its single implementation ({@link DialogBridge}). Each prompt shows a native dialog with one control
 * plus Confirm/Cancel; {@code onSubmit} fires with the entered value on Confirm, and {@code reopen}
 * (re-opening the parent chest screen) runs after either button since showing a dialog closes the chest.
 */
public interface Forms {

    /** A free-text field. {@code multiline} allows newlines (for long MiniMessage). */
    record TextPrompt(Component title, Component label, String initial, int maxLength, boolean multiline) {
        public static TextPrompt of(Component title, Component label, String initial) {
            return new TextPrompt(title, label, initial == null ? "" : initial, 512, false);
        }
    }

    /** A numeric field: a slider when both bounds are set, otherwise a parsed text field. */
    record NumberPrompt(Component title, Component label, double initial, Double min, Double max, boolean integer) {
    }

    /** A checkbox. */
    record BoolPrompt(Component title, Component label, boolean initial) {
    }

    /** A cycle-through single selector over a fixed option list. */
    record OptionPrompt(Component title, Component label, List<String> options, String initial) {
    }

    void text(Player player, TextPrompt prompt, Runnable reopen, Consumer<String> onSubmit);

    void number(Player player, NumberPrompt prompt, Runnable reopen, Consumer<Double> onSubmit);

    void bool(Player player, BoolPrompt prompt, Runnable reopen, Consumer<Boolean> onSubmit);

    void option(Player player, OptionPrompt prompt, Runnable reopen, Consumer<String> onSubmit);
}
