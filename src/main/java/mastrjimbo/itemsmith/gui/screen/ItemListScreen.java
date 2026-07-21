package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.util.Text;
import mastrjimbo.itemsmith.gui.Icons;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The creator's home screen: a paginated grid of every loaded item (plain material icons — never
 * tagged ItemSmith stacks, so the {@code InventoryListener} can't fire their abilities on a click).
 * Clicking an item opens its editor.
 */
public final class ItemListScreen {

    private final GuiManager gui;

    public ItemListScreen(GuiManager gui) {
        this.gui = gui;
    }

    public void open(Player player) {
        PaginatedGui menu = Gui.paginated()
                .title(Text.chat("<gradient:#4fc3f7:#b388ff>ItemSmith</gradient> <dark_gray>·</dark_gray> <gray>items"))
                .rows(6)
                .pageSize(45)
                .disableAllInteractions()
                .create();

        for (String id : gui.registry().ids()) {
            CustomItem def = gui.registry().get(id);
            menu.addItem(PaperItemBuilder.from(Icons.display(def))
                    .name(Text.item("<yellow>" + id))
                    .lore(describe(def))
                    .asGuiItem(event -> gui.edit(player, id)));
        }

        menu.setItem(6, 1, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ New item"))
                .lore(List.of(Text.item("<gray>start from a template")))
                .asGuiItem(event -> gui.openNew(player)));
        menu.setItem(6, 3, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Previous"))
                .asGuiItem(event -> menu.previous()));
        menu.setItem(6, 5, PaperItemBuilder.from(Material.BARRIER)
                .name(Text.item("<red>Close"))
                .asGuiItem(event -> event.getWhoClicked().closeInventory()));
        menu.setItem(6, 7, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>Next ▶"))
                .asGuiItem(event -> menu.next()));

        menu.open(player);
    }

    private List<Component> describe(CustomItem def) {
        List<Component> lore = new ArrayList<>();
        if (def == null) return lore;
        lore.add(Text.item("<dark_gray>" + def.material().getKey().getKey()));
        int n = def.abilities().size();
        lore.add(Text.item("<gray>" + n + " abilit" + (n == 1 ? "y" : "ies")));
        if (def.charges() != null) {
            lore.add(Text.item("<aqua>charges: " + def.charges() + "/" + def.maxCharges()));
        }
        lore.add(Text.item("<green>Click to edit"));
        return lore;
    }
}
