package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Best-effort auto-smelt: if the triggering event is a block break with a known
 * raw-to-smelted mapping, suppresses the vanilla drop and drops the smelted item
 * instead. Unmapped blocks are left untouched so vanilla drops normally.
 */
public final class AutoSmeltDropsAction implements Action {

    public static final String ID = "auto_smelt_drops";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Auto-Smelt Drops"; }
    @Override public String description() { return "Drops the smelted result instead of the raw block, for common ores/blocks."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.event() instanceof BlockBreakEvent bbe)) return;
        Material smelted = switch (bbe.getBlock().getType()) {
            case RAW_IRON -> Material.IRON_INGOT;
            case RAW_GOLD -> Material.GOLD_INGOT;
            case RAW_COPPER -> Material.COPPER_INGOT;
            case IRON_ORE, DEEPSLATE_IRON_ORE -> Material.IRON_INGOT;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> Material.GOLD_INGOT;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
            case SAND -> Material.GLASS;
            case COBBLESTONE -> Material.STONE;
            case NETHERRACK -> Material.NETHER_BRICK;
            default -> null;
        };
        if (smelted == null) return;
        bbe.setDropItems(false);
        bbe.getBlock().getWorld().dropItemNaturally(bbe.getBlock().getLocation(), new ItemStack(smelted));
    }
}
