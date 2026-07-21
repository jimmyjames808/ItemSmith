package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.template.Templates;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

/**
 * The entry point for a new item: pick a starting {@link Templates.Template}, then enter a file-name
 * id. The id must be {@code [a-z0-9_]+} and not already taken; on success the seeded draft opens in the
 * item editor. (Uses the capture-in-submit / navigate-in-reopen pattern so a valid id opens the editor
 * while an invalid one or a cancel returns here.)
 */
public final class TemplatePickerScreen {

    private final GuiManager gui;

    public TemplatePickerScreen(GuiManager gui) {
        this.gui = gui;
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<gradient:#4fc3f7:#b388ff>New item</gradient> <dark_gray>· pick a template"))
                .rows(3)
                .disableAllInteractions()
                .create();

        int slot = 10;
        for (Templates.Template template : Templates.ALL) {
            menu.setItem(slot++, PaperItemBuilder.from(template.icon())
                    .name(Text.item("<aqua>" + template.label()))
                    .lore(List.of(Text.item("<gray>" + template.description()),
                            Text.item("<green>Click to start")))
                    .asGuiItem(event -> promptId(player, template.key())));
        }

        menu.setItem(3, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back to list"))
                .asGuiItem(event -> gui.openList(player)));

        menu.open(player);
    }

    private void promptId(Player player, String templateKey) {
        String[] validId = {null};
        gui.forms().text(player,
                Forms.TextPrompt.of(Text.chat("<white>New item id"),
                        Text.chat("<gray>a-z, 0-9, _ — this is the file name"), ""),
                () -> {
                    if (validId[0] != null) {
                        gui.startDraft(player, Templates.create(gui.registries(), templateKey, validId[0]));
                    } else {
                        open(player);
                    }
                },
                input -> {
                    String id = input.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
                    if (!id.matches("[a-z0-9_]+")) {
                        player.sendMessage(Text.chat("<red>Invalid id '" + input + "' — use only a-z, 0-9, _."));
                        return;
                    }
                    if (gui.registry().get(id) != null) {
                        player.sendMessage(Text.chat("<red>An item named '" + id + "' already exists."));
                        return;
                    }
                    validId[0] = id;
                });
    }
}
