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
import org.bukkit.block.Block;
import org.bukkit.block.data.Openable;

/** Opens or closes the target block if it's an openable (door, trapdoor, fence gate). */
public final class OpenDoorAction implements Action {

    public static final String ID = "open_door";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("open", ParamType.BOOLEAN, true)
                    .label("Open").desc("True to open the target, false to close it."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Open Door"; }
    @Override public String description() { return "Opens or closes the target block if it's a door, trapdoor, or gate."; }
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
        if (block.getBlockData() instanceof Openable o) {
            o.setOpen(params.getBool("open", true));
            block.setBlockData(o);
        }
    }
}
