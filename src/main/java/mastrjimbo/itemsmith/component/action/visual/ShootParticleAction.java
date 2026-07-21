package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Visuals;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

/** Fires a stream of particles out from the caster's eyes in their look direction. */
public final class ShootParticleAction implements Action {

    public static final String ID = "shoot_particle";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("particle", ParamType.PARTICLE, "flame")
                    .label("Particle").desc("Which particle to spawn."))
            .add(ParamDef.of("head", ParamType.HEAD, "")
                    .label("Head (optional)").desc("Show a mob/player head instead of the particle (e.g. zombie_head, player_head). Blank = use the particle."))
            .add(ParamDef.of("head_owner", ParamType.STRING, "")
                    .label("Player head owner").desc("If head is player_head, the player name whose skin to use."))
            .add(ParamDef.of("distance", ParamType.DOUBLE, 8.0)
                    .label("Distance").min(0).desc("How far the stream travels."))
            .add(ParamDef.of("points", ParamType.INT, 20)
                    .label("Points").min(0).desc("How many particles make up the stream."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Shoot Particle"; }
    @Override public String description() { return "Fires a stream of particles in the caster's look direction."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Particle p = params.getParticle("particle");
        Material head = params.getMaterial("head");
        String owner = params.getString("head_owner", "");
        if (p == null && !Visuals.isHead(head)) return;
        World w = ctx.player().getWorld();
        if (w == null) return;
        double distance = params.getDouble("distance", 8.0);
        int points = params.getInt("points", 20);
        if (points <= 0) return;
        Location cur = ctx.player().getEyeLocation();
        Vector dir = cur.getDirection();
        Vector step = dir.multiply(distance / points);
        Location pt = cur.clone();
        for (int i = 0; i < points; i++) {
            pt.add(step);
            Visuals.emit(ctx.plugin(), pt, p, 1, 0, 0, 0, 0, head, owner);
        }
    }
}
