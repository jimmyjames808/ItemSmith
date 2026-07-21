package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.List;

/** The block directly beneath the target — or beneath the caster if there is no target. */
public final class BlockBelowTargetTargeter implements Targeter {

    public static final String ID = "block_below_target";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.TARGETER;
    }

    @Override
    public String displayName() {
        return "Block Below Target";
    }

    @Override
    public String description() {
        return "The block directly beneath the target, or the caster if none.";
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        Location l = Targets.location(ctx.eventTarget());
        if (l == null) l = ctx.player().getLocation();
        Block b = l.getBlock().getRelative(BlockFace.DOWN);
        return List.of(b);
    }
}
