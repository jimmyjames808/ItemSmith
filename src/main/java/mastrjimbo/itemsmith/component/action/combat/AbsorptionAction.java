package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Grants golden-apple-style absorption: yellow, non-regenerating shield hearts, via the vanilla
 * Absorption effect (the exact mechanic golden apples use). {@code amount} rounds up to whole effect
 * levels — each level is 4 points = 2 yellow hearts.
 */
public final class AbsorptionAction implements Action {

    public static final String ID = "absorption";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 4.0)
                    .label("Absorption").min(0)
                    .desc("Absorption points (2.0 = 1 yellow heart); rounds up to the nearest level of 2 hearts."))
            .add(ParamDef.of("duration", ParamType.INT, 120)
                    .label("Duration (seconds)").min(1)
                    .desc("How long the absorption hearts last."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Absorption"; }
    @Override public String description() { return "Grants golden-apple-style absorption hearts."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        LivingEntity living = Targets.living(target);
        if (living == null) return;
        double amount = params.getDouble("amount", 4.0);
        if (amount <= 0) return;
        int amplifier = Math.max(0, (int) Math.ceil(amount / 4.0) - 1);
        int durationTicks = Math.max(1, params.getInt("duration", 120)) * 20;
        living.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, durationTicks, amplifier));
    }
}
