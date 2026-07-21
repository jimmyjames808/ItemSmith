package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Locale;

/** Passes when the block at the chosen location (feet, under, or target) is the given material. */
public final class BlockAtCondition implements Condition {

    public static final String ID = "block_at";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, null)
                    .label("Material").desc("The block material to match."))
            .add(ParamDef.of("where", ParamType.ENUM, "feet")
                    .label("Where").options("feet", "under", "target")
                    .desc("Which block to check: at the caster's feet, under them, or the target."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.CONDITION;
    }

    @Override
    public String displayName() {
        return "Block At";
    }

    @Override
    public String description() {
        return "True when the block at the chosen location matches a material.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Material m = params.getMaterial("material");
        if (m == null) return false;
        Block block;
        switch (params.getString("where", "feet").toLowerCase(Locale.ROOT)) {
            case "feet":
                block = ctx.player().getLocation().getBlock();
                break;
            case "under":
                block = ctx.player().getLocation().getBlock().getRelative(BlockFace.DOWN);
                break;
            case "target":
                block = Targets.block(target);
                break;
            default:
                return false;
        }
        if (block == null) return false;
        return block.getType() == m;
    }
}
