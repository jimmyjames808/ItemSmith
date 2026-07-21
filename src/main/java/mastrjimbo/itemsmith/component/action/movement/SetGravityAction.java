package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Entity;

/** Enables or disables gravity on the target entity, defaulting to the caster when the target isn't one. */
public final class SetGravityAction implements Action {

    public static final String ID = "set_gravity";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("enabled", ParamType.BOOLEAN, false)
                    .label("Gravity enabled").desc("Whether gravity affects the entity."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "Set Gravity"; }
    @Override public String description() { return "Enables or disables gravity on the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity entity = Targets.entity(target);
        if (entity == null) entity = ctx.player();
        if (entity == null) return;
        boolean enabled = params.getBool("enabled", false);
        entity.setGravity(enabled);
    }
}
