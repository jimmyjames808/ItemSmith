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

/** Clears every water or lava block within a cube radius of the target (or caster) location. */
public final class DrainLiquidAction implements Action {

    public static final String ID = "drain_liquid";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.INT, 3)
                    .label("Radius").min(0).desc("Cube radius (in blocks) to drain liquid within."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Drain Liquid"; }
    @Override public String description() { return "Removes all water and lava within a cube radius of the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location center = Targets.location(target);
        if (center == null) center = ctx.player().getLocation();
        World w = center.getWorld();
        if (w == null) return;
        int radius = params.getInt("radius", 3);
        int cx = center.getBlockX();
        int cy = center.getBlockY();
        int cz = center.getBlockZ();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Block block = w.getBlockAt(cx + x, cy + y, cz + z);
                    Material type = block.getType();
                    if (type == Material.WATER || type == Material.LAVA) {
                        if (!Protect.mayEdit(ctx, block.getLocation())) continue;
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }
}
