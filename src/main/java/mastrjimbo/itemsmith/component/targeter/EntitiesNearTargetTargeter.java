package mastrjimbo.itemsmith.component.targeter;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Every living entity within a radius of the <em>trigger's</em> target — the entity or block the
 * event happened to, e.g. where a projectile landed.
 *
 * <p>The existing area targeters ({@code radius}, {@code nearby_entities}, {@code nearby_monsters})
 * all centre on the caster, which is wrong for anything that happens away from the caster: a grenade
 * that lands 20 blocks out should sweep entities around the impact, not around the thrower. This
 * centres on {@link AbilityContext#eventTarget()} instead, falling back to the caster only when the
 * trigger had no target.
 */
public final class EntitiesNearTargetTargeter implements Targeter {

    public static final String ID = "entities_near_target";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("radius", ParamType.DOUBLE, 5.0)
                    .label("Radius").min(0).desc("How far from the trigger point to gather entities."))
            .add(ParamDef.of("max", ParamType.INT, 0)
                    .label("Max").min(0).desc("Cap on how many to return. 0 for no cap."))
            .add(ParamDef.of("include_self", ParamType.BOOLEAN, false)
                    .label("Include self").desc("Whether the caster can be one of the targets."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.TARGETER; }
    @Override public String displayName() { return "Entities Near Target"; }
    @Override public String description() { return "Living entities around the trigger's target (e.g. an impact point)."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public List<Object> resolve(AbilityContext ctx, ParamValues params) {
        Location center = Targets.location(ctx.eventTarget());
        if (center == null) center = ctx.player().getLocation(); // no trigger target — fall back to caster
        if (center.getWorld() == null) return List.of();

        double radius = params.getDouble("radius", 5.0);
        int max = params.getInt("max", 0);
        boolean includeSelf = params.getBool("include_self", false);

        List<Object> out = new ArrayList<>();
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (!(e instanceof LivingEntity)) continue;
            if (!includeSelf && e.equals(ctx.player())) continue;
            out.add(e);
            if (max > 0 && out.size() >= max) break;
        }
        return out;
    }
}
