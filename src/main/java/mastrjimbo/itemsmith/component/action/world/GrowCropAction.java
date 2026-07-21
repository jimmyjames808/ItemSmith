package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.gate.Protect;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;

/** Instantly matures the target crop/plant to its final growth stage. */
public final class GrowCropAction implements Action {

    public static final String ID = "grow_crop";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Grow Crop"; }
    @Override public String description() { return "Instantly grows the target crop to full maturity."; }
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
        if (block.getBlockData() instanceof Ageable age) {
            age.setAge(age.getMaximumAge());
            block.setBlockData(age);
        }
    }
}
