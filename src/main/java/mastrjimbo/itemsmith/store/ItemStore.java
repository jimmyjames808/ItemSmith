package mastrjimbo.itemsmith.store;

import mastrjimbo.itemsmith.engine.CustomItem;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The per-item YAML store: one {@code <id>.yml} file per item under
 * {@code plugins/ItemSmith/items/}, the source of truth for the whole plugin.
 * Files are hand-editable and shareable; the GUI (M5) will read and write these
 * same files. On first run a couple of example items are copied in so the plugin
 * has something to demonstrate.
 */
public final class ItemStore {

    /** Bundled example items copied to disk on first run. */
    private static final List<String> DEFAULT_ITEMS = List.of("venom_blade.yml", "frost_axe.yml");

    private final Plugin plugin;
    private final ItemParser parser;
    private final ItemSerializer serializer;
    private final File itemsDir;

    public ItemStore(Plugin plugin, ItemParser parser, ItemSerializer serializer) {
        this.plugin = plugin;
        this.parser = parser;
        this.serializer = serializer;
        this.itemsDir = new File(plugin.getDataFolder(), "items");
    }

    /** The model→YAML writer, exposed so the GUI creator can save edited items. */
    public ItemSerializer serializer() {
        return serializer;
    }

    /** Creates the items folder and seeds the example files if it doesn't exist yet. */
    public void ensureDefaults() {
        if (itemsDir.isDirectory()) return;
        if (!itemsDir.mkdirs()) {
            plugin.getLogger().warning("Could not create items folder at " + itemsDir);
            return;
        }
        for (String name : DEFAULT_ITEMS) {
            try (InputStream in = plugin.getResource("default-items/" + name)) {
                if (in == null) continue;
                Files.copy(in, new File(itemsDir, name).toPath());
            } catch (IOException e) {
                plugin.getLogger().warning("Could not write default item '" + name + "': " + e.getMessage());
            }
        }
    }

    /** Loads and parses every {@code *.yml} in the items folder, ordered by filename. */
    public Map<String, CustomItem> loadAll() {
        Map<String, CustomItem> result = new LinkedHashMap<>();
        File[] files = itemsDir.listFiles((dir, n) -> n.toLowerCase(Locale.ROOT).endsWith(".yml"));
        if (files == null) return result;
        Arrays.sort(files, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        for (File file : files) {
            String id = file.getName().substring(0, file.getName().length() - ".yml".length())
                    .toLowerCase(Locale.ROOT);
            if (!id.matches("[a-z0-9_]+")) {
                plugin.getLogger().warning("Item file '" + file.getName()
                        + "' has an invalid id (use only a-z, 0-9, _); skipping.");
                continue;
            }
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            CustomItem item = parser.parse(id, cfg);
            if (item != null) result.put(id, item);
        }
        return result;
    }

    /**
     * Serializes an item and writes it to {@code <id>.yml}, atomically (temp file + move) so a crash
     * mid-write can never leave a half-written item file. The in-memory registry is refreshed
     * separately by the caller via {@code ItemSmith.reload()}.
     */
    public void save(CustomItem item) throws IOException {
        // The id becomes a filename, so re-validate it here too (callers already do) — a stray
        // '../' or slash must never let a write escape the items folder.
        String id = item.id();
        if (id == null || !id.matches("[a-z0-9_]+")) {
            throw new IOException("Refusing to save item with invalid id '" + id + "' (use only a-z, 0-9, _).");
        }
        if (!itemsDir.isDirectory() && !itemsDir.mkdirs()) {
            throw new IOException("Could not create items folder at " + itemsDir);
        }
        File target = new File(itemsDir, id + ".yml");
        File temp = new File(itemsDir, id + ".yml.tmp");
        serializer.serialize(item).save(temp);
        try {
            Files.move(temp.toPath(), target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicFailed) {
            // Some filesystems don't support ATOMIC_MOVE across the same dir; fall back to a plain replace.
            Files.move(temp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /** Parses an item from a YAML string (used by the GUI and round-trip checks). Null on parse failure. */
    public CustomItem parse(String id, String yaml) {
        YamlConfiguration cfg = new YamlConfiguration();
        try {
            cfg.loadFromString(yaml);
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().warning("Could not parse item '" + id + "' from string: " + e.getMessage());
            return null;
        }
        return parser.parse(id, cfg);
    }

    public File itemsDir() {
        return itemsDir;
    }
}
