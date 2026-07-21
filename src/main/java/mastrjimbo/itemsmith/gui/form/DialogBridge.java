package mastrjimbo.itemsmith.gui.form;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * The one and only place that touches the {@code @Experimental} Paper Dialog API. Builds a native
 * dialog with a single input plus Confirm / Cancel buttons, reads the submitted value in the
 * server-side callback, and re-opens the parent chest screen a tick later (showing a dialog closes the
 * chest, so we must restore it on both buttons). Keeping all Paper dialog types here means a future API
 * bump only touches this file.
 */
public final class DialogBridge implements Forms {

    private static final String KEY = "value";

    private final Plugin plugin;

    public DialogBridge(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void text(Player player, TextPrompt prompt, Runnable reopen, Consumer<String> onSubmit) {
        DialogInput input = DialogInput.text(KEY, prompt.label())
                .initial(prompt.initial() == null ? "" : prompt.initial())
                .maxLength(Math.max(1, prompt.maxLength()))
                .width(300)
                .build();
        show(player, input, prompt.title(), reopen, view -> {
            String v = view.getText(KEY);
            onSubmit.accept(v == null ? "" : v);
        });
    }

    @Override
    public void number(Player player, NumberPrompt prompt, Runnable reopen, Consumer<Double> onSubmit) {
        // A plain text field, not a slider: users type clean numbers ("1.5") instead of dragging to a
        // messy fraction. Bounds on the prompt are advisory only (shown, not enforced by a widget).
        DialogInput input = DialogInput.text(KEY, prompt.label())
                .initial(formatNumber(prompt.initial(), prompt.integer()))
                .maxLength(32)
                .width(200)
                .build();
        show(player, input, prompt.title(), reopen, view -> {
            String s = view.getText(KEY);
            if (s == null) return;
            try {
                double d = Double.parseDouble(s.trim());
                onSubmit.accept(prompt.integer() ? (double) Math.round(d) : d);
            } catch (NumberFormatException ignored) {
                // invalid input — keep the previous value
            }
        });
    }

    @Override
    public void bool(Player player, BoolPrompt prompt, Runnable reopen, Consumer<Boolean> onSubmit) {
        DialogInput input = DialogInput.bool(KEY, prompt.label()).initial(prompt.initial()).build();
        show(player, input, prompt.title(), reopen, view -> {
            Boolean b = view.getBoolean(KEY);
            if (b != null) onSubmit.accept(b);
        });
    }

    @Override
    public void option(Player player, OptionPrompt prompt, Runnable reopen, Consumer<String> onSubmit) {
        boolean anyMatch = prompt.options().stream().anyMatch(o -> o.equalsIgnoreCase(prompt.initial()));
        List<SingleOptionDialogInput.OptionEntry> entries = new ArrayList<>();
        for (int i = 0; i < prompt.options().size(); i++) {
            String opt = prompt.options().get(i);
            boolean initial = anyMatch ? opt.equalsIgnoreCase(prompt.initial()) : i == 0;
            entries.add(SingleOptionDialogInput.OptionEntry.create(opt, Text.item("<white>" + opt), initial));
        }
        DialogInput input = DialogInput.singleOption(KEY, prompt.label(), entries).build();
        show(player, input, prompt.title(), reopen, view -> {
            String v = view.getText(KEY);
            if (v != null) onSubmit.accept(v);
        });
    }

    /** Builds and shows the dialog: one input + Confirm (reads + reopens) / Cancel (reopens). */
    private void show(Player player, DialogInput input, Component title, Runnable reopen,
                      Consumer<DialogResponseView> read) {
        ActionButton confirm = ActionButton.builder(Text.item("<green>Confirm"))
                .action(DialogAction.customClick((view, audience) -> {
                    read.accept(view);
                    reopenLater(reopen);
                }, oneUse()))
                .build();
        ActionButton cancel = ActionButton.builder(Text.item("<red>Cancel"))
                .action(DialogAction.customClick((view, audience) -> reopenLater(reopen), oneUse()))
                .build();

        Dialog dialog = Dialog.create(factory -> factory.empty()
                .base(DialogBase.builder(title)
                        .inputs(List.of(input))
                        .canCloseWithEscape(true)
                        .build())
                .type(DialogType.multiAction(List.of(confirm, cancel)).build()));

        // Client guard (no dependency): dialogs need a 1.21.6+ client. If showing fails, tell the
        // player and restore the chest they came from instead of leaving them stuck.
        try {
            player.showDialog(dialog);
        } catch (Throwable t) {
            player.sendMessage(Text.chat("<red>The ItemSmith creator needs a 1.21.6+ client for this input."));
            reopenLater(reopen);
        }
    }

    private void reopenLater(Runnable reopen) {
        if (reopen != null) {
            plugin.getServer().getScheduler().runTask(plugin, reopen);
        }
    }

    private static ClickCallback.Options oneUse() {
        return ClickCallback.Options.builder().uses(1).build();
    }

    private static String formatNumber(double value, boolean integer) {
        return integer ? Long.toString(Math.round(value)) : Double.toString(value);
    }
}
