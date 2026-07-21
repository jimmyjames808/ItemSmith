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
import org.bukkit.World;
import org.bukkit.block.Block;

/** Breaks every block in a cube around the target block, dropping items as if mined. */
public final class AreaBreakAction implements Action {

    public static final String ID = "area_break";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("size", ParamType.INT, 1)
                    .label("Size").min(0).desc("Cube half-size around the target block to break."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Area Break"; }
    @Override public String description() { return "Breaks every block in a cube around the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Block block = Targets.block(target);
        if (block == null) {
            Location l = Targets.location(target);
            if (l != null) block = l.getBlock();
        }
        if (block == null) return;
        int size = params.getInt("size", 1);
        World world = block.getWorld();
        int bx = block.getX();
        int by = block.getY();
        int bz = block.getZ();
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {
                    Block b = world.getBlockAt(bx + x, by + y, bz + z);
                    if (!Protect.mayEdit(ctx, b.getLocation())) continue;
                    b.breakNaturally();
                }
            }
        }
    }
}
