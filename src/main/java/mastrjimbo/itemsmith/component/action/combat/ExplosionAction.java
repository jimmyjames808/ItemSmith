package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.gate.Protect;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;

/** Creates an explosion at the target's location, optionally setting fire and/or breaking blocks. */
public final class ExplosionAction implements Action {

    public static final String ID = "explosion";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("power", ParamType.DOUBLE, 2.0)
                    .label("Power").min(0).desc("Explosion power (TNT = 4.0)."))
            .add(ParamDef.of("fire", ParamType.BOOLEAN, false)
                    .label("Set fire").desc("Whether the blast leaves fires."))
            .add(ParamDef.of("break_blocks", ParamType.BOOLEAN, false)
                    .label("Break blocks").desc("Whether the blast destroys terrain."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Explosion"; }
    @Override public String description() { return "Creates an explosion at the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location loc = Targets.location(target);
        if (loc == null || loc.getWorld() == null) return;
        double power = params.getDouble("power", 2.0);
        boolean fire = params.getBool("fire", false);
        boolean breakBlocks = params.getBool("break_blocks", false);
        // Respect land claims (opt-in): if the caster can't build at the blast centre, keep the
        // damage/knockback but never break blocks or leave fires. Coarse (centre-checked) — a blast
        // straddling a claim edge still relies on the protection plugin's own explosion handling.
        if ((breakBlocks || fire) && !Protect.mayEdit(ctx, loc)) {
            breakBlocks = false;
            fire = false;
        }
        loc.getWorld().createExplosion(loc, (float) power, fire, breakBlocks);
    }
}
