package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/** Teleports the caster to the block they are looking at, within range. No-op if nothing is in range. */
public final class TeleportCursorAction implements Action {

    public static final String ID = "teleport_cursor";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("range", ParamType.INT, 30)
                    .label("Range").min(1).desc("How far to trace the caster's line of sight."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Teleport To Cursor"; }
    @Override public String description() { return "Teleports the caster to the block they're looking at."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        int range = params.getInt("range", 30);
        Block block = player.getTargetBlockExact(range);
        if (block == null) return;
        player.teleport(block.getLocation().add(0.5, 1, 0.5));
    }
}
