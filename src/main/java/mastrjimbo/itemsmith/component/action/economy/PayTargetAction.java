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
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Player;

/** Transfers money from the caster to the resolved target player. No-op without economy. */
public final class PayTargetAction implements Action {

    public static final String ID = "pay_target";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.DOUBLE, 100.0)
                    .label("Amount").min(0).desc("Money to transfer to the target player."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ECONOMY; }
    @Override public String displayName() { return "Pay Target"; }
    @Override public String description() { return "Transfers money from the caster to the target player."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return;
        Player recipient = Targets.player(target);
        if (recipient == null) return;
        VaultHook vault = plugin.vault();
        if (!vault.available()) return;
        double amount = params.getDouble("amount", 100.0);
        if (amount <= 0 || !vault.has(ctx.player(), amount)) return;
        if (vault.withdraw(ctx.player(), amount)) {
            vault.deposit(recipient, amount);
        }
    }
}
