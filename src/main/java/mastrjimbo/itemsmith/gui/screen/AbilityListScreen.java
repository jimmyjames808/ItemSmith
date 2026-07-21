package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gate.Gate;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.AbilityDraft;
import mastrjimbo.itemsmith.gui.draft.ConfiguredDraft;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.gui.draft.ParamBag;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists an item's abilities: click one to edit it, shift-click to remove it, or add a new one (seeded
 * on {@code right_click} + the {@code target} targeter) which opens straight into its editor.
 */
public final class AbilityListScreen {

    private final GuiManager gui;
    private final EditSession session;

    public AbilityListScreen(GuiManager gui, EditSession session) {
        this.gui = gui;
        this.session = session;
    }

    public void open(Player player) {
        ItemDraft draft = session.draft();
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Abilities</aqua> <dark_gray>·</dark_gray> <white>" + draft.id()))
                .rows(6)
                .disableAllInteractions()
                .create();

        int slot = 0;
        List<AbilityDraft> abilities = draft.abilities();
        for (int i = 0; i < abilities.size() && slot < 45; i++) {
            AbilityDraft ability = abilities.get(i);
            int number = i + 1;
            menu.setItem(slot++, PaperItemBuilder.from(Material.NETHER_STAR)
                    .name(Text.item("<yellow>Ability " + number))
                    .lore(List.of(
                            Text.item("<gray>on: <white>" + ability.activatorId()),
                            Text.item("<gray>" + ability.actions().size() + " action(s) · "
                                    + ability.conditions().size() + " condition(s)"),
                            Component.empty(),
                            Text.item("<green>Click to edit"),
                            Text.item("<red>Shift-click: remove")))
                    .asGuiItem(event -> {
                        if (event.isShiftClick()) {
                            abilities.remove(ability);
                            open(player);
                            return;
                        }
                        new AbilityEditorScreen(gui, session, ability).open(player);
                    }));
        }
        if (abilities.isEmpty()) {
            menu.setItem(22, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<gray>No abilities yet"))
                    .lore(List.of(Text.item("<dark_gray>use + Add ability")))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> new ItemEditorScreen(gui, session).open(player)));
        menu.setItem(6, 5, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add ability"))
                .asGuiItem(event -> {
                    AbilityDraft ability = new AbilityDraft("right_click", new ParamBag(), new ArrayList<>(),
                            new ConfiguredDraft<>(gui.registries().targeter("target"), new ParamBag()),
                            new ArrayList<>(), Gate.NONE, 0);
                    abilities.add(ability);
                    new AbilityEditorScreen(gui, session, ability).open(player);
                }));

        menu.open(player);
    }
}
