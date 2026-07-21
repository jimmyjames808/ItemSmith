package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ConditionEntry;
import mastrjimbo.itemsmith.gui.draft.ParamBag;
import mastrjimbo.itemsmith.gui.pick.ComponentPickerScreen;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Edits a list of {@link ConditionEntry} (an ability's gate, or later an {@code if} node's gate). Every
 * condition must pass for the ability to fire. Add via a searchable {@link ComponentPickerScreen}; per
 * condition: left-click edits its params, right-click toggles the universal {@code invert} flag,
 * shift-click removes it. Reusable via the {@code conditions}/{@code back} constructor args.
 */
public final class ConditionListScreen {

    private final GuiManager gui;
    private final EditSession session;
    private final Component title;
    private final List<ConditionEntry> conditions;
    private final Runnable back;

    public ConditionListScreen(GuiManager gui, EditSession session, Component title,
                               List<ConditionEntry> conditions, Runnable back) {
        this.gui = gui;
        this.session = session;
        this.title = title;
        this.conditions = conditions;
        this.back = back;
    }

    public void open(Player player) {
        Gui menu = Gui.gui().title(title).rows(6).disableAllInteractions().create();

        int slot = 0;
        for (int i = 0; i < conditions.size() && slot < 45; i++) {
            menu.setItem(slot++, conditionItem(player, conditions.get(i)));
        }
        if (conditions.isEmpty()) {
            menu.setItem(22, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<gray>No conditions"))
                    .lore(List.of(Text.item("<dark_gray>the ability always fires")))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));
        menu.setItem(6, 5, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add condition"))
                .lore(List.of(Text.item("<gray>all conditions must pass")))
                .asGuiItem(event -> new ComponentPickerScreen<>(gui,
                        Text.chat("<white>Choose <yellow>condition"),
                        gui.registries().conditions(), null,
                        cond -> conditions.add(new ConditionEntry(cond, new ParamBag(), false)),
                        () -> open(player)).open(player)));

        menu.open(player);
    }

    private GuiItem conditionItem(Player player, ConditionEntry entry) {
        boolean inverted = entry.inverted();
        boolean hasParams = !entry.def().schema().defs().isEmpty();
        return PaperItemBuilder.from(Material.COMPARATOR)
                .name(Text.item((inverted ? "<red>NOT </red>" : "") + "<yellow>" + entry.def().displayName()))
                .lore(List.of(
                        Text.item("<dark_gray>" + entry.def().id()),
                        Text.item(inverted ? "<red>inverted (must be false)" : "<gray>must be true"),
                        Component.empty(),
                        hasParams ? Text.item("<green>Left-click: edit params") : Text.item("<dark_gray>no params"),
                        Text.item("<yellow>Right-click: toggle invert"),
                        Text.item("<red>Shift-click: remove")))
                .asGuiItem(event -> {
                    if (event.isShiftClick()) {
                        conditions.remove(entry);
                        open(player);
                        return;
                    }
                    if (event.isRightClick()) {
                        entry.setInverted(!entry.inverted());
                        open(player);
                        return;
                    }
                    if (hasParams) {
                        new ParamEditorScreen(gui, session, Text.chat("<white>" + entry.def().displayName()),
                                entry.def().schema(), entry.params(), () -> open(player)).open(player);
                    }
                });
    }
}
