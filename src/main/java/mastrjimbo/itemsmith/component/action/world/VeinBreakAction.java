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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/** Flood-fills from the target block, breaking connected blocks of the same type (vein mining). */
public final class VeinBreakAction implements Action {

    public static final String ID = "vein_break";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("limit", ParamType.INT, 32)
                    .label("Block limit").min(1).desc("Maximum number of connected blocks to break."))
            .build();

    private static final BlockFace[] FACES = {
            BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST
    };

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Vein Break"; }
    @Override public String description() { return "Breaks connected blocks of the same type, spreading out from the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Block block = Targets.block(target);
        if (block == null) {
            Location l = Targets.location(target);
            if (l != null) block = l.getBlock();
        }
        if (block == null) return;
        Material type = block.getType();
        if (type == Material.AIR) return;
        int limit = params.getInt("limit", 32);
        World world = block.getWorld();

        Set<Block> visited = new HashSet<>();
        Deque<Block> queue = new ArrayDeque<>();
        queue.add(block);
        visited.add(block);
        int broken = 0;
        while (!queue.isEmpty() && broken < limit) {
            Block current = queue.poll();
            if (current.getType() != type) continue;
            if (!Protect.mayEdit(ctx, current.getLocation())) continue;
            current.breakNaturally();
            broken++;
            for (BlockFace face : FACES) {
                Block next = current.getRelative(face);
                if (visited.contains(next)) continue;
                visited.add(next);
                if (next.getType() == type) {
                    queue.add(next);
                }
            }
        }
    }
}
