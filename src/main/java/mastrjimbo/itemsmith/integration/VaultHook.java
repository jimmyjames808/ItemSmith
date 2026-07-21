package mastrjimbo.itemsmith.integration;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Null-object adapter over the Vault economy service. {@link #detect} resolves the
 * economy provider only when Vault is actually installed; otherwise every method is
 * a graceful no-op and {@link #available()} is false. Callers must treat "economy
 * unavailable" as "feature disabled" — never brick an item on a non-economy server.
 *
 * <p>The {@code Economy.class} literal is touched only inside {@link #detect} after
 * the Vault plugin is confirmed present, so this class links cleanly when Vault is
 * absent (the {@code economy} field simply stays null).
 */
public final class VaultHook {

    private final Economy economy; // null when Vault/economy provider absent

    private VaultHook(Economy economy) {
        this.economy = economy;
    }

    public static VaultHook detect(Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return new VaultHook(null);
        }
        try {
            RegisteredServiceProvider<Economy> rsp =
                    plugin.getServer().getServicesManager().getRegistration(Economy.class);
            return new VaultHook(rsp == null ? null : rsp.getProvider());
        } catch (Throwable t) {
            return new VaultHook(null);
        }
    }

    /** True only when Vault AND an economy provider (e.g. EssentialsX) are present. */
    public boolean available() {
        return economy != null;
    }

    public double balance(OfflinePlayer player) {
        return economy == null ? 0 : economy.getBalance(player);
    }

    public boolean has(OfflinePlayer player, double amount) {
        return economy != null && economy.has(player, amount);
    }

    public boolean withdraw(OfflinePlayer player, double amount) {
        if (economy == null || amount <= 0) return false;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean deposit(OfflinePlayer player, double amount) {
        if (economy == null || amount <= 0) return false;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    /** Sets the balance to {@code amount} by depositing/withdrawing the difference. */
    public boolean set(OfflinePlayer player, double amount) {
        if (economy == null) return false;
        double current = economy.getBalance(player);
        if (amount > current) return deposit(player, amount - current);
        if (amount < current) return withdraw(player, current - amount);
        return true;
    }
}
