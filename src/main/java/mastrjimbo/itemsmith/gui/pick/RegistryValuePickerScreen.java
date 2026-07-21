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
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * A paginated, searchable chest of values (potion effects, materials, particles, item ids…). Clicking
 * an option applies it and returns to the owning screen; a Search button opens a text dialog that
 * filters the list. Used by {@link mastrjimbo.itemsmith.gui.param.ParamEditor} for the registry-backed
 * parameter types so users pick from a list instead of typing a raw id.
 */
public final class RegistryValuePickerScreen {

    private final GuiManager gui;
    private final Component title;
    private final List<ValueProviders.Option> options;
    private final String current;
    private final Consumer<String> onSelect;
    private final Runnable back;
    private String filter = "";

    public RegistryValuePickerScreen(GuiManager gui, Component title, List<ValueProviders.Option> options,
                                     String current, Consumer<String> onSelect, Runnable back) {
        this.gui = gui;
        this.title = title;
        this.options = options;
        this.current = current == null ? "" : current;
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
        for (ValueProviders.Option option : options) {
            if (!needle.isEmpty() && !option.value().toLowerCase(Locale.ROOT).contains(needle)) continue;
            boolean selected = option.value().equalsIgnoreCase(current);
            // Defensive: a meta-less icon (e.g. an air-like material) would NPE PaperItemBuilder.name().
            ItemStack icon = option.icon() != null && option.icon().getItemMeta() != null
                    ? option.icon().clone() : new ItemStack(Material.PAPER);
            menu.addItem(PaperItemBuilder.from(icon)
                    .name(Text.item((selected ? "<green>▶ " : "<yellow>") + option.value()))
                    .lore(List.of(selected ? Text.item("<green>current") : Text.item("<gray>Click to select")))
                    .asGuiItem(event -> {
                        onSelect.accept(option.value());
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
                .lore(List.of(Text.item(filter.isEmpty() ? "<gray>filter the list" : "<gray>filter: <white>" + filter)))
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
}
