package mastrjimbo.itemsmith.component.action;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Adds flat bonus damage to the triggering damage event (2.0 = one heart).
 * Only meaningful under a damage-based activator such as {@code player_hit_entity};
 * it modifies the in-flight {@link EntityDamageByEntityEvent} rather than a target.
 */
public final class AddDamageAction implements Action {

    public static final String ID = "add_damage";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 2.0)
                    .label("Bonus damage").min(0).desc("Extra damage added to the hit (2.0 = 1 heart)."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.COMBAT;
    }

    @Override
    public String displayName() {
        return "Add Damage";
    }

    @Override
    public String description() {
        return "Adds flat bonus damage to the current hit.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        double amount = params.getDouble("amount", 0);
        if (amount <= 0) return;
        if (ctx.event() instanceof EntityDamageByEntityEvent event) {
            event.setDamage(event.getDamage() + amount);
        }
    }
}
