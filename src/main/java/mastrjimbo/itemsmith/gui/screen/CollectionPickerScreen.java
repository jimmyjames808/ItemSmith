package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.pick.RegistryValuePickerScreen;
import mastrjimbo.itemsmith.gui.pick.ValueProviders;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A reusable "edit a set/list of keys" screen: shows the current values as removable icons and adds new
 * ones through the shared {@link RegistryValuePickerScreen} (so entities/blocks/loot-tables are picked, not
 * typed). Mutates the given working list in place and calls {@code onChange} after every add/remove so the
 * caller can rebuild its immutable record. {@code allowManual} adds a free-text button (for loot wildcards).
 */
public final class CollectionPickerScreen {

    private final GuiManager gui;
    private final Component title;
    private final List<ValueProviders.Option> provider;
    private final List<String> values;
    private final Runnable onChange;
    private final Runnable back;
    private final boolean allowManual;
    private final Map<String, ItemStack> iconByValue = new HashMap<>();

    public CollectionPickerScreen(GuiManager gui, Component title, List<ValueProviders.Option> provider,
                                  List<String> values, Runnable onChange, Runnable back, boolean allowManual) {
        this.gui = gui;
        this.title = title;
        this.provider = provider;
        this.values = values;
        this.onChange = onChange;
        this.back = back;
        this.allowManual = allowManual;
        for (ValueProviders.Option o : provider) iconByValue.putIfAbsent(o.value(), o.icon());
    }

    public void open(Player player) {
        Gui menu = Gui.gui().title(title).rows(6).disableAllInteractions().create();

        int slot = 0;
        for (int i = 0; i < values.size() && slot < 45; i++, slot++) {
            String value = values.get(i);
            int index = i;
            menu.setItem(slot + 1, PaperItemBuilder.from(iconFor(value))
                    .name(Text.item("<yellow>" + value))
                    .lore(List.of(Text.item("<red>Click to remove")))
                    .asGuiItem(event -> {
                        values.remove(index);
                        onChange.run();
                        open(player);
                    }));
        }

        if (values.isEmpty()) {
            menu.setItem(3, 5, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<dark_gray>nothing selected"))
                    .asGuiItem(event -> {
                    }));
        }

        menu.setItem(6, 3, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add"))
                .lore(List.of(Text.item("<gray>pick from the list")))
                .asGuiItem(event -> new RegistryValuePickerScreen(gui, Text.chat("<white>Choose"), provider, "",
                        key -> {
                            if (key != null && !values.contains(key)) {
                                values.add(key);
                                onChange.run();
                            }
                        },
                        () -> open(player)).open(player)));

        if (allowManual) {
            menu.setItem(6, 5, PaperItemBuilder.from(Material.NAME_TAG)
                    .name(Text.item("<aqua>+ Type pattern"))
                    .lore(List.of(Text.item("<gray>wildcard prefix, e.g. chests/")))
                    .asGuiItem(event -> gui.forms().text(player,
                            Forms.TextPrompt.of(Text.chat("<white>Loot table pattern"),
                                    Text.chat("<gray>prefix or full key"), ""),
                            () -> open(player),
                            v -> {
                                if (v == null) return;
                                String t = v.trim();
                                if (!t.isEmpty() && !values.contains(t)) {
                                    values.add(t);
                                    onChange.run();
                                }
                            })));
        }

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }

    private ItemStack iconFor(String value) {
        ItemStack icon = iconByValue.get(value);
        if (icon == null || icon.getItemMeta() == null) return new ItemStack(Material.PAPER);
        return icon.clone();
    }
}
