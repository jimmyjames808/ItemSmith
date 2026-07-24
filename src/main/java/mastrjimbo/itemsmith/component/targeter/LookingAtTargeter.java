package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.List;

/**
 * Whatever the caster is aiming at: the entity in their crosshair if there is one, otherwise the
 * block behind it.
 *
 * <p>Entity-first is the point. {@code looking_at_entity} and {@code looking_at_block} both resolve
 * when you aim at a mob standing in front of a wall, so pairing them on one activator fires both
 * abilities at once. This picks exactly one, which lets a single ability behave differently
 * depending on what's under the crosshair.
 */
public final class LookingAtTargeter implements Targeter {

    public static final String ID = "looking_at";

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
        return "Looking At (entity or block)";
    }

    @Override
    public String description() {
        return "The entity the caster is aiming at, or the block if there is no entity.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        int range = (int) params.getDouble("range", 30);
        Entity e = ctx.player().getTargetEntity(range);
        if (e != null) return List.of(e);
        Block b = ctx.player().getTargetBlockExact(range);
        return b == null ? List.of() : List.of(b);
    }
}
