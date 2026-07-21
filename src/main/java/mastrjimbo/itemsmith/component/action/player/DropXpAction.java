package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;

/** Drops an experience orb at one block above the Target's location. **/
public final class DropXpAction implements Action {

    public static final String ID = "drop_xp";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.INT, 10)
                    .label("Xp amount").min(0).desc("Xp dropped."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Drop Xp"; }
    @Override public String description() { return "Drops experience points to the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        int amount = params.getInt("amount", 10);
        Location loc = Targets.location(target);
        if (loc == null || loc.getWorld() == null) return;
        Location spawn = loc.clone().add(0, 1.0, 0);
        spawn.getWorld().spawn(spawn, ExperienceOrb.class, experienceOrb -> {
            experienceOrb.setExperience(amount);
        });
    }
}
