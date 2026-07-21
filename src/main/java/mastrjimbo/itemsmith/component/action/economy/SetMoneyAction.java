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

/** Sets the caster's Vault balance to a fixed amount. No-op without economy. */
public final class SetMoneyAction implements Action {

    public static final String ID = "set_money";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 0.0)
                    .label("Amount").min(0).desc("Balance to set the caster to."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ECONOMY; }
    @Override public String displayName() { return "Set Money"; }
    @Override public String description() { return "Sets the caster's Vault balance to a fixed amount."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return;
        VaultHook vault = plugin.vault();
        if (!vault.available()) return;
        vault.set(ctx.player(), params.getDouble("amount", 0.0));
    }
}
