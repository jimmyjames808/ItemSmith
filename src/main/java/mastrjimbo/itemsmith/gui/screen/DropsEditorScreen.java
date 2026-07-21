package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.drops.BlockDrop;
import mastrjimbo.itemsmith.drops.DropSources;
import mastrjimbo.itemsmith.drops.MobDrop;
import mastrjimbo.itemsmith.drops.SilkTouchPolicy;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Edits an item's direct drops: a list of mob-death rules and a list of block-break rules. Changes are held
 * in working lists and lowered onto the draft's {@link DropSources} when the screen closes.
 */
public final class DropsEditorScreen {

    private final GuiManager gui;
    private final ItemDraft draft;
    private final Runnable back;
    private final List<MobDrop> mobs;
    private final List<BlockDrop> blocks;

    public DropsEditorScreen(GuiManager gui, ItemDraft draft, Runnable back) {
        this.gui = gui;
        this.draft = draft;
        this.back = back;
        DropSources d = draft.drops();
        this.mobs = new ArrayList<>(d == null ? List.of() : d.mobDrops());
        this.blocks = new ArrayList<>(d == null ? List.of() : d.blockDrops());
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Drops"))
                .rows(6)
                .disableAllInteractions()
                .create();

        menu.setItem(1, 2, header(Material.ZOMBIE_HEAD, "<yellow>Mob drops"));
        int slot = 10; // row 2 start
        for (int i = 0; i < mobs.size() && slot <= 17; i++, slot++) {
            MobDrop md = mobs.get(i);
            int index = i;
            menu.setItem(slot, PaperItemBuilder.from(Material.ROTTEN_FLESH)
                    .name(Text.item("<white>" + mobWho(md)))
                    .lore(List.of(Text.item("<gray>chance " + pct(md.chance()) + " · " + countLabel(md.min(), md.max())),
                            Text.item("<gray>" + (md.requirePlayerKill() ? "player-kill only" : "any death")),
                            Text.item("<green>Left: edit  <red>Shift: remove")))
                    .asGuiItem(event -> {
                        if (event.isShiftClick()) {
                            mobs.remove(index);
                            open(player);
                        } else {
                            new MobDropScreen(gui, mobs, index, () -> open(player)).open(player);
                        }
                    }));
        }
        menu.setItem(2, 9, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add mob drop"))
                .asGuiItem(event -> {
                    mobs.add(new MobDrop(new LinkedHashSet<>(), 0.1, 1, 1, true));
                    new MobDropScreen(gui, mobs, mobs.size() - 1, () -> open(player)).open(player);
                }));

        menu.setItem(4, 2, header(Material.IRON_PICKAXE, "<yellow>Block drops"));
        slot = 37; // row 5 start
        for (int i = 0; i < blocks.size() && slot <= 44; i++, slot++) {
            BlockDrop bd = blocks.get(i);
            int index = i;
            menu.setItem(slot, PaperItemBuilder.from(Material.STONE)
                    .name(Text.item("<white>" + joinMaterials(bd.blocks())))
                    .lore(List.of(Text.item("<gray>chance " + pct(bd.chance()) + " · " + countLabel(bd.min(), bd.max())),
                            Text.item("<gray>silk-touch " + bd.silkTouch().name().toLowerCase(java.util.Locale.ROOT)),
                            Text.item("<green>Left: edit  <red>Shift: remove")))
                    .asGuiItem(event -> {
                        if (event.isShiftClick()) {
                            blocks.remove(index);
                            open(player);
                        } else {
                            new BlockDropScreen(gui, blocks, index, () -> open(player)).open(player);
                        }
                    }));
        }
        menu.setItem(5, 9, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add block drop"))
                .asGuiItem(event -> {
                    blocks.add(new BlockDrop(new LinkedHashSet<>(), 0.25, 1, 1, SilkTouchPolicy.ANY));
                    new BlockDropScreen(gui, blocks, blocks.size() - 1, () -> open(player)).open(player);
                }));

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .lore(List.of(Text.item("<gray>saves these drops to the draft")))
                .asGuiItem(event -> {
                    apply();
                    back.run();
                }));

        menu.open(player);
    }

    private void apply() {
        draft.setDrops(mobs.isEmpty() && blocks.isEmpty()
                ? DropSources.NONE
                : new DropSources(new ArrayList<>(mobs), new ArrayList<>(blocks)));
    }

    private dev.triumphteam.gui.guis.GuiItem header(Material icon, String name) {
        return PaperItemBuilder.from(icon).name(Text.item(name)).asGuiItem(event -> {
        });
    }

    private String mobWho(MobDrop md) {
        return md.entities().isEmpty() ? "any mob" : joinEntities(md.entities());
    }

    private String joinEntities(Iterable<EntityType> types) {
        List<String> names = new ArrayList<>();
        for (EntityType t : types) names.add(t.getKey().getKey());
        return String.join(", ", names);
    }

    private String joinMaterials(Iterable<Material> mats) {
        List<String> names = new ArrayList<>();
        for (Material m : mats) names.add(m.getKey().getKey());
        return names.isEmpty() ? "(no blocks)" : String.join(", ", names);
    }

    private String countLabel(int min, int max) {
        return min == max ? String.valueOf(min) : min + "-" + max;
    }

    private String pct(double chance) {
        double p = chance * 100.0;
        return (p == Math.floor(p) ? String.valueOf((long) p) : String.valueOf(p)) + "%";
    }
}
