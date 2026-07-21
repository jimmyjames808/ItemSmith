package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.Entity;

/** Passes when the target's entity type matches the configured type. Fail-closed when there is no entity target. */
public final class EntityTypeIsCondition implements Condition {

    public static final String ID = "entity_type_is";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("entity_type", ParamType.ENTITY_TYPE, "zombie")
                    .label("Entity Type").desc("Vanilla entity type the target must be, e.g. ZOMBIE, SKELETON."))
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
        return "Entity Type Is";
    }

    @Override
    public String description() {
        return "True when the target's entity type matches.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Entity e = Targets.entity(target);
        if (e == null) return false;
        return e.getType().name().equalsIgnoreCase(params.getString("entity_type", "ZOMBIE"));
    }
}
