package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.gate.Protect;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.World;

/** Within a cube radius of the target block, replaces every block of one material with another. */
public final class ReplaceNearBlocksAction implements Action {

    public static final String ID = "replace_near_blocks";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("from", ParamType.MATERIAL, "STONE")
                    .label("From material").desc("The material to search for."))
            .add(ParamDef.of("to", ParamType.MATERIAL, "AIR")
                    .label("To material").desc("The material to replace it with."))
            .add(ParamDef.of("radius", ParamType.INT, 3)
                    .label("Radius").min(0).desc("Cube half-size around the target block to scan."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Replace Nearby Blocks"; }
    @Override public String description() { return "Replaces every block of one type with another within a cube around the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Block block = Targets.block(target);
        if (block == null) {
            Location l = Targets.location(target);
            if (l != null) block = l.getBlock();
        }
        if (block == null) return;
        Material from = params.getMaterial("from");
        Material to = params.getMaterial("to");
        if (from == null || to == null) return;
        int radius = params.getInt("radius", 3);
        World world = block.getWorld();
        int bx = block.getX();
        int by = block.getY();
        int bz = block.getZ();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block b = world.getBlockAt(bx + x, by + y, bz + z);
                    if (b.getType() == from) {
                        if (!Protect.mayEdit(ctx, b.getLocation())) continue;
                        b.setType(to);
                    }
                }
            }
        }
    }
}
