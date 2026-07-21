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

/** Sets the target block to a chosen liquid. */
public final class PlaceLiquidAction implements Action {

    public static final String ID = "place_liquid";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("liquid", ParamType.MATERIAL, "WATER")
                    .label("Liquid").desc("The liquid material to place (e.g. WATER, LAVA)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Place Liquid"; }
    @Override public String description() { return "Places a liquid at the target block."; }
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
        Material liquid = params.getMaterial("liquid");
        if (liquid == null) return;
        block.setType(liquid);
    }
}
