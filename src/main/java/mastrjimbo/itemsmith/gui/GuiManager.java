package mastrjimbo.itemsmith.gui;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.gui.form.DialogBridge;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.screen.CatalogScreen;
import mastrjimbo.itemsmith.gui.screen.ItemEditorScreen;
import mastrjimbo.itemsmith.gui.screen.ItemListScreen;
import mastrjimbo.itemsmith.gui.screen.TemplatePickerScreen;
import mastrjimbo.itemsmith.registry.Registries;
import mastrjimbo.itemsmith.store.ItemStore;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Entry point and owner of the in-game item creator. Holds one {@link EditSession} per editing player,
 * opens the screens, and runs the save path (draft → {@link CustomItem} → {@link ItemStore#save} →
 * {@code plugin.reload()}). Constructed after item loading and injected the registries / store /
 * registry it needs, matching the plugin's constructor-injection style.
 */
public final class GuiManager {

    private final ItemSmith plugin;
    private final Registries registries;
    private final ItemStore store;
    private final ItemRegistry registry;
    private final Forms forms;
    private final Map<UUID, EditSession> sessions = new HashMap<>();

    public GuiManager(ItemSmith plugin, Registries registries, ItemStore store, ItemRegistry registry) {
        this.plugin = plugin;
        this.registries = registries;
        this.store = store;
        this.registry = registry;
        this.forms = new DialogBridge(plugin);
    }

    /** The native-dialog value-entry bridge shared by every parameter editor. */
    public Forms forms() {
        return forms;
    }

    /** Opens the item list (the creator's home screen). */
    public void openList(Player player) {
        new ItemListScreen(this).open(player);
    }

    /** Opens the read-only player catalog (browse items + how to obtain + add to recipe book). */
    public void openCatalog(Player player) {
        new CatalogScreen(this).open(player);
    }

    /** Hydrates the given item into a fresh draft session and opens its editor. */
    public void edit(Player player, String id) {
        CustomItem item = registry.get(id);
        if (item == null) {
            player.sendMessage(Text.chat("<red>Unknown item '" + id + "'."));
            return;
        }
        EditSession session = new EditSession(player.getUniqueId(), ItemDraft.hydrate(item));
        sessions.put(player.getUniqueId(), session);
        new ItemEditorScreen(this, session).open(player);
    }

    /** Opens the template picker to start a brand-new item. */
    public void openNew(Player player) {
        new TemplatePickerScreen(this).open(player);
    }

    /** Starts editing a freshly-built (not-yet-saved) draft — used by new-item and Save-As flows. */
    public void startDraft(Player player, ItemDraft draft) {
        EditSession session = new EditSession(player.getUniqueId(), draft);
        sessions.put(player.getUniqueId(), session);
        new ItemEditorScreen(this, session).open(player);
    }

    /** Lowers the session's draft, writes it, and hot-reloads. @return reloaded item count, or -1 on failure. */
    public int save(Player player, EditSession session) {
        try {
            store.save(session.draft().toCustomItem());
        } catch (IOException e) {
            player.sendMessage(Text.chat("<red>Save failed: " + e.getMessage()));
            return -1;
        }
        return plugin.reload();
    }

    /** Writes the current draft under a NEW id (Save As), leaving the original untouched. @return count or -1. */
    public int saveAs(Player player, EditSession session, String newId) {
        CustomItem base = session.draft().toCustomItem();
        CustomItem copy = new CustomItem(newId, base.material(), base.itemModel(), base.customModelData(),
                base.name(), base.lore(), base.recipes(), base.abilities(), base.charges(), base.maxCharges(),
                base.onDepletion(), base.durabilityBar(), base.drops(), base.loot());
        try {
            store.save(copy);
        } catch (IOException e) {
            player.sendMessage(Text.chat("<red>Save failed: " + e.getMessage()));
            return -1;
        }
        return plugin.reload();
    }

    public EditSession session(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public void endSession(Player player) {
        sessions.remove(player.getUniqueId());
    }

    public ItemSmith plugin() {
        return plugin;
    }

    public Registries registries() {
        return registries;
    }

    public ItemStore store() {
        return store;
    }

    public ItemRegistry registry() {
        return registry;
    }
}
