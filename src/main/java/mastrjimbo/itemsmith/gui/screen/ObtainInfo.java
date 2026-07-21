package mastrjimbo.itemsmith.gui.screen;

import mastrjimbo.itemsmith.drops.BlockDrop;
import mastrjimbo.itemsmith.drops.DropSources;
import mastrjimbo.itemsmith.drops.MobDrop;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.loot.LootInjection;
import mastrjimbo.itemsmith.loot.LootRule;
import mastrjimbo.itemsmith.recipe.RecipeSpec;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/** Renders human-readable "how to obtain this item" lines for the player catalog (list summary + detail). */
final class ObtainInfo {

    private ObtainInfo() {
    }

    /** A compact one-word-ish tag line for the catalog list ("Craftable · Dropped · Loot"), or "Admin-only". */
    static Component tags(CustomItem def) {
        List<String> tags = new ArrayList<>();
        if (!def.recipes().isEmpty()) tags.add("Craftable");
        if (def.drops() != null && !def.drops().isEmpty()) tags.add("Dropped");
        if (def.loot() != null && !def.loot().isEmpty()) tags.add("Loot");
        return tags.isEmpty()
                ? Text.item("<dark_gray>Admin-only")
                : Text.item("<green>" + String.join(" <dark_gray>·</dark_gray> <green>", tags));
    }

    /** Full detail lines describing every way the item can be obtained. */
    static List<Component> lines(CustomItem def) {
        List<Component> out = new ArrayList<>();
        for (RecipeSpec r : def.recipes()) {
            out.add(Text.item("<gray>◆ " + recipeLabel(r)));
        }
        DropSources drops = def.drops();
        if (drops != null) {
            for (MobDrop md : drops.mobDrops()) out.add(Text.item("<gray>◆ " + mobLabel(md)));
            for (BlockDrop bd : drops.blockDrops()) out.add(Text.item("<gray>◆ " + blockLabel(bd)));
        }
        LootInjection loot = def.loot();
        if (loot != null) {
            for (LootRule lr : loot.rules()) out.add(Text.item("<gray>◆ " + lootLabel(lr)));
        }
        if (out.isEmpty()) {
            out.add(Text.item("<dark_gray>Not obtainable in survival — admin give only"));
        }
        return out;
    }

    private static String recipeLabel(RecipeSpec r) {
        return switch (r) {
            case RecipeSpec.Shaped s -> "Crafting (shaped)";
            case RecipeSpec.Shapeless s -> "Crafting (shapeless)";
            case RecipeSpec.Cooking c -> cookVerb(c.kind()) + " " + mat(c.input());
            case RecipeSpec.Smithing s -> "Smithing: " + mat(s.base()) + " + " + mat(s.addition());
            case RecipeSpec.Stonecutting s -> "Stonecutter: " + mat(s.input());
        };
    }

    private static String cookVerb(RecipeSpec.Cooking.Kind kind) {
        return switch (kind) {
            case FURNACE -> "Smelt";
            case BLASTING -> "Blast";
            case SMOKING -> "Smoke";
            case CAMPFIRE -> "Cook";
        };
    }

    private static String mobLabel(MobDrop md) {
        String who = md.entities().isEmpty() ? "any mob" : joinEntities(md.entities());
        return "Dropped by " + who + " (" + pct(md.chance()) + ")";
    }

    private static String blockLabel(BlockDrop bd) {
        return "Mining " + joinMaterials(bd.blocks()) + " (" + pct(bd.chance()) + ")";
    }

    private static String lootLabel(LootRule lr) {
        return "Loot: " + String.join(", ", lr.tables()) + " (" + pct(lr.chance()) + ")";
    }

    private static String joinEntities(Iterable<EntityType> types) {
        StringJoiner j = new StringJoiner(", ");
        for (EntityType t : types) j.add(t.getKey().getKey());
        return j.toString();
    }

    private static String joinMaterials(Iterable<Material> mats) {
        StringJoiner j = new StringJoiner(", ");
        for (Material m : mats) j.add(m.getKey().getKey());
        return j.toString();
    }

    private static String mat(Material m) {
        return m.getKey().getKey();
    }

    /** Formats a 0-1 chance as a percentage without a trailing {@code .0} (e.g. 0.5 -> "50%"). */
    private static String pct(double chance) {
        double p = chance * 100.0;
        if (p == Math.floor(p)) return (long) p + "%";
        return String.valueOf(Math.round(p * 10.0) / 10.0) + "%";
    }
}
