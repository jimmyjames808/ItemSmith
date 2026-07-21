package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.BranchDraft;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

/**
 * Edits a {@code random} node's weighted branches. Exactly one branch is chosen each time, with odds
 * proportional to its weight. Add via the button; per branch: <b>left-click</b> edits its actions
 * (an {@link ActionTreeScreen}), <b>right-click</b> edits its weight, <b>shift-left</b> removes it.
 */
public final class BranchListScreen {

    private final GuiManager gui;
    private final EditSession session;
    private final String breadcrumb;
    private final List<BranchDraft> branches;
    private final Runnable back;

    public BranchListScreen(GuiManager gui, EditSession session, String breadcrumb,
                            List<BranchDraft> branches, Runnable back) {
        this.gui = gui;
        this.session = session;
        this.breadcrumb = breadcrumb;
        this.branches = branches;
        this.back = back;
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<light_purple>" + breadcrumb))
                .rows(6)
                .disableAllInteractions()
                .create();

        int slot = 0;
        for (int i = 0; i < branches.size() && slot < 45; i++) {
            menu.setItem(slot++, branchItem(player, i));
        }
        if (branches.isEmpty()) {
            menu.setItem(22, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<gray>No branches yet"))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));
        menu.setItem(6, 5, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add branch"))
                .asGuiItem(event -> {
                    branches.add(new BranchDraft(1.0, new ArrayList<>()));
                    open(player);
                }));

        menu.open(player);
    }

    private GuiItem branchItem(Player player, int index) {
        BranchDraft branch = branches.get(index);
        return PaperItemBuilder.from(Material.PAPER)
                .name(Text.item("<yellow>Branch " + (index + 1) + " <dark_gray>(weight " + branch.weight() + ")"))
                .lore(List.of(
                        Text.item("<gray>" + branch.body().size() + " action(s)"),
                        Component.empty(),
                        Text.item("<green>Left-click: edit actions"),
                        Text.item("<yellow>Right-click: edit weight"),
                        Text.item("<red>Shift-left: remove")))
                .asGuiItem(event -> {
                    ClickType click = event.getClick();
                    if (click == ClickType.SHIFT_LEFT) {
                        branches.remove(index);
                        open(player);
                    } else if (click == ClickType.RIGHT) {
                        gui.forms().number(player,
                                new Forms.NumberPrompt(Text.chat("<white>Branch weight"),
                                        Text.chat("<gray>relative odds"), branch.weight(), null, null, false),
                                () -> open(player),
                                branch::setWeight);
                    } else if (click == ClickType.LEFT) {
                        new ActionTreeScreen(gui, session, breadcrumb + " › branch " + (index + 1),
                                branch.body(), () -> open(player)).open(player);
                    }
                });
    }
}
