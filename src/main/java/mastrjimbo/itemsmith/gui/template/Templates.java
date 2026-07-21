package mastrjimbo.itemsmith.gui.template;

import mastrjimbo.itemsmith.drops.DropSources;
import mastrjimbo.itemsmith.loot.LootInjection;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.gate.DepletionPolicy;
import mastrjimbo.itemsmith.gate.Gate;
import mastrjimbo.itemsmith.gui.draft.AbilityDraft;
import mastrjimbo.itemsmith.gui.draft.ConfiguredDraft;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.gui.draft.ParamBag;
import mastrjimbo.itemsmith.registry.Registries;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Starting points for a new item. Each template seeds an {@link ItemDraft} with a sensible base
 * material and (except Blank) one starter ability wired to a fitting activator + the {@code target}
 * targeter, so the user drops straight into filling in the effects rather than a blank slate.
 */
public final class Templates {

    /** One selectable template: key, icon, label, and a one-line description. */
    public record Template(String key, Material icon, String label, String description) {
    }

    public static final List<Template> ALL = List.of(
            new Template("weapon", Material.IRON_SWORD, "Weapon", "melee item — fires when you hit an entity"),
            new Template("tool", Material.IRON_PICKAXE, "Tool", "fires when you break a block"),
            new Template("armor", Material.IRON_CHESTPLATE, "Armor", "fires when equipped"),
            new Template("consumable", Material.APPLE, "Consumable", "fires when eaten"),
            new Template("wearable", Material.CARVED_PUMPKIN, "Wearable", "cosmetic — fires when worn"),
            new Template("blank", Material.PAPER, "Blank", "empty item, no abilities"));

    private Templates() {
    }

    public static ItemDraft create(Registries registries, String templateKey, String id) {
        return switch (templateKey) {
            case "weapon" -> item(id, Material.IRON_SWORD, starter(registries, "player_hit_entity"));
            case "tool" -> item(id, Material.IRON_PICKAXE, starter(registries, "block_break"));
            case "armor" -> item(id, Material.IRON_CHESTPLATE, starter(registries, "equip"));
            case "consumable" -> item(id, Material.APPLE, starter(registries, "item_consume"));
            case "wearable" -> item(id, Material.CARVED_PUMPKIN, starter(registries, "equip"));
            default -> item(id, Material.PAPER); // blank
        };
    }

    private static ItemDraft item(String id, Material material, AbilityDraft... abilities) {
        List<AbilityDraft> list = new ArrayList<>(List.of(abilities));
        return new ItemDraft(id, material, null, null, "", new ArrayList<>(), new ArrayList<>(), list,
                null, null, DepletionPolicy.CONSUME, false, DropSources.NONE, LootInjection.NONE);
    }

    /** A fresh ability on the given activator, targeting the trigger's entity, with no actions yet. */
    private static AbilityDraft starter(Registries registries, String activatorId) {
        Targeter target = registries.targeter("target");
        return new AbilityDraft(activatorId, new ParamBag(), new ArrayList<>(),
                new ConfiguredDraft<>(target, new ParamBag()), new ArrayList<>(), Gate.NONE, 0);
    }
}
