package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.loot.LootInjection;
import mastrjimbo.itemsmith.loot.LootRule;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Edits an item's loot-table injection rules. Each rule targets a set of table-key patterns
 * ({@code chests/…}, {@code gameplay/fishing}, {@code entities/…}) with a chance and count. Changes are
 * held in a working list and lowered back onto the draft when the screen closes.
 */
public final class LootEditorScreen {

    private final GuiManager gui;
    private final ItemDraft draft;
    private final Runnable back;
    private final List<LootRule> rules;

    public LootEditorScreen(GuiManager gui, ItemDraft draft, Runnable back) {
        this.gui = gui;
        this.draft = draft;
        this.back = back;
        this.rules = new ArrayList<>(draft.loot() == null ? List.of() : draft.loot().rules());
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Loot injection"))
                .rows(6)
                .disableAllInteractions()
                .create();

        int slot = 0;
        for (int i = 0; i < rules.size() && slot < 45; i++, slot++) {
            LootRule rule = rules.get(i);
            int index = i;
            menu.setItem(slot + 1, PaperItemBuilder.from(Material.CHEST)
                    .name(Text.item("<yellow>" + String.join(", ", rule.tables())))
                    .lore(List.of(
                            Text.item("<gray>chance " + pct(rule.chance()) + " · count " + countLabel(rule)),
                            Text.item("<green>Left-click: edit"),
                            Text.item("<red>Shift-click: remove")))
                    .asGuiItem(event -> {
                        if (event.isShiftClick()) {
                            rules.remove(index);
                            open(player);
                        } else {
                            new LootRuleScreen(gui, rules, index, () -> open(player)).open(player);
                        }
                    }));
        }

        menu.setItem(6, 3, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add loot rule"))
                .lore(List.of(Text.item("<gray>inject this item into a loot table")))
                .asGuiItem(event -> {
                    rules.add(new LootRule(new ArrayList<>(List.of("chests/")), 0.1, 1, 1));
                    new LootRuleScreen(gui, rules, rules.size() - 1, () -> open(player)).open(player);
                }));

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .lore(List.of(Text.item("<gray>saves these rules to the draft")))
                .asGuiItem(event -> {
                    apply();
                    back.run();
                }));

        menu.open(player);
    }

    private void apply() {
        draft.setLoot(rules.isEmpty() ? LootInjection.NONE : new LootInjection(new ArrayList<>(rules)));
    }

    private String countLabel(LootRule rule) {
        return rule.min() == rule.max() ? String.valueOf(rule.min()) : rule.min() + "-" + rule.max();
    }

    private String pct(double chance) {
        double p = chance * 100.0;
        return (p == Math.floor(p) ? String.valueOf((long) p) : String.valueOf(p)) + "%";
    }
}
