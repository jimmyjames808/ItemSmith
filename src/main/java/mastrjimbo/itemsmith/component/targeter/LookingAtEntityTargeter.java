package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Entity;

import java.util.List;

/** The entity the caster is directly looking at, up to a range. */
public final class LookingAtEntityTargeter implements Targeter {

    public static final String ID = "looking_at_entity";

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
        return "Looking At Entity";
    }

    @Override
    public String description() {
        return "The entity the caster is directly looking at.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        Entity e = ctx.player().getTargetEntity((int) params.getDouble("range", 30));
        return e == null ? List.of() : List.of(e);
    }
}
