package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.Icons;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.pick.RegistryValuePickerScreen;
import mastrjimbo.itemsmith.gui.pick.ValueProviders;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The item hub. For L2 this is a read-only view of the draft's fields plus Save / Back — enough to
 * prove the hydrate → draft → {@link ItemDraft#toCustomItem()} → save round-trip through the GUI leaves
 * an unchanged item. Per-field editors are wired in as later checkpoints land.
 */
public final class ItemEditorScreen {

    private final GuiManager gui;
    private final EditSession session;

    public ItemEditorScreen(GuiManager gui, EditSession session) {
        this.gui = gui;
        this.session = session;
    }

    public void open(Player player) {
        ItemDraft draft = session.draft();
        Gui menu = Gui.gui()
                .title(Text.chat("<gradient:#4fc3f7:#b388ff>Edit</gradient> <white>" + draft.id()))
                .rows(6)
                .disableAllInteractions()
                .create();

        // Live preview of the actual built item (identity intact — but it sits in a non-interactive slot).
        menu.setItem(1, 5, PaperItemBuilder.from(iconOf(draft))
                .name(Text.item(draft.name() == null || draft.name().isEmpty()
                        ? "<gray><italic>(no name)" : draft.name()))
                .lore(previewLore(draft))
                .asGuiItem(event -> {
                }));

        menu.setItem(3, 2, PaperItemBuilder.from(draft.material())
                .name(Text.item("<aqua>Material"))
                .lore(List.of(Text.item("<gray>" + draft.material().getKey().getKey()),
                        Text.item("<green>Click to change")))
                .asGuiItem(event -> new RegistryValuePickerScreen(gui,
                        Text.chat("<white>Choose <yellow>material"),
                        ValueProviders.options(ParamType.MATERIAL, gui.registry()),
                        draft.material().getKey().getKey(),
                        key -> {
                            Material picked = Material.matchMaterial(key);
                            if (picked != null && picked.isItem()) draft.setMaterial(picked);
                        },
                        () -> open(player)).open(player)));
        menu.setItem(3, 3, PaperItemBuilder.from(Material.NAME_TAG)
                .name(Text.item("<aqua>Name"))
                .lore(List.of(draft.name() == null || draft.name().isEmpty()
                                ? Text.item("<dark_gray>(none)") : Text.item(draft.name()),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().text(player,
                        Forms.TextPrompt.of(Text.chat("<white>Edit display name"),
                                Text.chat("<gray>MiniMessage supported"), draft.name()),
                        () -> open(player),
                        draft::setName)));
        menu.setItem(3, 4, PaperItemBuilder.from(Material.WRITABLE_BOOK)
                .name(Text.item("<aqua>Lore"))
                .lore(loreLines(draft))
                .asGuiItem(event -> new LoreEditorScreen(gui, draft.lore(), () -> open(player)).open(player)));
        menu.setItem(3, 5, PaperItemBuilder.from(Material.NETHER_STAR)
                .name(Text.item("<aqua>Abilities"))
                .lore(List.of(Text.item("<gray>" + draft.abilities().size() + " defined"),
                        Text.item("<green>Click to open")))
                .asGuiItem(event -> new AbilityListScreen(gui, session).open(player)));
        menu.setItem(3, 6, PaperItemBuilder.from(Material.AMETHYST_SHARD)
                .name(Text.item("<aqua>Charges & settings"))
                .lore(List.of(
                        Text.item(draft.charges() == null ? "<gray>no charges"
                                : "<gray>charges " + draft.charges() + "/" + draft.maxCharges()),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> new ItemSettingsScreen(gui, session, () -> open(player)).open(player)));
        menu.setItem(3, 7, PaperItemBuilder.from(Material.EXPERIENCE_BOTTLE)
                .name(Text.item("<aqua>Stats"))
                .lore(List.of(Text.item("<gray>" + draft.stats().size() + " defined"),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> new StatsEditorScreen(gui, draft, () -> open(player)).open(player)));

        // Obtaining row: how players get this item.
        menu.setItem(4, 3, PaperItemBuilder.from(Material.CRAFTING_TABLE)
                .name(Text.item("<gold>Recipes"))
                .lore(List.of(Text.item("<gray>" + recipeSummary(draft)),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> new RecipeListScreen(gui, draft, () -> open(player)).open(player)));
        menu.setItem(4, 5, PaperItemBuilder.from(Material.ZOMBIE_HEAD)
                .name(Text.item("<gold>Drops"))
                .lore(List.of(Text.item("<gray>" + dropsSummary(draft)),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> new DropsEditorScreen(gui, draft, () -> open(player)).open(player)));
        menu.setItem(4, 7, PaperItemBuilder.from(Material.CHEST)
                .name(Text.item("<gold>Loot tables"))
                .lore(List.of(Text.item("<gray>" + lootSummary(draft)),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> new LootEditorScreen(gui, draft, () -> open(player)).open(player)));

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back to list"))
                .asGuiItem(event -> gui.openList(player)));
        menu.setItem(6, 4, PaperItemBuilder.from(Material.DROPPER)
                .name(Text.item("<yellow>Give me one"))
                .lore(List.of(Text.item("<gray>get the saved item in-hand")))
                .asGuiItem(event -> giveToSelf(player)));
        menu.setItem(6, 8, PaperItemBuilder.from(Material.PAPER)
                .name(Text.item("<yellow>Save As…"))
                .lore(List.of(Text.item("<gray>save a copy under a new id")))
                .asGuiItem(event -> saveAs(player)));
        menu.setItem(6, 9, PaperItemBuilder.from(Material.EMERALD_BLOCK)
                .name(Text.item("<green>Save"))
                .lore(List.of(Text.item("<gray>Write to <white>" + draft.id() + ".yml<gray> and reload")))
                .asGuiItem(event -> {
                    int count = gui.save(player, session);
                    if (count >= 0) {
                        player.sendMessage(Text.chat("<green>Saved <white>" + draft.id()
                                + "<green> — reloaded <white>" + count + "<green> item(s)."));
                    }
                    gui.openList(player);
                }));

        menu.open(player);
    }

    private String recipeSummary(ItemDraft draft) {
        var recipes = draft.recipes();
        if (recipes.isEmpty()) return "none";
        if (recipes.size() == 1) return recipes.get(0).type();
        return recipes.size() + " recipes";
    }

    private String dropsSummary(ItemDraft draft) {
        var d = draft.drops();
        if (d == null || d.isEmpty()) return "none";
        int mob = d.mobDrops().size();
        int block = d.blockDrops().size();
        return mob + " mob · " + block + " block";
    }

    private String lootSummary(ItemDraft draft) {
        var l = draft.loot();
        if (l == null || l.isEmpty()) return "none";
        return l.rules().size() + " rule(s)";
    }

    /** Admin give: hands the player the saved build of this item (save first if it's brand-new). */
    private void giveToSelf(Player player) {
        var item = gui.registry().build(session.draft().id());
        if (item == null) {
            player.sendMessage(Text.chat("<yellow>Save the item first, then it can be given."));
            return;
        }
        player.getInventory().addItem(item);
        player.sendMessage(Text.chat("<green>Gave you <white>" + session.draft().id() + "<green>."));
    }

    /** Prompts for a new id and writes a copy of the current draft under it, then edits the copy. */
    private void saveAs(Player player) {
        String[] validId = {null};
        gui.forms().text(player,
                Forms.TextPrompt.of(Text.chat("<white>Save As — new id"), Text.chat("<gray>a-z, 0-9, _"), ""),
                () -> {
                    if (validId[0] != null) {
                        int count = gui.saveAs(player, session, validId[0]);
                        if (count >= 0) {
                            player.sendMessage(Text.chat("<green>Saved copy as <white>" + validId[0]
                                    + "<green> — reloaded <white>" + count + "<green> item(s)."));
                            gui.edit(player, validId[0]);
                            return;
                        }
                    }
                    open(player);
                },
                input -> {
                    String id = input.trim().toLowerCase(java.util.Locale.ROOT).replace(' ', '_');
                    if (!id.matches("[a-z0-9_]+")) {
                        player.sendMessage(Text.chat("<red>Invalid id '" + input + "'."));
                        return;
                    }
                    if (gui.registry().get(id) != null) {
                        player.sendMessage(Text.chat("<red>'" + id + "' already exists."));
                        return;
                    }
                    validId[0] = id;
                });
    }

    private ItemStack iconOf(ItemDraft draft) {
        return Icons.display(draft.material(), draft.itemModel(), draft.customModelData());
    }

    private List<Component> previewLore(ItemDraft draft) {
        List<Component> lore = new ArrayList<>();
        for (String line : draft.lore()) {
            lore.add(Text.item(line));
        }
        if (!lore.isEmpty()) lore.add(Component.empty());
        lore.add(Text.item("<dark_gray>live preview"));
        return lore;
    }

    private List<Component> loreLines(ItemDraft draft) {
        List<Component> lore = new ArrayList<>();
        if (draft.lore().isEmpty()) {
            lore.add(Text.item("<dark_gray>(no lore)"));
            return lore;
        }
        for (String line : draft.lore()) {
            lore.add(Text.item(line));
        }
        return lore;
    }
}
