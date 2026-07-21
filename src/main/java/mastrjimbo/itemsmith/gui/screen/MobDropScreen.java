package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.drops.MobDrop;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.pick.ValueProviders;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Edits one {@link MobDrop} in place: which mobs, chance, count, and whether a player kill is required. */
public final class MobDropScreen {

    private final GuiManager gui;
    private final List<MobDrop> list;
    private final int index;
    private final Runnable back;

    public MobDropScreen(GuiManager gui, List<MobDrop> list, int index, Runnable back) {
        this.gui = gui;
        this.list = list;
        this.index = index;
        this.back = back;
    }

    public void open(Player player) {
        MobDrop md = list.get(index);
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Mob drop"))
                .rows(3)
                .disableAllInteractions()
                .create();

        menu.setItem(2, 2, PaperItemBuilder.from(Material.ZOMBIE_HEAD)
                .name(Text.item("<aqua>Mobs"))
                .lore(List.of(Text.item("<gray>" + (md.entities().isEmpty() ? "any mob" : join(md.entities()))),
                        Text.item("<dark_gray>none selected = any mob"),
                        Text.item("<green>Click to pick mobs")))
                .asGuiItem(event -> {
                    MobDrop cur = list.get(index);
                    List<String> working = new ArrayList<>();
                    for (EntityType t : cur.entities()) working.add(t.getKey().getKey());
                    new CollectionPickerScreen(gui, Text.chat("<white>Mobs that drop this"),
                            ValueProviders.options(ParamType.ENTITY_TYPE, gui.registry()),
                            working,
                            () -> list.set(index, new MobDrop(toEntities(working),
                                    cur.chance(), cur.min(), cur.max(), cur.requirePlayerKill())),
                            () -> open(player),
                            false).open(player);
                }));

        menu.setItem(2, 3, PaperItemBuilder.from(Material.GOLD_NUGGET)
                .name(Text.item("<aqua>Chance"))
                .lore(List.of(Text.item("<gray>" + md.chance() + " (0-1)"), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Chance (0-1)"),
                                Text.chat("<gray>0 = never, 1 = always"), md.chance(), 0.0, 1.0, false),
                        () -> open(player),
                        v -> list.set(index, new MobDrop(md.entities(), clamp01(v), md.min(), md.max(),
                                md.requirePlayerKill())))));

        menu.setItem(2, 4, PaperItemBuilder.from(Material.PAPER)
                .name(Text.item("<aqua>Min count"))
                .lore(List.of(Text.item("<gray>" + md.min()), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Minimum count"), Text.chat("<gray>at least"),
                                md.min(), 1.0, 64.0, true),
                        () -> open(player),
                        v -> {
                            int min = Math.max(1, (int) Math.round(v));
                            list.set(index, new MobDrop(md.entities(), md.chance(), min, Math.max(min, md.max()),
                                    md.requirePlayerKill()));
                        })));

        menu.setItem(2, 5, PaperItemBuilder.from(Material.PAPER)
                .name(Text.item("<aqua>Max count"))
                .lore(List.of(Text.item("<gray>" + md.max()), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Maximum count"), Text.chat("<gray>at most"),
                                md.max(), 1.0, 64.0, true),
                        () -> open(player),
                        v -> {
                            int max = Math.max(1, (int) Math.round(v));
                            list.set(index, new MobDrop(md.entities(), md.chance(), Math.min(md.min(), max), max,
                                    md.requirePlayerKill()));
                        })));

        menu.setItem(2, 6, PaperItemBuilder.from(md.requirePlayerKill() ? Material.DIAMOND_SWORD : Material.SKELETON_SKULL)
                .name(Text.item("<aqua>Require player kill"))
                .lore(List.of(Text.item("<gray>" + (md.requirePlayerKill() ? "yes" : "no")),
                        Text.item("<dark_gray>on = only player-credited kills drop"),
                        Text.item("<green>Click to toggle")))
                .asGuiItem(event -> gui.forms().bool(player,
                        new Forms.BoolPrompt(Text.chat("<white>Require player kill?"),
                                Text.chat("<gray>prevents mob-farm exploits"), md.requirePlayerKill()),
                        () -> open(player),
                        v -> list.set(index, new MobDrop(md.entities(), md.chance(), md.min(), md.max(), v)))));

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

    private Set<EntityType> toEntities(List<String> keys) {
        Set<EntityType> out = new LinkedHashSet<>();
        for (String key : keys) {
            try {
                out.add(EntityType.valueOf(key.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
                // picker values come straight from EntityType keys, so this shouldn't happen
            }
        }
        return out;
    }

    private String join(Set<EntityType> types) {
        List<String> names = new ArrayList<>();
        for (EntityType t : types) names.add(t.getKey().getKey());
        return String.join(", ", names);
    }

    private static double clamp01(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }
}
