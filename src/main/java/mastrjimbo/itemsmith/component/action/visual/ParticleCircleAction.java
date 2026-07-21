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

/** Fills a horizontal disc around the target's location with several concentric rings of particles. */
public final class ParticleCircleAction implements Action {

    public static final String ID = "particle_circle";

    /** Rings are spaced this far apart, from r=0.3 out to the configured radius. */
    private static final double RING_STEP = 0.3;

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("particle", ParamType.PARTICLE, "flame")
                    .label("Particle").desc("Which particle to spawn."))
            .add(ParamDef.of("head", ParamType.HEAD, "")
                    .label("Head (optional)").desc("Show a mob/player head instead of the particle (e.g. zombie_head, player_head). Blank = use the particle."))
            .add(ParamDef.of("head_owner", ParamType.STRING, "")
                    .label("Player head owner").desc("If head is player_head, the player name whose skin to use."))
            .add(ParamDef.of("radius", ParamType.DOUBLE, 2.0)
                    .label("Radius").min(0).desc("Disc radius."))
            .add(ParamDef.of("points", ParamType.INT, 30)
                    .label("Points").min(0).desc("How many particles form each ring."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Particle Circle"; }
    @Override public String description() { return "Fills a horizontal disc of particles around the target."; }
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
        double radius = params.getDouble("radius", 2.0);
        int points = params.getInt("points", 30);
        if (points <= 0 || radius <= 0) return;
        int rings = Math.max(1, (int) Math.ceil(radius / RING_STEP));
        for (int ring = 0; ring < rings; ring++) {
            double r = rings == 1 ? radius : 0.3 + (radius - 0.3) * ring / (rings - 1);
            for (int i = 0; i < points; i++) {
                double angle = 2 * Math.PI * i / points;
                Location pt = l.clone().add(r * Math.cos(angle), 0, r * Math.sin(angle));
                Visuals.emit(ctx.plugin(), pt, p, 1, 0, 0, 0, 0, head, owner);
            }
        }
    }
}
