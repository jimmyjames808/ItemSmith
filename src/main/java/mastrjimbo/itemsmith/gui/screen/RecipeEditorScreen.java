package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.pick.RegistryValuePickerScreen;
import mastrjimbo.itemsmith.gui.pick.ValueProviders;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.recipe.RecipeSpec;
import mastrjimbo.itemsmith.recipe.RecipeSpec.Cooking.Kind;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Edits one recipe (by index into the draft's recipe list). A type selector across all Bukkit-native
 * families reshapes the editor: a 3×3 grid for shaped, an ingredient list for shapeless, an input +
 * experience + cook-time for the smelting family, three inputs for smithing, one for the stonecutter.
 * Every edit rebuilds the immutable {@link RecipeSpec} back into the draft, so what you see is what saves.
 */
public final class RecipeEditorScreen {

    private static final String[] TYPES =
            {"shaped", "shapeless", "furnace", "blasting", "smoking", "campfire", "smithing", "stonecutting"};

    private static final int[][] GRID = {{3, 4}, {3, 5}, {3, 6}, {4, 4}, {4, 5}, {4, 6}, {5, 4}, {5, 5}, {5, 6}};
    // A complete frame around the 3×3 grid (rows 3-5, cols 4-6) so the fillable cells read as a boxed area.
    private static final int[][] FRAME = {
            {2, 3}, {2, 4}, {2, 5}, {2, 6}, {2, 7},   // top
            {6, 3}, {6, 4}, {6, 5}, {6, 6}, {6, 7},   // bottom
            {3, 3}, {4, 3}, {5, 3},                   // left
            {3, 7}, {4, 7}, {5, 7}};                  // right

    private final GuiManager gui;
    private final ItemDraft draft;
    private final int index;
    private final Runnable back;

    public RecipeEditorScreen(GuiManager gui, ItemDraft draft, int index, Runnable back) {
        this.gui = gui;
        this.draft = draft;
        this.index = index;
        this.back = back;
    }

    private RecipeSpec current() {
        return draft.recipes().get(index);
    }

    private void set(RecipeSpec spec) {
        draft.recipes().set(index, spec);
    }

    public void open(Player player) {
        RecipeSpec r = current();
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Recipe <dark_gray>· <gray>" + r.type()))
                .rows(6)
                .disableAllInteractions()
                .create();

        // Type selector row.
        for (int i = 0; i < TYPES.length; i++) {
            String type = TYPES[i];
            boolean selected = r.type().equals(type);
            menu.setItem(1, i + 1, PaperItemBuilder.from(iconForType(type))
                    .name(Text.item((selected ? "<green>▶ " : "<gray>") + type))
                    .lore(List.of(Text.item(selected ? "<green>current" : "<gray>click to switch")))
                    .asGuiItem(event -> {
                        if (!selected) {
                            convertTo(type);
                            open(player);
                        }
                    }));
        }

        switch (r) {
            case RecipeSpec.Shaped s -> renderShaped(menu, player, s);
            case RecipeSpec.Shapeless s -> renderShapeless(menu, player, s);
            case RecipeSpec.Cooking c -> renderCooking(menu, player, c);
            case RecipeSpec.Smithing s -> renderSmithing(menu, player, s);
            case RecipeSpec.Stonecutting s -> renderStonecutting(menu, player, s);
        }

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }

    /** Draws the grey frame around the crafting grid so its cells stand out from the empty background. */
    private void drawFrame(Gui menu) {
        for (int[] rc : FRAME) {
            menu.setItem(rc[0], rc[1], PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Component.empty())
                    .asGuiItem(event -> {
                    }));
        }
    }

    // ---- shaped ----

    private void renderShaped(Gui menu, Player player, RecipeSpec.Shaped s) {
        drawFrame(menu);
        Material[] grid = gridOf(s);
        for (int i = 0; i < GRID.length; i++) {
            menu.setItem(GRID[i][0], GRID[i][1], cell(player, grid, i));
        }
    }

    private GuiItem cell(Player player, Material[] grid, int i) {
        Material m = grid[i];
        if (m == null) {
            return PaperItemBuilder.from(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<dark_gray>empty"))
                    .lore(List.of(Text.item("<green>Click to set")))
                    .asGuiItem(event -> pickCell(player, i));
        }
        return PaperItemBuilder.from(m)
                .name(Text.item("<yellow>" + m.getKey().getKey()))
                .lore(List.of(Text.item("<green>Left: change  <red>Right: clear")))
                .asGuiItem(event -> {
                    if (event.getClick() == ClickType.RIGHT) {
                        Material[] g = gridOf((RecipeSpec.Shaped) current());
                        g[i] = null;
                        set(buildShaped(g));
                        open(player);
                    } else {
                        pickCell(player, i);
                    }
                });
    }

    private void pickCell(Player player, int i) {
        new RegistryValuePickerScreen(gui, Text.chat("<white>Choose <yellow>material"),
                ValueProviders.options(ParamType.MATERIAL, gui.registry()), "",
                key -> {
                    Material m = Material.matchMaterial(key);
                    if (m != null) {
                        Material[] g = gridOf((RecipeSpec.Shaped) current());
                        g[i] = m;
                        set(buildShaped(g));
                    }
                },
                () -> open(player)).open(player);
    }

    private Material[] gridOf(RecipeSpec.Shaped s) {
        Material[] g = new Material[9];
        List<String> rows = s.shape();
        for (int r = 0; r < rows.size() && r < 3; r++) {
            String line = rows.get(r);
            for (int c = 0; c < line.length() && c < 3; c++) {
                g[r * 3 + c] = s.ingredients().get(line.charAt(c));
            }
        }
        return g;
    }

    private RecipeSpec buildShaped(Material[] grid) {
        Map<Material, Character> letters = new LinkedHashMap<>();
        char next = 'A';
        List<String> shape = new ArrayList<>();
        for (int row = 0; row < 3; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < 3; col++) {
                Material m = grid[row * 3 + col];
                if (m == null) {
                    line.append(' ');
                } else {
                    Character ch = letters.get(m);
                    if (ch == null) {
                        ch = next++;
                        letters.put(m, ch);
                    }
                    line.append(ch);
                }
            }
            shape.add(line.toString());
        }
        Map<Character, Material> ingredients = new HashMap<>();
        for (Map.Entry<Material, Character> e : letters.entrySet()) {
            ingredients.put(e.getValue(), e.getKey());
        }
        return new RecipeSpec.Shaped(shape, ingredients);
    }

    // ---- shapeless ----

    private void renderShapeless(Gui menu, Player player, RecipeSpec.Shapeless s) {
        drawFrame(menu);
        List<Material> mats = s.materials();
        for (int i = 0; i < GRID.length; i++) {
            int cellIndex = i;
            if (i < mats.size()) {
                Material m = mats.get(i);
                menu.setItem(GRID[i][0], GRID[i][1], PaperItemBuilder.from(m)
                        .name(Text.item("<yellow>" + m.getKey().getKey()))
                        .lore(List.of(Text.item("<red>Shift-click: remove")))
                        .asGuiItem(event -> {
                            if (event.isShiftClick()) {
                                List<Material> list = new ArrayList<>(((RecipeSpec.Shapeless) current()).materials());
                                if (cellIndex < list.size()) list.remove(cellIndex);
                                set(new RecipeSpec.Shapeless(list));
                                open(player);
                            }
                        }));
            } else {
                menu.setItem(GRID[i][0], GRID[i][1], PaperItemBuilder.from(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                        .name(Text.item("<dark_gray>empty"))
                        .lore(List.of(Text.item("<green>Click to add ingredient")))
                        .asGuiItem(event -> new RegistryValuePickerScreen(gui,
                                Text.chat("<white>Choose <yellow>ingredient"),
                                ValueProviders.options(ParamType.MATERIAL, gui.registry()), "",
                                key -> {
                                    Material m = Material.matchMaterial(key);
                                    if (m != null) {
                                        List<Material> list =
                                                new ArrayList<>(((RecipeSpec.Shapeless) current()).materials());
                                        list.add(m);
                                        set(new RecipeSpec.Shapeless(list));
                                    }
                                },
                                () -> open(player)).open(player)));
            }
        }
    }

    // ---- cooking ----

    private void renderCooking(Gui menu, Player player, RecipeSpec.Cooking c) {
        menu.setItem(3, 3, materialButton(player, "<aqua>Input", c.input(),
                m -> set(new RecipeSpec.Cooking(c.kind(), m, c.experience(), c.cookTime()))));
        menu.setItem(3, 5, PaperItemBuilder.from(Material.EXPERIENCE_BOTTLE)
                .name(Text.item("<aqua>Experience"))
                .lore(List.of(Text.item("<gray>" + c.experience()), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Experience"), Text.chat("<gray>XP granted"),
                                c.experience(), 0.0, 100.0, false),
                        () -> open(player),
                        v -> set(new RecipeSpec.Cooking(c.kind(), c.input(), (float) (double) v, c.cookTime())))));
        menu.setItem(3, 7, PaperItemBuilder.from(Material.CLOCK)
                .name(Text.item("<aqua>Cook time"))
                .lore(List.of(Text.item("<gray>" + c.cookTime() + " ticks"), Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Cook time (ticks)"),
                                Text.chat("<gray>20 ticks = 1s"), c.cookTime(), 1.0, 10000.0, true),
                        () -> open(player),
                        v -> set(new RecipeSpec.Cooking(c.kind(), c.input(), c.experience(),
                                Math.max(1, (int) Math.round(v)))))));
    }

    // ---- smithing ----

    private void renderSmithing(Gui menu, Player player, RecipeSpec.Smithing s) {
        menu.setItem(3, 3, materialButton(player, "<aqua>Template", s.template(),
                m -> set(new RecipeSpec.Smithing(m, s.base(), s.addition()))));
        menu.setItem(3, 5, materialButton(player, "<aqua>Base", s.base(),
                m -> set(new RecipeSpec.Smithing(s.template(), m, s.addition()))));
        menu.setItem(3, 7, materialButton(player, "<aqua>Addition", s.addition(),
                m -> set(new RecipeSpec.Smithing(s.template(), s.base(), m))));
    }

    // ---- stonecutting ----

    private void renderStonecutting(Gui menu, Player player, RecipeSpec.Stonecutting s) {
        menu.setItem(3, 5, materialButton(player, "<aqua>Input", s.input(),
                m -> set(new RecipeSpec.Stonecutting(m))));
    }

    // ---- shared ----

    private GuiItem materialButton(Player player, String label, Material current, java.util.function.Consumer<Material> onPick) {
        return PaperItemBuilder.from(current.isItem() ? current : Material.PAPER)
                .name(Text.item(label))
                .lore(List.of(Text.item("<gray>" + current.getKey().getKey()), Text.item("<green>Click to change")))
                .asGuiItem(event -> new RegistryValuePickerScreen(gui, Text.chat("<white>Choose <yellow>material"),
                        ValueProviders.options(ParamType.MATERIAL, gui.registry()), current.getKey().getKey(),
                        key -> {
                            Material m = Material.matchMaterial(key);
                            if (m != null) onPick.accept(m);
                        },
                        () -> open(player)).open(player));
    }

    private void convertTo(String type) {
        RecipeSpec cur = current();
        Kind targetKind = Kind.from(type);
        RecipeSpec next;
        if (targetKind != null && cur instanceof RecipeSpec.Cooking c) {
            next = new RecipeSpec.Cooking(targetKind, c.input(), c.experience(), c.cookTime()); // keep input/xp/time
        } else {
            next = defaultOf(type);
        }
        set(next);
    }

    private RecipeSpec defaultOf(String type) {
        Kind kind = Kind.from(type);
        if (kind != null) {
            return new RecipeSpec.Cooking(kind, Material.COBBLESTONE, 0.1f, kind.defaultCookTime());
        }
        return switch (type) {
            case "shapeless" -> new RecipeSpec.Shapeless(new ArrayList<>());
            case "smithing" -> new RecipeSpec.Smithing(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                    Material.IRON_INGOT, Material.DIAMOND);
            case "stonecutting" -> new RecipeSpec.Stonecutting(Material.STONE);
            default -> new RecipeSpec.Shaped(List.of("   ", "   ", "   "), new HashMap<>());
        };
    }

    private Material iconForType(String type) {
        return switch (type) {
            case "shapeless" -> Material.CRAFTING_TABLE;
            case "furnace" -> Material.FURNACE;
            case "blasting" -> Material.BLAST_FURNACE;
            case "smoking" -> Material.SMOKER;
            case "campfire" -> Material.CAMPFIRE;
            case "smithing" -> Material.SMITHING_TABLE;
            case "stonecutting" -> Material.STONECUTTER;
            default -> Material.CRAFTING_TABLE;
        };
    }
}
