package mastrjimbo.itemsmith.gui.pick;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * A paginated, searchable chest for choosing one engine {@link mastrjimbo.itemsmith.engine.Component}
 * (activator / condition / targeter / action). Entries are grouped by category (with a per-category
 * icon) and sorted for browsing; the search box filters by id, display name, or category. Selecting a
 * component applies it via {@code onSelect} and returns to the owning screen.
 */
public final class ComponentPickerScreen<T extends mastrjimbo.itemsmith.engine.Component> {

    private final GuiManager gui;
    private final Component title;
    private final List<T> components;
    private final String currentId;
    private final Consumer<T> onSelect;
    private final Runnable back;
    private String filter = "";

    public ComponentPickerScreen(GuiManager gui, Component title, java.util.Collection<T> components,
                                 String currentId, Consumer<T> onSelect, Runnable back) {
        this.gui = gui;
        this.title = title;
        this.components = new ArrayList<>(components);
        this.components.sort(Comparator.comparing((T c) -> c.category())
                .thenComparing(mastrjimbo.itemsmith.engine.Component::displayName));
        this.currentId = currentId == null ? "" : currentId;
        this.onSelect = onSelect;
        this.back = back;
    }

    public void open(Player player) {
        PaginatedGui menu = Gui.paginated()
                .title(title)
                .rows(6)
                .pageSize(45)
                .disableAllInteractions()
                .create();

        String needle = filter.toLowerCase(Locale.ROOT);
        int shown = 0;
        for (T component : components) {
            if (!needle.isEmpty() && !matches(component, needle)) continue;
            boolean selected = component.id().equalsIgnoreCase(currentId);
            menu.addItem(PaperItemBuilder.from(iconFor(component.category()))
                    .name(Text.item((selected ? "<green>▶ " : "<yellow>") + component.displayName()))
                    .lore(describe(component, selected))
                    .asGuiItem(event -> {
                        onSelect.accept(component);
                        back.run();
                    }));
            shown++;
        }
        if (shown == 0) {
            menu.addItem(PaperItemBuilder.from(Material.BARRIER)
                    .name(Text.item("<red>No matches for \"" + filter + "\""))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(6, 2, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Previous"))
                .asGuiItem(event -> menu.previous()));
        menu.setItem(6, 4, PaperItemBuilder.from(Material.OAK_SIGN)
                .name(Text.item("<yellow>Search"))
                .lore(List.of(Text.item(filter.isEmpty() ? "<gray>filter by name / category" : "<gray>filter: <white>" + filter)))
                .asGuiItem(event -> gui.forms().text(player,
                        Forms.TextPrompt.of(Text.chat("<white>Search"), Text.chat("<gray>type to filter"), filter),
                        () -> open(player),
                        v -> this.filter = v)));
        menu.setItem(6, 5, PaperItemBuilder.from(Material.BARRIER)
                .name(Text.item("<red>◀ Back (cancel)"))
                .asGuiItem(event -> back.run()));
        if (!filter.isEmpty()) {
            menu.setItem(6, 6, PaperItemBuilder.from(Material.WATER_BUCKET)
                    .name(Text.item("<white>Clear filter"))
                    .asGuiItem(event -> {
                        this.filter = "";
                        open(player);
                    }));
        }
        menu.setItem(6, 8, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>Next ▶"))
                .asGuiItem(event -> menu.next()));

        menu.open(player);
    }

    private boolean matches(T component, String needle) {
        return component.id().toLowerCase(Locale.ROOT).contains(needle)
                || component.displayName().toLowerCase(Locale.ROOT).contains(needle)
                || component.category().toLowerCase(Locale.ROOT).contains(needle);
    }

    private List<Component> describe(T component, boolean selected) {
        List<Component> lore = new ArrayList<>();
        lore.add(Text.item("<dark_gray>" + component.category() + " · " + component.id()));
        if (component.description() != null && !component.description().isEmpty()) {
            lore.add(Text.item("<gray>" + component.description()));
        }
        lore.add(Component.empty());
        lore.add(selected ? Text.item("<green>current") : Text.item("<green>Click to choose"));
        return lore;
    }

    private Material iconFor(String category) {
        return switch (category) {
            case "Combat" -> Material.IRON_SWORD;
            case "Movement" -> Material.FEATHER;
            case "Effects" -> Material.GLASS_BOTTLE;
            case "World" -> Material.GRASS_BLOCK;
            case "Player" -> Material.PLAYER_HEAD;
            case "Economy" -> Material.GOLD_INGOT;
            case "Command" -> Material.COMMAND_BLOCK;
            case "Visual" -> Material.FIREWORK_STAR;
            case "Meta" -> Material.REPEATER;
            case "Condition" -> Material.COMPARATOR;
            case "Targeter" -> Material.ENDER_EYE;
            case "Interact" -> Material.STICK;
            case "Block" -> Material.GRASS_BLOCK;
            case "Item" -> Material.CHEST;
            case "Projectile" -> Material.ARROW;
            case "Lifecycle" -> Material.CLOCK;
            default -> Material.PAPER;
        };
    }
}
