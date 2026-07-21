package mastrjimbo.itemsmith.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * The session / world family: join, quit, kick, world change, portal, game-mode
 * change, command and chat. {@code join} fires once per distinct custom item the
 * player carries; the rest key to the main hand. Chat arrives off the main
 * thread, so it is bounced to the next tick before running abilities.
 */
public final class SessionListener implements Listener {

    private final Plugin plugin;
    private final ItemRegistry registry;
    private final AbilityEngine engine;

    public SessionListener(Plugin plugin, ItemRegistry registry, AbilityEngine engine) {
        this.plugin = plugin;
        this.registry = registry;
        this.engine = engine;
    }

    private void fireMain(String activator, Player player, org.bukkit.event.Event event, Object target) {
        engine.fireItem(activator, player, player.getInventory().getItemInMainHand(), event, target);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Set<String> seen = new HashSet<>();
        for (ItemStack item : event.getPlayer().getInventory().getContents()) {
            String id = registry.idOf(item);
            if (id == null || !seen.add(id)) continue;
            engine.fireItem(Activators.JOIN, event.getPlayer(), item, event, null);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        fireMain(Activators.QUIT, event.getPlayer(), event, null);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        fireMain(Activators.KICK, event.getPlayer(), event, null);
    }

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        fireMain(Activators.CHANGE_WORLD, event.getPlayer(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        fireMain(Activators.PORTAL, event.getPlayer(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onGameMode(PlayerGameModeChangeEvent event) {
        fireMain(Activators.GAMEMODE_CHANGE, event.getPlayer(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        fireMain(Activators.COMMAND, event.getPlayer(), event, null);
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        ItemStack held = player.getInventory().getItemInMainHand();
        // AsyncChatEvent runs off the main thread; defer ability execution to a safe tick.
        plugin.getServer().getScheduler().runTask(plugin,
                () -> engine.fireItem(Activators.CHAT, player, held, null, null));
    }
}
