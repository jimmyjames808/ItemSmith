package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/** Swaps the caster's and the target entity's locations. No-op for non-entity targets. */
public final class SwapPositionsAction implements Action {

    public static final String ID = "swap_positions";

    private static final ParamSchema SCHEMA = ParamSchema.EMPTY;

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Swap Positions"; }
    @Override public String description() { return "Swaps the caster and the target entity's locations."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        Entity other = Targets.entity(target);
        if (other == null) return;
        Location playerLoc = player.getLocation().clone();
        Location otherLoc = other.getLocation().clone();
        player.teleport(otherLoc);
        other.teleport(playerLoc);
    }
}
