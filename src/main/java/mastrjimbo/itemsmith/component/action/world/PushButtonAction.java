package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.gate.Protect;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import mastrjimbo.itemsmith.util.TempTasks;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Powerable;

/** Powers the target block (e.g. a button) for one second, then automatically releases it. */
public final class PushButtonAction implements Action {

    public static final String ID = "push_button";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Push Button"; }
    @Override public String description() { return "Powers the target block briefly, then releases it (like a button)."; }
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
            p.setPowered(true);
            block.setBlockData(p);
            Block finalBlock = block;
            TempTasks.later(ctx.plugin(), 20, () -> {
                if (finalBlock.getBlockData() instanceof Powerable q) {
                    q.setPowered(false);
                    finalBlock.setBlockData(q);
                }
            });
        }
    }
}
