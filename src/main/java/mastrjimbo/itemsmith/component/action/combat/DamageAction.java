package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.LivingEntity;

/**
 * Deals flat damage to the target living entity. When {@code from_caster} is true the hit is
 * attributed to the player, so vanilla knockback, aggro and death credit behave as if the player
 * struck it. No-op for non-living targets.
 */
public final class DamageAction implements Action {

    public static final String ID = "damage";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 4.0)
                    .label("Damage").min(0).desc("Damage dealt (2.0 = 1 heart)."))
            .add(ParamDef.of("from_caster", ParamType.BOOLEAN, true)
                    .label("Attribute to caster").desc("Credit the player as the attacker (knockback/aggro/kill credit)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Damage"; }
    @Override public String description() { return "Deals flat damage to the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        double amount = params.getDouble("amount", 4.0);
        if (amount <= 0) return;
        if (params.getBool("from_caster", true) && ctx.player() != null) {
            living.damage(amount, ctx.player());
        } else {
            living.damage(amount);
        }
    }
}
