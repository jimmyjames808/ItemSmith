package mastrjimbo.itemsmith.component.condition.economy;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.integration.VaultHook;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes when the caster's Vault balance is strictly below the given amount. Passes without economy. */
public final class BalanceBelowCondition implements Condition {

    public static final String ID = "balance_below";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 100.0)
                    .label("Amount").min(0).desc("Balance must be below this."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.CONDITION; }
    @Override public String displayName() { return "Balance Below"; }
    @Override public String description() { return "True when the caster's balance is below the amount."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return true;
        VaultHook vault = plugin.vault();
        if (!vault.available()) return true;
        return vault.balance(ctx.player()) < params.getDouble("amount", 100.0);
    }
}
