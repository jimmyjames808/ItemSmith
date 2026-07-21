package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.drops.BlockDrop;
import mastrjimbo.itemsmith.drops.SilkTouchPolicy;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.pick.ValueProviders;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Edits one {@link BlockDrop} in place: which blocks, chance, count, and the Silk Touch policy. */
public final class BlockDropScreen {

    private static final List<String> SILK_OPTIONS = List.of("any", "require", "forbid");

    private final GuiManager gui;
    private final List<BlockDrop> list;
    private final int index;
    private final Runnable back;

    public BlockDropScreen(GuiManager gui, List<BlockDrop> list, int index, Runnable back) {
        this.gui = gui;
        this.list = list;
        this.index = index;
        this.back = back;
    }

    public void open(Player player) {
        BlockDrop bd = list.get(index);
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Block drop"))
                .rows(3)
                .disableAllInteractions()
                .create();

        menu.setItem(2, 2, PaperItemBuilder.from(Material.STONE)
                .name(Text.item("<aqua>Blocks"))
                .lore(List.of(Text.item("<gray>" + (bd.blocks().isEmpty() ? "(none)" : join(bd.blocks()))),
                        Text.item("<green>Click to pick blocks")))
                .asGuiItem(event -> {
                    BlockDrop cur = list.get(index);
                    List<String> working = new ArrayList<>();
                    for (Material m : cur.blocks()) working.add(m.getKey().getKey());
                    new CollectionPickerScreen(gui, Text.chat("<white>Blocks that drop this"),
                            ValueProviders.blocks(),
                            working,
                            () -> list.set(index, new BlockDrop(toMaterials(working),
                                    cur.chance(), cur.min(), cur.max(), cur.silkTouch())),
                            () -> open(player),
                            false).open(player);
                }));

        menu.setItem(2, 3, PaperItemBuilder.from(Material.GOLD_NUGGET)
                .name(Text.item("<aqua>Chance"))
                .lore(List.of(Text.item("<gray>" + bd.chance() + " (0-1)"), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Chance (0-1)"),
                                Text.chat("<gray>0 = never, 1 = always"), bd.chance(), 0.0, 1.0, false),
                        () -> open(player),
                        v -> list.set(index, new BlockDrop(bd.blocks(), clamp01(v), bd.min(), bd.max(),
                                bd.silkTouch())))));

        menu.setItem(2, 4, PaperItemBuilder.from(Material.PAPER)
                .name(Text.item("<aqua>Min count"))
                .lore(List.of(Text.item("<gray>" + bd.min()), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Minimum count"), Text.chat("<gray>at least"),
                                bd.min(), 1.0, 64.0, true),
                        () -> open(player),
                        v -> {
                            int min = Math.max(1, (int) Math.round(v));
                            list.set(index, new BlockDrop(bd.blocks(), bd.chance(), min, Math.max(min, bd.max()),
                                    bd.silkTouch()));
                        })));

        menu.setItem(2, 5, PaperItemBuilder.from(Material.PAPER)
                .name(Text.item("<aqua>Max count"))
                .lore(List.of(Text.item("<gray>" + bd.max()), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Maximum count"), Text.chat("<gray>at most"),
                                bd.max(), 1.0, 64.0, true),
                        () -> open(player),
                        v -> {
                            int max = Math.max(1, (int) Math.round(v));
                            list.set(index, new BlockDrop(bd.blocks(), bd.chance(), Math.min(bd.min(), max), max,
                                    bd.silkTouch()));
                        })));

        menu.setItem(2, 6, PaperItemBuilder.from(Material.DIAMOND_PICKAXE)
                .name(Text.item("<aqua>Silk Touch"))
                .lore(List.of(Text.item("<gray>" + bd.silkTouch().name().toLowerCase(Locale.ROOT)),
                        Text.item("<dark_gray>any / require / forbid"),
                        Text.item("<green>Click to change")))
                .asGuiItem(event -> gui.forms().option(player,
                        new Forms.OptionPrompt(Text.chat("<white>Silk Touch policy"),
                                Text.chat("<gray>require = only with silk, forbid = only without"),
                                SILK_OPTIONS, bd.silkTouch().name().toLowerCase(Locale.ROOT)),
                        () -> open(player),
                        v -> list.set(index, new BlockDrop(bd.blocks(), bd.chance(), bd.min(), bd.max(),
                                SilkTouchPolicy.from(v))))));

        menu.setItem(2, 8, PaperItemBuilder.from(Material.BARRIER)
                .name(Text.item("<red>Delete drop"))
                .asGuiItem(event -> {
                    list.remove(index);
                    back.run();
                }));
        menu.setItem(3, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }

    private Set<Material> toMaterials(List<String> keys) {
        Set<Material> out = new LinkedHashSet<>();
        for (String key : keys) {
            Material m = Material.matchMaterial(key);
            if (m != null) out.add(m);
        }
        return out;
    }

    private String join(Set<Material> mats) {
        List<String> names = new ArrayList<>();
        for (Material m : mats) names.add(m.getKey().getKey());
        return String.join(", ", names);
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
