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

/** Traces a horizontal ring of particles around the target's location. */
public final class ParticleRingAction implements Action {

    public static final String ID = "particle_ring";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("particle", ParamType.PARTICLE, "flame")
                    .label("Particle").desc("Which particle to spawn."))
            .add(ParamDef.of("head", ParamType.HEAD, "")
                    .label("Head (optional)").desc("Show a mob/player head instead of the particle (e.g. zombie_head, player_head). Blank = use the particle."))
            .add(ParamDef.of("head_owner", ParamType.STRING, "")
                    .label("Player head owner").desc("If head is player_head, the player name whose skin to use."))
            .add(ParamDef.of("radius", ParamType.DOUBLE, 1.5)
                    .label("Radius").min(0).desc("Ring radius."))
            .add(ParamDef.of("points", ParamType.INT, 24)
                    .label("Points").min(0).desc("How many particles form the ring."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Particle Ring"; }
    @Override public String description() { return "Traces a horizontal ring of particles around the target."; }
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
        double radius = params.getDouble("radius", 1.5);
        int points = params.getInt("points", 24);
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            Location pt = l.clone().add(radius * Math.cos(angle), 0, radius * Math.sin(angle));
            Visuals.emit(ctx.plugin(), pt, p, 1, 0, 0, 0, 0, head, owner);
        }
    }
}
