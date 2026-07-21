package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.engine.FlowAction;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ActionNodeDraft;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The hub for one flow node (delay / repeat / if / random / chance). Edits its parameters, its
 * condition gate (for {@code if}), each of its nested bodies (drilling into an {@link ActionTreeScreen}
 * per body — {@code do} for repeat/chance, {@code then}/{@code else} for if), and its weighted branches
 * (for {@code random}). Only the aspects a given flow actually uses are shown.
 */
public final class FlowNodeScreen {

    private final GuiManager gui;
    private final EditSession session;
    private final String breadcrumb;
    private final ActionNodeDraft node;
    private final Runnable back;

    public FlowNodeScreen(GuiManager gui, EditSession session, String breadcrumb,
                          ActionNodeDraft node, Runnable back) {
        this.gui = gui;
        this.session = session;
        this.breadcrumb = breadcrumb;
        this.node = node;
        this.back = back;
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<light_purple>" + breadcrumb))
                .rows(3)
                .disableAllInteractions()
                .create();

        FlowAction flow = (FlowAction) node.def();
        int slot = 10;

        if (!node.def().schema().defs().isEmpty()) {
            menu.setItem(slot++, PaperItemBuilder.from(Material.REPEATER)
                    .name(Text.item("<aqua>Parameters"))
                    .lore(List.of(Text.item("<gray>" + node.def().schema().defs().size() + " to edit"),
                            Text.item("<green>Click to edit")))
                    .asGuiItem(event -> new ParamEditorScreen(gui, session,
                            Text.chat("<white>" + node.def().displayName()),
                            node.def().schema(), node.params(), () -> open(player)).open(player)));
        }

        if (node.def().id().equals("if")) {
            menu.setItem(slot++, PaperItemBuilder.from(Material.COMPARATOR)
                    .name(Text.item("<aqua>Conditions"))
                    .lore(List.of(Text.item("<gray>" + node.conditions().size() + " defined"),
                            Text.item("<dark_gray>decides then vs else"),
                            Text.item("<green>Click to edit")))
                    .asGuiItem(event -> new ConditionListScreen(gui, session,
                            Text.chat("<aqua>if — conditions"), node.conditions(), () -> open(player)).open(player)));
        }

        for (String key : ordered(flow.bodyKeys())) {
            List<ActionNodeDraft> body = node.bodies().computeIfAbsent(key, k -> new ArrayList<>());
            menu.setItem(slot++, PaperItemBuilder.from(Material.CHEST)
                    .name(Text.item("<aqua>" + key + " <dark_gray>(" + body.size() + ")"))
                    .lore(List.of(Text.item("<gray>actions run for this branch"),
                            Text.item("<green>Click to open")))
                    .asGuiItem(event -> new ActionTreeScreen(gui, session, breadcrumb + " › " + key,
                            body, () -> open(player)).open(player)));
        }

        if (flow.usesBranches()) {
            menu.setItem(slot++, PaperItemBuilder.from(Material.DROPPER)
                    .name(Text.item("<aqua>Branches <dark_gray>(" + node.branches().size() + ")"))
                    .lore(List.of(Text.item("<gray>one is chosen at random by weight"),
                            Text.item("<green>Click to edit")))
                    .asGuiItem(event -> new BranchListScreen(gui, session, breadcrumb + " › branches",
                            node.branches(), () -> open(player)).open(player)));
        }

        if (slot == 10) {
            menu.setItem(13, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<gray>Nothing to configure"))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(3, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }

    /** Orders body keys so they read naturally: do first, then before else. */
    private List<String> ordered(java.util.Set<String> keys) {
        List<String> out = new ArrayList<>(keys);
        out.sort(Comparator.comparingInt(this::rank));
        return out;
    }

    private int rank(String key) {
        return switch (key) {
            case "do" -> 0;
            case "then" -> 1;
            case "else" -> 2;
            default -> 3;
        };
    }
}
