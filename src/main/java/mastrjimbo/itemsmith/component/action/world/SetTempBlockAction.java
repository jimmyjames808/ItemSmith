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
import mastrjimbo.itemsmith.util.TempTasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/** Temporarily changes the target block to a material, then reverts it after a delay. */
public final class SetTempBlockAction implements Action {

    public static final String ID = "set_temp_block";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, "STONE")
                    .label("Material").desc("The temporary block type to place."))
            .add(ParamDef.of("ticks", ParamType.INT, 100)
                    .label("Duration (ticks)").min(1).desc("How long before the block reverts."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Set Temporary Block"; }
    @Override public String description() { return "Changes the target block to a material, then reverts it after a delay."; }
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
        int ticks = params.getInt("ticks", 100);
        Material old = block.getType();
        Block finalBlock = block;
        block.setType(material);
        TempTasks.later(ctx.plugin(), ticks, () -> finalBlock.setType(old));
    }
}
