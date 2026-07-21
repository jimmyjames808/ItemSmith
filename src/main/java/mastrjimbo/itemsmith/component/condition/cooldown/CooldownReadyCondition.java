package mastrjimbo.itemsmith.component.condition.cooldown;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.gate.NamedCooldownStore;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/**
 * Passes when the caster's named cooldown has elapsed (side-effect free — arming is done by the
 * {@code set_cooldown} action or an ability's cooldown-group gate). With an optional {@code seconds}
 * &gt; 0, passes when the remaining time is at or below that many seconds ("near-ready" gating).
 */
public final class CooldownReadyCondition implements Condition {

    public static final String ID = "cooldown_ready";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("key", ParamType.STRING, "ability")
                    .label("Key").desc("Name of the cooldown to check."))
            .add(ParamDef.of("seconds", ParamType.DOUBLE, 0.0)
                    .label("Within Seconds").min(0)
                    .desc("If > 0, passes when the cooldown has this many seconds or fewer left.")
                    .markAdvanced())
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.CONDITION; }
    @Override public String displayName() { return "Cooldown Ready"; }
    @Override public String description() { return "True when a named cooldown has elapsed."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return true;
        NamedCooldownStore store = plugin.cooldownGroups();
        String key = params.getString("key", "ability");
        double within = params.getDouble("seconds", 0.0);
        if (within > 0) {
            return store.remainingMillis(ctx.player(), key) <= within * 1000;
        }
        return store.ready(ctx.player(), key);
    }
}
