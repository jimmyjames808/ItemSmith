package mastrjimbo.itemsmith.component.action.movement;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.World;
import org.bukkit.entity.Player;

/** Teleports the caster to the spawn point of a named world. No-op if the world isn't loaded. */
public final class WorldTeleportAction implements Action {

    public static final String ID = "world_teleport";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("world", ParamType.WORLD, "world")
                    .label("World").desc("Name of the world to send the caster to."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.MOVEMENT; }
    @Override public String displayName() { return "World Teleport"; }
    @Override public String description() { return "Teleports the caster to the spawn of a named world."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player player = ctx.player();
        if (player == null) return;
        String name = params.getString("world", "world");
        World world = ctx.plugin().getServer().getWorld(name);
        if (world == null) return;
        player.teleport(world.getSpawnLocation());
    }
}
