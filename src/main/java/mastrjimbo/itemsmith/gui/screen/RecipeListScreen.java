package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.recipe.RecipeSpec;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Lists the draft's recipes (an item may have several across families) with add / edit / remove. Editing
 * opens {@link RecipeEditorScreen} on that recipe; adding appends a blank shaped recipe to edit. Recipes
 * left empty (no ingredients) are pruned when leaving.
 */
public final class RecipeListScreen {

    private final GuiManager gui;
    private final ItemDraft draft;
    private final Runnable back;

    public RecipeListScreen(GuiManager gui, ItemDraft draft, Runnable back) {
        this.gui = gui;
        this.draft = draft;
        this.back = back;
    }

    public void open(Player player) {
        List<RecipeSpec> recipes = draft.recipes();
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Recipes <dark_gray>· <gray>" + recipes.size()))
                .rows(6)
                .disableAllInteractions()
                .create();

        int slot = 0;
        for (int i = 0; i < recipes.size() && slot < 45; i++, slot++) {
            RecipeSpec r = recipes.get(i);
            int index = i;
            menu.setItem(slot + 1, PaperItemBuilder.from(iconFor(r))
                    .name(Text.item("<yellow>" + r.type()))
                    .lore(List.of(Text.item("<gray>" + summary(r)),
                            Text.item("<green>Left-click: edit"),
                            Text.item("<red>Shift-click: remove")))
                    .asGuiItem(event -> {
                        if (event.isShiftClick()) {
                            recipes.remove(index);
                            open(player);
                        } else {
                            new RecipeEditorScreen(gui, draft, index, () -> open(player)).open(player);
                        }
                    }));
        }

        menu.setItem(6, 3, PaperItemBuilder.from(Material.LIME_DYE)
                .name(Text.item("<green>+ Add recipe"))
                .lore(List.of(Text.item("<gray>pick the type in the editor")))
                .asGuiItem(event -> {
                    recipes.add(new RecipeSpec.Shaped(List.of("   ", "   ", "   "), new HashMap<>()));
                    new RecipeEditorScreen(gui, draft, recipes.size() - 1, () -> open(player)).open(player);
                }));

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> {
                    prune();
                    back.run();
                }));

        menu.open(player);
    }

    /** Drops recipes that were started but never given ingredients, so nothing invalid is saved. */
    private void prune() {
        draft.recipes().removeIf(r ->
                (r instanceof RecipeSpec.Shaped s && s.ingredients().isEmpty())
                        || (r instanceof RecipeSpec.Shapeless sl && sl.materials().isEmpty()));
    }

    static Material iconFor(RecipeSpec r) {
        return switch (r) {
            case RecipeSpec.Shaped s -> Material.CRAFTING_TABLE;
            case RecipeSpec.Shapeless s -> Material.CRAFTING_TABLE;
            case RecipeSpec.Cooking c -> switch (c.kind()) {
                case FURNACE -> Material.FURNACE;
                case BLASTING -> Material.BLAST_FURNACE;
                case SMOKING -> Material.SMOKER;
                case CAMPFIRE -> Material.CAMPFIRE;
            };
            case RecipeSpec.Smithing s -> Material.SMITHING_TABLE;
            case RecipeSpec.Stonecutting s -> Material.STONECUTTER;
        };
    }

    static String summary(RecipeSpec r) {
        return switch (r) {
            case RecipeSpec.Shaped s -> s.ingredients().isEmpty() ? "empty grid" : s.ingredients().size() + " ingredient(s)";
            case RecipeSpec.Shapeless s -> s.materials().size() + " ingredient(s)";
            case RecipeSpec.Cooking c -> "from " + c.input().getKey().getKey();
            case RecipeSpec.Smithing s -> s.base().getKey().getKey() + " + " + s.addition().getKey().getKey();
            case RecipeSpec.Stonecutting s -> "from " + s.input().getKey().getKey();
        };
    }
}
