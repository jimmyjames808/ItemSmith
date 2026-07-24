package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Edits an item's declared initial stats: the {@code stats:} block of name→value pairs stamped onto each
 * item's PDC when it is first built. Each existing stat shows as an entry — left-click to edit its value,
 * shift-click to remove — and an "Add stat" control captures a new name (validated as {@code [a-z0-9_]+})
 * then its value. Edits land directly on the draft's stats map via {@link ItemDraft#putStat} /
 * {@link ItemDraft#removeStat}; there is no working copy to apply.
 */
public final class StatsEditorScreen {

    private final GuiManager gui;
    private final ItemDraft draft;
    private final Runnable back;

    public StatsEditorScreen(GuiManager gui, ItemDraft draft, Runnable back) {
        this.gui = gui;
        this.draft = draft;
        this.back = back;
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Stats"))
                .rows(6)
                .disableAllInteractions()
                .create();

        menu.setItem(1, 5, PaperItemBuilder.from(Material.EXPERIENCE_BOTTLE)
                .name(Text.item("<aqua>Stats"))
                .lore(List.of(Text.item("<gray>initial values stamped onto each item"),
                        Text.item("<gray>" + draft.stats().size() + " defined")))
                .asGuiItem(event -> {
                }));

        List<String> names = new ArrayList<>(draft.stats().keySet());
        if (names.isEmpty()) {
            menu.setItem(3, 5, PaperItemBuilder.from(Material.GRAY_DYE)
                    .name(Text.item("<gray><italic>(no stats yet)"))
                    .lore(List.of(Text.item("<gray>use <green>+ Add stat<gray> below")))
                    .asGuiItem(event -> {
                    }));
        }
        int slot = 9; // row 2 start
        for (int i = 0; i < names.size() && slot <= 44; i++, slot++) {
            String name = names.get(i);
            String value = draft.stats().get(name);
            menu.setItem(slot, PaperItemBuilder.from(Material.PAPER)
                    .name(Text.item("<white>" + name))
                    .lore(List.of(Text.item("<gray>value: <yellow>" + value),
                            Text.item("<green>Left: edit  <red>Shift: remove")))
                    .asGuiItem(event -> {
                        if (event.isShiftClick()) {
                            draft.removeStat(name);
                            open(player);
                        } else {
                            editStat(player, name);
                        }
                    }));
        }

        menu.setItem(6, 5, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add stat"))
                .lore(List.of(Text.item("<gray>name (a-z, 0-9, _) then a value")))
                .asGuiItem(event -> addStat(player)));

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }

    /** Re-prompts for an existing stat's value, seeding the field with the current one. */
    private void editStat(Player player, String name) {
        gui.forms().text(player,
                Forms.TextPrompt.of(Text.chat("<white>Value for <yellow>" + name),
                        Text.chat("<gray>stat value"), draft.stats().get(name)),
                () -> open(player),
                value -> draft.putStat(name, value));
    }

    /**
     * Add flow: first prompt captures and validates the name (navigation happens in the reopen once a valid
     * name is held, mirroring {@code ItemEditorScreen}'s Save-As), then a second prompt captures the value.
     */
    private void addStat(Player player) {
        String[] validName = {null};
        gui.forms().text(player,
                Forms.TextPrompt.of(Text.chat("<white>Add stat — name"), Text.chat("<gray>a-z, 0-9, _"), ""),
                () -> {
                    if (validName[0] != null) {
                        promptValue(player, validName[0]);
                    } else {
                        open(player);
                    }
                },
                input -> {
                    String name = input.trim().toLowerCase(Locale.ROOT);
                    if (!name.matches("[a-z0-9_]+")) {
                        player.sendMessage(Text.chat("<red>Invalid stat name '" + input + "' — use a-z, 0-9, _."));
                        return;
                    }
                    if (draft.stats().containsKey(name)) {
                        player.sendMessage(Text.chat("<red>Stat '" + name + "' already exists."));
                        return;
                    }
                    validName[0] = name;
                });
    }

    /** Second step of the add flow: prompts for the value and writes the new stat onto the draft. */
    private void promptValue(Player player, String name) {
        gui.forms().text(player,
                Forms.TextPrompt.of(Text.chat("<white>Value for <yellow>" + name),
                        Text.chat("<gray>stat value"), ""),
                () -> open(player),
                value -> draft.putStat(name, value));
    }
}
