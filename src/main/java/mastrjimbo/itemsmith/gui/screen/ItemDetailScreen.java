package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.util.Text;
import mastrjimbo.itemsmith.gui.Icons;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-only detail for one catalog item: its display name + lore, how it can be obtained, and — for items
 * with a Bukkit-native recipe — an "Add to recipe book" button that unlocks the recipe(s) in the player's
 * vanilla recipe book via {@link Player#discoverRecipes}. Never gives the item.
 */
public final class ItemDetailScreen {

    private final GuiManager gui;
    private final String id;
    private final Runnable back;

    public ItemDetailScreen(GuiManager gui, String id, Runnable back) {
        this.gui = gui;
        this.id = id;
        this.back = back;
    }

    public void open(Player player) {
        CustomItem def = gui.registry().get(id);
        if (def == null) {
            player.sendMessage(Text.chat("<red>Unknown item '" + id + "'."));
            back.run();
            return;
        }

        Gui menu = Gui.gui()
                .title(Text.chat("<gray>Catalog <dark_gray>· <white>" + id))
                .rows(3)
                .disableAllInteractions()
                .create();

        menu.setItem(2, 3, PaperItemBuilder.from(Icons.display(def))
                .name(nameComponent(def))
                .lore(itemLore(def))
                .asGuiItem(event -> {
                }));

        // How to obtain.
        List<Component> obtain = new ArrayList<>();
        obtain.add(Text.item("<gray>Ways to get this item:"));
        obtain.addAll(ObtainInfo.lines(def));
        menu.setItem(2, 5, PaperItemBuilder.from(Material.KNOWLEDGE_BOOK)
                .name(Text.item("<aqua>How to obtain"))
                .lore(obtain)
                .asGuiItem(event -> {
                }));

        // Recipe-book unlock, only when the item actually has Bukkit-native recipes registered.
        List<NamespacedKey> recipeKeys = gui.plugin().recipes().registeredKeysFor(id);
        if (!recipeKeys.isEmpty()) {
            menu.setItem(2, 7, PaperItemBuilder.from(Material.WRITABLE_BOOK)
                    .name(Text.item("<green>Add to recipe book"))
                    .lore(List.of(
                            Text.item("<gray>unlocks " + recipeKeys.size() + " recipe"
                                    + (recipeKeys.size() == 1 ? "" : "s") + " in your book"),
                            Text.item("<green>Click")))
                    .asGuiItem(event -> {
                        int added = player.discoverRecipes(recipeKeys);
                        player.sendMessage(Text.chat(added > 0
                                ? "<green>Added " + added + " recipe(s) for <white>" + id + "<green> to your recipe book."
                                : "<yellow>Those recipes are already in your recipe book."));
                    }));
        }

        menu.setItem(3, 5, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }

    private Component nameComponent(CustomItem def) {
        String name = def.name();
        return (name == null || name.isEmpty()) ? Text.item("<yellow>" + id) : Text.item(name);
    }

    private List<Component> itemLore(CustomItem def) {
        List<Component> lore = new ArrayList<>();
        if (def.lore() != null) {
            for (String line : def.lore()) lore.add(Text.item(line));
        }
        lore.add(Text.item("<dark_gray>" + def.material().getKey().getKey()));
        return lore;
    }
}
