package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.ActionDamage;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/** Damages every living entity within a radius of the target, skipping the caster. */
public final class DamageNearbyAction implements Action {

    public static final String ID = "damage_nearby";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.DOUBLE, 4.0)
                    .label("Radius").min(0).desc("How far around the target to reach."))
            .add(ParamDef.of("amount", ParamType.DOUBLE, 4.0)
                    .label("Damage").min(0).desc("Damage dealt to each entity."))
            .add(ParamDef.of("hit_caster", ParamType.BOOLEAN, false)
                    .label("Include caster").desc("Whether the caster is also hurt."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Damage Nearby"; }
    @Override public String description() { return "Damages living entities around the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location loc = Targets.location(target);
        if (loc == null || loc.getWorld() == null) return;
        double radius = params.getDouble("radius", 4.0);
        double amount = params.getDouble("amount", 4.0);
        boolean hitCaster = params.getBool("hit_caster", false);
        for (Entity e : loc.getWorld().getNearbyEntities(loc, radius, radius, radius)) {
            if (!(e instanceof LivingEntity living)) continue;
            if (!hitCaster && living.equals(ctx.player())) continue;
            ActionDamage.deal(living, amount, ctx.player());
        }
    }
}
