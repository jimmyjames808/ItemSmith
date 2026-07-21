package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.gate.Protect;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/** Removes the target block silently — no drops, just set to air. */
public final class BreakBlockNoDropAction implements Action {

    public static final String ID = "break_block_no_drop";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Break Block (No Drop)"; }
    @Override public String description() { return "Removes the target block without dropping any items."; }
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
        block.setType(Material.AIR);
    }
}
