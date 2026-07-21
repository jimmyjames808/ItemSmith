package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.util.Text;
import mastrjimbo.itemsmith.gui.Icons;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The player-facing catalog: a paginated, searchable grid of every custom item. Read-only — clicking an
 * item opens its {@link ItemDetailScreen} (how to obtain / add to recipe book), never gives the item.
 * Icons are plain untagged materials, so the {@code InventoryListener} can't fire item abilities on a click.
 */
public final class CatalogScreen {

    private final GuiManager gui;
    private String filter = "";

    public CatalogScreen(GuiManager gui) {
        this.gui = gui;
    }

    public void open(Player player) {
        PaginatedGui menu = Gui.paginated()
                .title(Text.chat("<gradient:#4fc3f7:#b388ff>ItemSmith</gradient> <dark_gray>·</dark_gray> <gray>catalog"))
                .rows(6)
                .pageSize(45)
                .disableAllInteractions()
                .create();

        String needle = filter.toLowerCase(Locale.ROOT);
        int shown = 0;
        for (String id : gui.registry().ids()) {
            CustomItem def = gui.registry().get(id);
            if (def == null) continue;
            String display = displayName(def, id);
            if (!needle.isEmpty()
                    && !id.toLowerCase(Locale.ROOT).contains(needle)
                    && !display.toLowerCase(Locale.ROOT).contains(needle)) {
                continue;
            }
            shown++;
            menu.addItem(PaperItemBuilder.from(Icons.display(def))
                    .name(nameComponent(def, id))
                    .lore(listLore(def))
                    .asGuiItem(event -> new ItemDetailScreen(gui, id, () -> open(player)).open(player)));
        }

        if (shown == 0) {
            menu.addItem(PaperItemBuilder.from(Material.BARRIER)
                    .name(Text.item("<red>No matching items"))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(6, 3, PaperItemBuilder.from(Material.OAK_SIGN)
                .name(Text.item("<yellow>Search"))
                .lore(List.of(Text.item(filter.isEmpty()
                        ? "<gray>click to filter by name or id"
                        : "<gray>filter: <white>" + filter)))
                .asGuiItem(event -> gui.forms().text(player,
                        Forms.TextPrompt.of(Text.chat("<white>Search catalog"),
                                Text.chat("<gray>type a name or id"), filter),
                        () -> open(player),
                        v -> this.filter = v == null ? "" : v.trim())));

        if (!filter.isEmpty()) {
            menu.setItem(6, 4, PaperItemBuilder.from(Material.WATER_BUCKET)
                    .name(Text.item("<red>Clear filter"))
                    .asGuiItem(event -> {
                        filter = "";
                        open(player);
                    }));
        }

        menu.setItem(6, 2, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Previous"))
                .asGuiItem(event -> menu.previous()));
        menu.setItem(6, 8, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>Next ▶"))
                .asGuiItem(event -> menu.next()));
        menu.setItem(6, 5, PaperItemBuilder.from(Material.BARRIER)
                .name(Text.item("<red>Close"))
                .asGuiItem(event -> event.getWhoClicked().closeInventory()));

        menu.open(player);
    }

    private List<Component> listLore(CustomItem def) {
        List<Component> lore = new ArrayList<>();
        lore.add(ObtainInfo.tags(def));
        lore.add(Text.item("<green>Click to view"));
        return lore;
    }

    private Component nameComponent(CustomItem def, String id) {
        String name = def.name();
        return (name == null || name.isEmpty()) ? Text.item("<yellow>" + id) : Text.item(name);
    }

    private String displayName(CustomItem def, String id) {
        String name = def.name();
        return (name == null || name.isEmpty()) ? id : name;
    }
}
