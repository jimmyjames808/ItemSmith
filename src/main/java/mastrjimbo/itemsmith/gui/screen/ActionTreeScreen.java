package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ActionNodeDraft;
import mastrjimbo.itemsmith.gui.pick.ComponentPickerScreen;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Collections;
import java.util.List;

/**
 * Edits one level of the action tree — a live {@code List<ActionNodeDraft>} (the ability's root actions,
 * or a flow node's body, or a branch body). Actions run top-to-bottom. Add via a searchable picker;
 * per action: <b>left-click</b> opens it (leaf → param editor, flow → {@link FlowNodeScreen} to edit its
 * nested bodies), <b>right-click</b> moves it down, <b>shift-right</b> moves it up, <b>shift-left</b>
 * removes it. Drilling in reuses this same screen, so nesting is uniform at any depth.
 */
public final class ActionTreeScreen {

    private final GuiManager gui;
    private final EditSession session;
    private final String breadcrumb;
    private final List<ActionNodeDraft> actions;
    private final Runnable back;

    public ActionTreeScreen(GuiManager gui, EditSession session, String breadcrumb,
                            List<ActionNodeDraft> actions, Runnable back) {
        this.gui = gui;
        this.session = session;
        this.breadcrumb = breadcrumb;
        this.actions = actions;
        this.back = back;
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>" + breadcrumb))
                .rows(6)
                .disableAllInteractions()
                .create();

        int slot = 0;
        for (int i = 0; i < actions.size() && slot < 45; i++) {
            menu.setItem(slot++, actionItem(player, i));
        }
        if (actions.isEmpty()) {
            menu.setItem(22, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<gray>No actions here yet"))
                    .lore(List.of(Text.item("<dark_gray>use + Add action")))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));
        menu.setItem(6, 5, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add action"))
                .asGuiItem(event -> new ComponentPickerScreen<>(gui,
                        Text.chat("<white>Choose <yellow>action"),
                        gui.registries().actions(), null,
                        action -> actions.add(ActionNodeDraft.create(action)),
                        () -> open(player)).open(player)));

        menu.open(player);
    }

    private GuiItem actionItem(Player player, int index) {
        ActionNodeDraft node = actions.get(index);
        boolean flow = node.isFlow();
        return PaperItemBuilder.from(iconFor(node))
                .name(Text.item("<gray>" + (index + 1) + ". " + (flow ? "<light_purple>" : "<aqua>")
                        + node.def().displayName()))
                .lore(List.of(
                        Text.item("<dark_gray>" + node.def().id()),
                        flow ? Text.item("<gray>" + flowSummary(node))
                                : Text.item("<gray>" + node.def().schema().defs().size() + " param(s)"),
                        Component.empty(),
                        flow ? Text.item("<green>Left-click: open") : Text.item("<green>Left-click: edit params"),
                        Text.item("<yellow>Right / Shift-right: move down / up"),
                        Text.item("<red>Shift-left: remove")))
                .asGuiItem(event -> {
                    ClickType click = event.getClick();
                    if (click == ClickType.SHIFT_LEFT) {
                        actions.remove(index);
                        open(player);
                    } else if (click == ClickType.RIGHT && index < actions.size() - 1) {
                        Collections.swap(actions, index, index + 1);
                        open(player);
                    } else if (click == ClickType.SHIFT_RIGHT && index > 0) {
                        Collections.swap(actions, index, index - 1);
                        open(player);
                    } else if (click == ClickType.LEFT) {
                        openNode(player, node);
                    }
                });
    }

    private void openNode(Player player, ActionNodeDraft node) {
        if (node.isFlow()) {
            new FlowNodeScreen(gui, session, breadcrumb + " › " + node.def().id(), node,
                    () -> open(player)).open(player);
        } else {
            new ParamEditorScreen(gui, session, Text.chat("<white>" + node.def().displayName()),
                    node.def().schema(), node.params(), () -> open(player)).open(player);
        }
    }

    private String flowSummary(ActionNodeDraft node) {
        return switch (node.def().id()) {
            case "if" -> "then " + bodySize(node, "then") + " · else " + bodySize(node, "else");
            case "repeat", "chance" -> bodySize(node, "do") + " action(s)";
            case "random" -> node.branches().size() + " branch(es)";
            case "delay" -> "pause";
            default -> "flow";
        };
    }

    private int bodySize(ActionNodeDraft node, String key) {
        List<ActionNodeDraft> body = node.bodies().get(key);
        return body == null ? 0 : body.size();
    }

    private Material iconFor(ActionNodeDraft node) {
        if (!node.isFlow()) return Material.PAPER;
        return switch (node.def().id()) {
            case "delay" -> Material.CLOCK;
            case "repeat" -> Material.REPEATER;
            case "if" -> Material.COMPARATOR;
            case "random" -> Material.DROPPER;
            case "chance" -> Material.SUNFLOWER;
            default -> Material.REDSTONE_TORCH;
        };
    }
}
