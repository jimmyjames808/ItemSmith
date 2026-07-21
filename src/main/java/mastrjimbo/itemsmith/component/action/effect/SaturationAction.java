package mastrjimbo.itemsmith.component.action.effect;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Player;

/**
 * Adds saturation to the target player. Falls back to the caster when the
 * target isn't a player. No-op if neither resolves.
 */
public final class SaturationAction implements Action {

    public static final String ID = "saturation";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 5.0)
                    .label("Amount").min(0).desc("Saturation points to add."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.EFFECTS; }
    @Override public String displayName() { return "Saturation"; }
    @Override public String description() { return "Adds saturation to the target (or the caster)."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player p = Targets.player(target);
        if (p == null) p = ctx.player();
        if (p == null) return;
        double amount = params.getDouble("amount", 5.0);
        p.setSaturation((float) (p.getSaturation() + amount));
    }
}
