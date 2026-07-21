package mastrjimbo.itemsmith.component.action.effect;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.LivingEntity;

/**
 * Restores remaining air to the target living entity, capped at its maximum
 * air. No-op for non-living targets.
 */
public final class OxygenAction implements Action {

    public static final String ID = "oxygen";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("seconds", ParamType.INT, 15)
                    .label("Seconds").min(0).desc("Seconds of air to restore."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.EFFECTS; }
    @Override public String displayName() { return "Oxygen"; }
    @Override public String description() { return "Restores air to the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        int seconds = params.getInt("seconds", 15);
        long air = (long) living.getRemainingAir() + (long) seconds * 20L;
        living.setRemainingAir((int) Math.min(living.getMaximumAir(), air));
    }
}
