package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.gate.Protect;
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
import org.bukkit.entity.Player;

/** Strikes lightning at the target's location — real, damaging by default, or a cosmetic-only flash. */
public final class StrikeLightningAction implements Action {

    public static final String ID = "strike_lightning";

    /** Vanilla lightning deals 5 damage; we reuse it for the caster-safe manual path. */
    private static final double LIGHTNING_DAMAGE = 5.0;
    private static final double LIGHTNING_RADIUS = 3.0;

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("damage", ParamType.BOOLEAN, true)
                    .label("Deals damage").desc("If false, spawns a cosmetic flash that hurts nothing."))
            .add(ParamDef.of("damage_caster", ParamType.BOOLEAN, true)
                    .label("Damages the caster").desc("If false, the player who cast it is spared while everyone else is still hit."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Strike Lightning"; }
    @Override public String description() { return "Calls a lightning bolt down on the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location loc = Targets.location(target);
        if (loc == null || loc.getWorld() == null) return;

        // A real bolt can start fires. If the ability respects claims and the caster can't build here,
        // downgrade to a cosmetic flash (no fire, no damage) so it can't grief protected land.
        boolean deal = params.getBool("damage", true);
        if (!deal || !Protect.mayEdit(ctx, loc)) {
            loc.getWorld().strikeLightningEffect(loc);
            return;
        }

        if (params.getBool("damage_caster", true)) {
            loc.getWorld().strikeLightning(loc); // real bolt — hits everyone nearby, including the caster
            return;
        }

        // Caster-safe path: cosmetic bolt for the visual, then apply lightning-strength damage to every
        // nearby living entity EXCEPT the caster (vanilla lightning gives no way to exclude one entity).
        Player caster = ctx.player();
        loc.getWorld().strikeLightningEffect(loc);
        for (Entity e : loc.getWorld().getNearbyEntities(loc, LIGHTNING_RADIUS, LIGHTNING_RADIUS + 1, LIGHTNING_RADIUS)) {
            if (e instanceof LivingEntity le && !e.equals(caster)) {
                le.setFireTicks(Math.max(le.getFireTicks(), 40));
                ActionDamage.deal(le, LIGHTNING_DAMAGE, caster);
            }
        }
    }
}
