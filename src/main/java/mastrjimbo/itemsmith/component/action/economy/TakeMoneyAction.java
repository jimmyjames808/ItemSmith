package mastrjimbo.itemsmith.component.action.economy;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.integration.VaultHook;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Withdraws Vault money from the caster's balance (down to zero). No-op without economy. */
public final class TakeMoneyAction implements Action {

    public static final String ID = "take_money";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 100.0)
                    .label("Amount").min(0).desc("Money to take from the caster."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ECONOMY; }
    @Override public String displayName() { return "Take Money"; }
    @Override public String description() { return "Withdraws Vault money from the caster's balance."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return;
        VaultHook vault = plugin.vault();
        if (!vault.available()) return;
        double amount = params.getDouble("amount", 100.0);
        double balance = vault.balance(ctx.player());
        vault.withdraw(ctx.player(), Math.min(amount, balance));
    }
}
