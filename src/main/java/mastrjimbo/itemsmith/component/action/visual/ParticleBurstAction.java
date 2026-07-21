package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import mastrjimbo.itemsmith.util.Visuals;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;

/** Spawns a dense burst of particles at the target's location — a bigger, showier {@link ParticleAction}. */
public final class ParticleBurstAction implements Action {

    public static final String ID = "particle_burst";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("particle", ParamType.PARTICLE, "explosion")
                    .label("Particle").desc("Which particle to spawn."))
            .add(ParamDef.of("head", ParamType.HEAD, "")
                    .label("Head (optional)").desc("Show a mob/player head instead of the particle (e.g. zombie_head, player_head). Blank = use the particle."))
            .add(ParamDef.of("head_owner", ParamType.STRING, "")
                    .label("Player head owner").desc("If head is player_head, the player name whose skin to use."))
            .add(ParamDef.of("count", ParamType.INT, 30)
                    .label("Count").min(0).desc("How many particles to spawn."))
            .add(ParamDef.of("spread", ParamType.DOUBLE, 0.6)
                    .label("Spread").min(0).desc("Random offset radius on each axis."))
            .add(ParamDef.of("speed", ParamType.DOUBLE, 0.05)
                    .label("Speed").min(0).desc("Particle extra/speed value."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Particle Burst"; }
    @Override public String description() { return "Spawns a dense burst of particles at the target's location."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Particle p = params.getParticle("particle");
        Material head = params.getMaterial("head");
        String owner = params.getString("head_owner", "");
        if (p == null && !Visuals.isHead(head)) return;
        Location l = Targets.location(target);
        if (l == null) l = ctx.player().getLocation();
        World w = l.getWorld();
        if (w == null) return;
        int count = params.getInt("count", 30);
        double spread = params.getDouble("spread", 0.6);
        double speed = params.getDouble("speed", 0.05);
        Visuals.emit(ctx.plugin(), l, p, count, spread, spread, spread, speed, head, owner);
    }
}
