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

/** Spawns a falling block entity of a chosen material above the target block. */
public final class SpawnFallingBlockAction implements Action {

    public static final String ID = "spawn_falling_block";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, "SAND")
                    .label("Material").desc("The block type to spawn as a falling block."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Spawn Falling Block"; }
    @Override public String description() { return "Spawns a falling block above the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Block block = Targets.block(target);
        if (block == null) {
            Location l = Targets.location(target);
            if (l != null) block = l.getBlock();
        }
        if (block == null) return;
        if (!Protect.mayEdit(ctx, block.getLocation())) return;
        Material material = params.getMaterial("material");
        if (material == null) return;
        block.getWorld().spawnFallingBlock(block.getLocation().add(0.5, 1, 0.5), material.createBlockData());
    }
}
