package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.block.Block;

import java.util.List;

/** The block the caster is directly looking at, up to a range. */
public final class LookingAtBlockTargeter implements Targeter {

    public static final String ID = "looking_at_block";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("range", ParamType.DOUBLE, 30.0)
                    .label("Range").min(0).desc("How far along the line of sight to look."))
            .build();

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
        return "Looking At Block";
    }

    @Override
    public String description() {
        return "The block the caster is directly looking at.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        Block b = ctx.player().getTargetBlockExact((int) params.getDouble("range", 30));
        return b == null ? List.of() : List.of(b);
    }
}
