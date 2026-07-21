package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.pick.ValueProviders;
import mastrjimbo.itemsmith.loot.LootRule;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Edits one {@link LootRule} in place within its parent list: the table-key patterns, the chance, and the
 * min/max count. Each field opens a native dialog; the rule record is rebuilt into the list on every edit.
 */
public final class LootRuleScreen {

    private final GuiManager gui;
    private final List<LootRule> rules;
    private final int index;
    private final Runnable back;

    public LootRuleScreen(GuiManager gui, List<LootRule> rules, int index, Runnable back) {
        this.gui = gui;
        this.rules = rules;
        this.index = index;
        this.back = back;
    }

    public void open(Player player) {
        LootRule rule = rules.get(index);
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Loot rule"))
                .rows(3)
                .disableAllInteractions()
                .create();

        menu.setItem(2, 2, PaperItemBuilder.from(Material.CHEST)
                .name(Text.item("<aqua>Loot tables"))
                .lore(List.of(Text.item("<gray>" + String.join(", ", rule.tables())),
                        Text.item("<dark_gray>chests / fishing / entities / …"),
                        Text.item("<green>Click to pick tables")))
                .asGuiItem(event -> {
                    LootRule cur = rules.get(index);
                    List<String> working = new ArrayList<>(cur.tables());
                    new CollectionPickerScreen(gui, Text.chat("<white>Loot tables"),
                            ValueProviders.lootTables(),
                            working,
                            () -> rules.set(index, new LootRule(new ArrayList<>(working),
                                    cur.chance(), cur.min(), cur.max())),
                            () -> open(player),
                            true).open(player);
                }));

        menu.setItem(2, 4, PaperItemBuilder.from(Material.GOLD_NUGGET)
                .name(Text.item("<aqua>Chance"))
                .lore(List.of(Text.item("<gray>" + rule.chance() + " (0-1)"),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Chance (0-1)"),
                                Text.chat("<gray>0 = never, 1 = always"), rule.chance(), 0.0, 1.0, false),
                        () -> open(player),
                        v -> rules.set(index, new LootRule(rule.tables(), clamp01(v), rule.min(), rule.max())))));

        menu.setItem(2, 5, PaperItemBuilder.from(Material.PAPER)
                .name(Text.item("<aqua>Min count"))
                .lore(List.of(Text.item("<gray>" + rule.min()), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Minimum count"),
                                Text.chat("<gray>how many at least"), rule.min(), 1.0, 64.0, true),
                        () -> open(player),
                        v -> {
                            int min = Math.max(1, (int) Math.round(v));
                            rules.set(index, new LootRule(rule.tables(), rule.chance(), min, Math.max(min, rule.max())));
                        })));

        menu.setItem(2, 6, PaperItemBuilder.from(Material.PAPER)
                .name(Text.item("<aqua>Max count"))
                .lore(List.of(Text.item("<gray>" + rule.max()), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Maximum count"),
                                Text.chat("<gray>how many at most"), rule.max(), 1.0, 64.0, true),
                        () -> open(player),
                        v -> {
                            int max = Math.max(1, (int) Math.round(v));
                            rules.set(index, new LootRule(rule.tables(), rule.chance(), Math.min(rule.min(), max), max));
                        })));

        menu.setItem(2, 8, PaperItemBuilder.from(Material.BARRIER)
                .name(Text.item("<red>Delete rule"))
                .asGuiItem(event -> {
                    rules.remove(index);
                    back.run();
                }));

        menu.setItem(3, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
