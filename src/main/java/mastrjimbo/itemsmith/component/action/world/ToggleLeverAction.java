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
import org.bukkit.block.data.Powerable;

/** Flips the powered state of the target block if it's a powerable (lever, pressure plate). */
public final class ToggleLeverAction implements Action {

    public static final String ID = "toggle_lever";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Toggle Lever"; }
    @Override public String description() { return "Flips the target block's powered state (e.g. a lever)."; }
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
        if (block.getBlockData() instanceof Powerable p) {
            p.setPowered(!p.isPowered());
            block.setBlockData(p);
        }
    }
}
