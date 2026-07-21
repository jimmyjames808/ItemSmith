package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import mastrjimbo.itemsmith.util.TempTasks;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

/**
 * Deals damage to the target living entity but negates the resulting knockback: the
 * velocity from just before the hit is restored one tick later. No-op for non-living targets.
 */
public final class DamageNoKnockbackAction implements Action {

    public static final String ID = "damage_no_knockback";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 4.0)
                    .label("Damage").min(0).desc("Damage dealt (2.0 = 1 heart)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Damage (No Knockback)"; }
    @Override public String description() { return "Deals damage without applying knockback to the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        double amount = params.getDouble("amount", 4.0);
        if (amount <= 0) return;
        Vector v = living.getVelocity();
        living.damage(amount, ctx.player());
        TempTasks.later(ctx.plugin(), 1, () -> {
            if (living.isValid()) living.setVelocity(v);
        });
    }
}
