package mastrjimbo.itemsmith.component.action.cooldown;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Arms a named cooldown for the caster; pair with the {@code cooldown_ready} condition to gate on it. */
public final class SetCooldownAction implements Action {

    public static final String ID = "set_cooldown";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("key", ParamType.STRING, "ability")
                    .label("Key").desc("Name of the cooldown (shared across items using the same key)."))
            .add(ParamDef.of("seconds", ParamType.DOUBLE, 5.0)
                    .label("Seconds").min(0).desc("How long the cooldown lasts."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.META; }
    @Override public String displayName() { return "Set Cooldown"; }
    @Override public String description() { return "Starts a named cooldown on the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return;
        plugin.cooldownGroups().trigger(ctx.player(), params.getString("key", "ability"),
                params.getDouble("seconds", 5.0));
    }
}
