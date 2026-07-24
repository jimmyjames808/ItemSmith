package mastrjimbo.itemsmith.gui.draft;

import mastrjimbo.itemsmith.drops.DropSources;
import mastrjimbo.itemsmith.engine.Ability;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.gate.DepletionPolicy;
import mastrjimbo.itemsmith.loot.LootInjection;
import mastrjimbo.itemsmith.recipe.RecipeSpec;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The whole in-progress item the creator edits, mirroring {@link CustomItem} with mutable fields.
 * Hydrated from a loaded item on open, lowered back to an immutable {@link CustomItem} on save. The id
 * is fixed at creation (rename is "Save As"). An item may hold several recipes ({@link #recipes()}).
 */
public final class ItemDraft {

    private final String id;
    private Material material;
    private NamespacedKey itemModel;
    private Integer customModelData;
    private String name;
    private final List<String> lore;
    private final List<RecipeSpec> recipes;
    private final List<AbilityDraft> abilities;
    private Integer charges;
    private Integer maxCharges;
    private DepletionPolicy onDepletion;
    private boolean durabilityBar;
    private DropSources drops;
    private LootInjection loot;
    // Carried through the GUI round-trip unchanged (the creator can't edit stats yet), so editing an
    // item never wipes its stats: declaration.
    private final Map<String, String> stats;

    public ItemDraft(String id, Material material, NamespacedKey itemModel, Integer customModelData,
                     String name, List<String> lore, List<RecipeSpec> recipes,
                     List<AbilityDraft> abilities, Integer charges, Integer maxCharges,
                     DepletionPolicy onDepletion, boolean durabilityBar, DropSources drops,
                     LootInjection loot, Map<String, String> stats) {
        this.id = id;
        this.material = material;
        this.itemModel = itemModel;
        this.customModelData = customModelData;
        this.name = name;
        this.lore = lore;
        this.recipes = recipes;
        this.abilities = abilities;
        this.charges = charges;
        this.maxCharges = maxCharges;
        this.onDepletion = onDepletion;
        this.durabilityBar = durabilityBar;
        this.drops = drops;
        this.loot = loot;
        this.stats = stats == null ? new LinkedHashMap<>() : new LinkedHashMap<>(stats);
    }

    public static ItemDraft hydrate(CustomItem item) {
        List<AbilityDraft> abilities = new ArrayList<>();
        for (Ability a : item.abilities()) {
            abilities.add(AbilityDraft.hydrate(a));
        }
        return new ItemDraft(
                item.id(),
                item.material(),
                item.itemModel(),
                item.customModelData(),
                item.name(),
                new ArrayList<>(item.lore()),
                new ArrayList<>(item.recipes()),
                abilities,
                item.charges(),
                item.maxCharges(),
                item.onDepletion(),
                item.durabilityBar(),
                item.drops(),
                item.loot(),
                item.stats());
    }

    public CustomItem toCustomItem() {
        List<Ability> lowered = new ArrayList<>();
        for (AbilityDraft a : abilities) {
            lowered.add(a.toAbility());
        }
        return new CustomItem(
                id,
                material,
                itemModel,
                customModelData,
                name,
                new ArrayList<>(lore),
                new ArrayList<>(recipes),
                lowered,
                charges,
                maxCharges,
                onDepletion,
                durabilityBar,
                drops,
                loot,
                stats);
    }

    public String id() {
        return id;
    }

    public Material material() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public NamespacedKey itemModel() {
        return itemModel;
    }

    public void setItemModel(NamespacedKey itemModel) {
        this.itemModel = itemModel;
    }

    public Integer customModelData() {
        return customModelData;
    }

    public void setCustomModelData(Integer customModelData) {
        this.customModelData = customModelData;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> lore() {
        return lore;
    }

    public List<RecipeSpec> recipes() {
        return recipes;
    }

    /** First recipe, or null. Convenience for the single-recipe legacy editor (O4 adds a full list screen). */
    public RecipeSpec recipe() {
        return recipes.isEmpty() ? null : recipes.get(0);
    }

    /** Replaces the recipe list with the given single recipe (or clears it when null). */
    public void setRecipe(RecipeSpec recipe) {
        recipes.clear();
        if (recipe != null) recipes.add(recipe);
    }

    public List<AbilityDraft> abilities() {
        return abilities;
    }

    public Integer charges() {
        return charges;
    }

    public void setCharges(Integer charges) {
        this.charges = charges;
    }

    public Integer maxCharges() {
        return maxCharges;
    }

    public void setMaxCharges(Integer maxCharges) {
        this.maxCharges = maxCharges;
    }

    public DepletionPolicy onDepletion() {
        return onDepletion;
    }

    public void setOnDepletion(DepletionPolicy onDepletion) {
        this.onDepletion = onDepletion;
    }

    public boolean durabilityBar() {
        return durabilityBar;
    }

    public void setDurabilityBar(boolean durabilityBar) {
        this.durabilityBar = durabilityBar;
    }

    public DropSources drops() {
        return drops;
    }

    public void setDrops(DropSources drops) {
        this.drops = drops;
    }

    public LootInjection loot() {
        return loot;
    }

    public void setLoot(LootInjection loot) {
        this.loot = loot;
    }

    /** Initial stat values, carried through the round-trip unchanged (not GUI-editable yet). */
    public Map<String, String> stats() {
        return stats;
    }
}
