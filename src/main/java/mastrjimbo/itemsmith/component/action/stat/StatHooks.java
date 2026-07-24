package mastrjimbo.itemsmith.component.action.stat;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import org.bukkit.inventory.ItemStack;

/**
 * Shared plumbing for the stat-mutating actions. Every action that changes a stat runs its mutation
 * through {@link #mutate}, which captures the numeric value on both sides and fires the
 * {@code stat_reached} threshold hook on a rising crossing. Centralizing it here means a new
 * stat-mutating action can't silently skip the hook — the bug that appeared when {@code multiply_stat}
 * / {@code reset_stat} were added alongside a hook only wired into {@code set_stat} / {@code add_stat}.
 */
final class StatHooks {

    private StatHooks() {
    }

    /**
     * Applies {@code change} to the stat, then fires {@code stat_reached} if the value rose across a
     * watched threshold. Non-numeric (text) stats read as 0 on both sides, so they never cross.
     */
    static void mutate(AbilityContext ctx, ItemStack stack, String name, Runnable change) {
        double before = ctx.registry().getStatNumber(stack, name, 0);
        change.run();
        double after = ctx.registry().getStatNumber(stack, name, 0);
        if (ctx.plugin() instanceof ItemSmith smith) {
            smith.engine().fireStatReached(ctx.player(), stack, name, before, after);
        }
    }
}
