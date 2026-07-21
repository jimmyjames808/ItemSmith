package mastrjimbo.itemsmith.gui;

import mastrjimbo.itemsmith.engine.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/** Cosmectic display icons for the GUI **/
public class Icons {

    private Icons() {}

    /**
     * A cosmetic display icon: the base material wearing the given item-model / custom-model-data,
     * so it previews with its real texture (when the viewer has the pack), but WITHOUT the ItemSmith
     * identity tag — so the {@code InventoryListener} never treats it as a real ItemSmith item and it
     * can't fire abilities on a click. Used for saved items and for the in-editor draft alike.
     */
    @SuppressWarnings("deprecation") // setCustomModelData(Integer) is the legacy-pack fallback
    public static ItemStack display(Material material, NamespacedKey itemModel, Integer customModelData) {
        Material mat = material != null && material.isItem() ? material : Material.PAPER;
        ItemStack stack = new ItemStack(mat);
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            if (itemModel != null) meta.setItemModel(itemModel);
            if (customModelData != null) meta.setCustomModelData(customModelData);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    /** Display icon for a saved {@link CustomItem}. */
    public static ItemStack display(CustomItem def) {
        if (def == null) return new ItemStack(Material.PAPER);
        return display(def.material(), def.itemModel(), def.customModelData());
    }

}
