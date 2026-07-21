package mastrjimbo.itemsmith.gate;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.integration.ProtectionHook;
import mastrjimbo.itemsmith.integration.VaultHook;
import mastrjimbo.itemsmith.util.Text;
import mastrjimbo.itemsmith.util.Xp;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Evaluates an ability's {@link Gate}. The engine calls {@link #check} (pure — verifies
 * permission, region, cooldown group, charges and every use-cost, mutating nothing) and,
 * only on a pass, {@link #commit} (consumes them all, back-to-back in the same tick).
 *
 * <p>Because nothing is consumed until every requirement is known satisfiable, there is no
 * partial consume and no refund path. Costs settle at ability <em>start</em> (consistent with
 * native cooldown and the executor's async convention). Atomicity is per-ability: sibling
 * abilities on the same trigger are independent rules.
 */
public final class GateEvaluator {

    private final VaultHook vault;
    private final ProtectionHook protection;
    private final NamedCooldownStore cooldownGroups;
    private final Logger logger;

    public GateEvaluator(VaultHook vault, ProtectionHook protection,
                         NamedCooldownStore cooldownGroups, Logger logger) {
        this.vault = vault;
        this.protection = protection;
        this.cooldownGroups = cooldownGroups;
        this.logger = logger;
    }

    /** Pure: returns PASS or the first-failing reason. No side effects. */
    public GateCheck check(Gate gate, AbilityContext ctx) {
        if (gate == null || gate.isNoOp()) return GateCheck.PASS;
        Player p = ctx.player();

        // Permission is access control — never bypassed by the bypass perms.
        if (gate.permission() != null && !gate.permission().isEmpty() && !p.hasPermission(gate.permission())) {
            return GateCheck.deny(GateCheck.Reason.PERMISSION, gate.permission());
        }

        if (gate.region() != null && !gate.region().isNoOp() && !bypasses(p, "region")) {
            RegionSpec rs = gate.region();
            if (rs.region() != null && !rs.region().isEmpty() && !protection.isInRegion(p, rs.region())) {
                return GateCheck.deny(GateCheck.Reason.REGION, rs.region());
            }
            if (rs.canBuild() && !protection.canBuild(p, p.getLocation())) {
                return GateCheck.deny(GateCheck.Reason.REGION, "build");
            }
        }

        if (gate.cooldownGroup() != null && !gate.cooldownGroup().isEmpty() && !bypasses(p, "cooldown")) {
            if (!cooldownGroups.ready(p, gate.cooldownGroup())) {
                double secs = cooldownGroups.remainingMillis(p, gate.cooldownGroup()) / 1000.0;
                return GateCheck.deny(GateCheck.Reason.COOLDOWN, String.format(Locale.ROOT, "%.1f", secs));
            }
        }

        if (!bypasses(p, "cost")) {
            ItemStack stack = ctx.itemStack();
            if (gate.chargeCost() > 0 && ctx.registry().charges(stack) < gate.chargeCost()) {
                return GateCheck.deny(GateCheck.Reason.CHARGES, String.valueOf(gate.chargeCost()));
            }
            CostSpec c = gate.cost();
            if (c != null && !c.isNone()) {
                if (c.money() > 0 && vault.available() && !vault.has(p, c.money())) {
                    return GateCheck.deny(GateCheck.Reason.MONEY, String.valueOf(c.money()));
                }
                if (c.xpLevels() > 0 && p.getLevel() < c.xpLevels()) {
                    return GateCheck.deny(GateCheck.Reason.XP, c.xpLevels() + " levels");
                }
                if (c.xpPoints() > 0 && Xp.total(p) < c.xpPoints()) {
                    return GateCheck.deny(GateCheck.Reason.XP, c.xpPoints() + " xp");
                }
                if (c.hunger() > 0 && (p.getFoodLevel() + p.getSaturation()) < c.hunger()) {
                    return GateCheck.deny(GateCheck.Reason.HUNGER, String.valueOf(c.hunger()));
                }
                for (CostSpec.ItemCost ic : c.items()) {
                    if (ic.material() == null) continue;
                    if (!p.getInventory().containsAtLeast(new ItemStack(ic.material()), ic.amount())) {
                        return GateCheck.deny(GateCheck.Reason.ITEMS,
                                ic.amount() + "x " + ic.material().name().toLowerCase(Locale.ROOT));
                    }
                }
            }
        }

        return GateCheck.PASS;
    }

    /** Consumes everything the (already-passing) gate requires. Money first (the only externally-failing op). */
    public void commit(Gate gate, AbilityContext ctx) {
        if (gate == null || gate.isNoOp()) return;
        Player p = ctx.player();

        if (!bypasses(p, "cost")) {
            CostSpec c = gate.cost();
            if (c != null && !c.isNone()) {
                if (c.money() > 0 && vault.available()) vault.withdraw(p, c.money());
                if (c.xpLevels() > 0) p.setLevel(Math.max(0, p.getLevel() - c.xpLevels()));
                if (c.xpPoints() > 0) p.giveExp(-c.xpPoints());
                if (c.hunger() > 0) drainHunger(p, c.hunger());
                for (CostSpec.ItemCost ic : c.items()) {
                    if (ic.material() != null) {
                        p.getInventory().removeItem(new ItemStack(ic.material(), Math.max(1, ic.amount())));
                    }
                }
            }
            if (gate.chargeCost() > 0) {
                boolean depleted = ctx.registry().decrementCharges(ctx.itemStack(), gate.chargeCost());
                if (depleted) onDepleted(ctx);
            }
        }

        if (gate.cooldownGroup() != null && !gate.cooldownGroup().isEmpty() && !bypasses(p, "cooldown")) {
            cooldownGroups.trigger(p, gate.cooldownGroup(), gate.cooldownGroupSeconds());
        }

        // Opt-in: let this ability's block actions respect land claims.
        if (gate.region() != null && gate.region().respectClaims()) {
            ctx.variables().put(Protect.VAR, Boolean.TRUE);
        }
    }

    /** Sends the ability's deny message (if any), substituting the reason tokens. Silent by default. */
    public void notifyDenied(Gate gate, AbilityContext ctx, GateCheck check) {
        String msg = gate.denyMessage();
        if (msg == null || msg.isEmpty()) return;
        String out = msg
                .replace("<reason>", check.reason().name().toLowerCase(Locale.ROOT))
                .replace("<needed>", check.detail())
                .replace("<remaining>", check.detail());
        ctx.player().sendMessage(Text.chat(out));
    }

    private void onDepleted(AbilityContext ctx) {
        CustomItem def = ctx.registry().get(ctx.itemId());
        if (def != null && def.onDepletion() == DepletionPolicy.BREAK) {
            Player p = ctx.player();
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
        }
    }

    private void drainHunger(Player p, double amount) {
        double sat = p.getSaturation();
        if (sat >= amount) {
            p.setSaturation((float) (sat - amount));
            return;
        }
        amount -= sat;
        p.setSaturation(0f);
        p.setFoodLevel(Math.max(0, p.getFoodLevel() - (int) Math.ceil(amount)));
    }

    private boolean bypasses(Player p, String section) {
        return p.hasPermission("itemsmith.bypass.all") || p.hasPermission("itemsmith.bypass." + section);
    }
}
