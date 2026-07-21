package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.List;

/**
 * Edits an item's lore, one line per row (MiniMessage). Add a line via the button; per line:
 * <b>left-click</b> edits its text, <b>right-click</b> / <b>shift-right</b> move it down / up,
 * <b>shift-left</b> removes it. Operates on the live {@code List<String>} so edits land straight on the
 * draft.
 */
public final class LoreEditorScreen {

    private final GuiManager gui;
    private final List<String> lore;
    private final Runnable back;

    public LoreEditorScreen(GuiManager gui, List<String> lore, Runnable back) {
        this.gui = gui;
        this.lore = lore;
        this.back = back;
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Lore"))
                .rows(6)
                .disableAllInteractions()
                .create();

        int slot = 0;
        for (int i = 0; i < lore.size() && slot < 45; i++) {
            menu.setItem(slot++, lineItem(player, i));
        }
        if (lore.isEmpty()) {
            menu.setItem(22, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<gray>No lore lines"))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));
        menu.setItem(6, 5, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add line"))
                .asGuiItem(event -> {
                    lore.add("");
                    open(player);
                }));

        menu.open(player);
    }

    private GuiItem lineItem(Player player, int index) {
        String line = lore.get(index);
        return PaperItemBuilder.from(Material.PAPER)
                .name(line.isEmpty() ? Text.item("<dark_gray><italic>(blank line)") : Text.item(line))
                .lore(List.of(
                        Text.item("<dark_gray>line " + (index + 1)),
                        Component.empty(),
                        Text.item("<green>Left-click: edit"),
                        Text.item("<yellow>Right / Shift-right: move down / up"),
                        Text.item("<red>Shift-left: remove")))
                .asGuiItem(event -> {
                    ClickType click = event.getClick();
                    if (click == ClickType.SHIFT_LEFT) {
                        lore.remove(index);
                        open(player);
                    } else if (click == ClickType.RIGHT && index < lore.size() - 1) {
                        Collections.swap(lore, index, index + 1);
                        open(player);
                    } else if (click == ClickType.SHIFT_RIGHT && index > 0) {
                        Collections.swap(lore, index, index - 1);
                        open(player);
                    } else if (click == ClickType.LEFT) {
                        gui.forms().text(player,
                                Forms.TextPrompt.of(Text.chat("<white>Edit lore line"),
                                        Text.chat("<gray>MiniMessage supported"), line),
                                () -> open(player),
                                v -> lore.set(index, v));
                    }
                });
    }
}
