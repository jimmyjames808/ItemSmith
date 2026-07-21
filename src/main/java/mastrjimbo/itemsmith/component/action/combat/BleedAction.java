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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/** Applies a bleed damage-over-time: a small hit every interval for a number of ticks (self-scheduling). */
public final class BleedAction implements Action {

    public static final String ID = "bleed";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("damage", ParamType.DOUBLE, 1.0)
                    .label("Damage per tick").min(0).desc("Damage dealt each bleed tick."))
            .add(ParamDef.of("interval", ParamType.INT, 20)
                    .label("Interval (ticks)").min(1).desc("Ticks between bleed hits."))
            .add(ParamDef.of("count", ParamType.INT, 3)
                    .label("Count").min(1).desc("How many bleed hits total."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Bleed"; }
    @Override public String description() { return "Damage over time — a hit every interval."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        double damage = params.getDouble("damage", 1.0);
        int interval = Math.max(1, params.getInt("interval", 20));
        int count = Math.max(1, params.getInt("count", 3));
        Player source = ctx.player();
        new BukkitRunnable() {
            int remaining = count;

            @Override
            public void run() {
                if (remaining-- <= 0 || living.isDead() || !living.isValid()) {
                    cancel();
                    return;
                }
                living.damage(damage, source);
            }
        }.runTaskTimer(ctx.plugin(), interval, interval);
    }
}
