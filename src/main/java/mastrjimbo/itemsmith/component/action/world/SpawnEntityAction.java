package mastrjimbo.itemsmith.component.action.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.Locale;

/** Spawns one or more entities of a given type at the target (or caster) location. */
public final class SpawnEntityAction implements Action {

    public static final String ID = "spawn_entity";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("entity", ParamType.ENTITY_TYPE, "zombie")
                    .label("Entity Type").desc("The entity type to spawn (e.g. zombie, skeleton)."))
            .add(ParamDef.of("count", ParamType.INT, 1)
                    .label("Count").range(1, 20).desc("How many entities to spawn."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.WORLD; }
    @Override public String displayName() { return "Spawn Entity"; }
    @Override public String description() { return "Spawns one or more entities of a chosen type at the target location."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location l = Targets.location(target);
        if (l == null) l = ctx.player().getLocation();
        if (l.getWorld() == null) return;
        String type = params.getString("entity", "zombie");
        int count = params.getInt("count", 1);
        try {
            EntityType entityType = EntityType.valueOf(type.trim().toUpperCase(Locale.ROOT));
            for (int i = 0; i < count; i++) {
                l.getWorld().spawnEntity(l, entityType);
            }
        } catch (IllegalArgumentException ignored) {
            // Unknown entity type name — no-op.
        }
    }
}
