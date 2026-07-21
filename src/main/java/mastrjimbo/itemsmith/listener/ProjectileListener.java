package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.papermc.paper.event.entity.EntityLoadCrossbowEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

/**
 * The projectile family: shooting a bow/crossbow, throwing an item (snowball,
 * potion, pearl, trident, egg), the projectile landing, and riptide. The firing
 * item's id is stamped onto the projectile at launch so the landing trigger can
 * trace back to it even though the item is no longer "in hand".
 */
public final class ProjectileListener implements Listener {

    private final AbilityEngine engine;
    private final ItemRegistry registry;
    private final Plugin plugin;
    private final ProjectileTracker tracker;
    private final NamespacedKey shotKey;

    public ProjectileListener(Plugin plugin, ItemRegistry registry, AbilityEngine engine, ProjectileTracker tracker) {
        this.plugin = plugin;
        this.registry = registry;
        this.engine = engine;
        this.tracker = tracker;
        this.shotKey = new NamespacedKey(plugin, "shot_item");
    }

    @EventHandler(ignoreCancelled = true)
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack bow = event.getBow();
        String id = registry.idOf(bow);
        if (id == null) return;

        Entity projectile = event.getProjectile();
        projectile.getPersistentDataContainer().set(shotKey, PersistentDataType.STRING, id);
        if (projectile instanceof Projectile pr) tracker.track(pr);
        engine.fireItem(Activators.PROJECTILE_LAUNCH, player, bow, event, projectile);
        Material m = bow != null ? bow.getType() : null;
        if (m == Material.CROSSBOW) {
            engine.fireItem(Activators.PROJECTILE_LAUNCH_CROSSBOW, player, bow, event, projectile);
        } else if (m == Material.BOW) {
            engine.fireItem(Activators.PROJECTILE_LAUNCH_BOW, player, bow, event, projectile);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();
        if (!(proj.getShooter() instanceof Player player)) return;
        if (proj instanceof Arrow || proj instanceof SpectralArrow) return; // bow arrows handled by onShoot

        ItemStack hand = player.getInventory().getItemInMainHand();
        String id = registry.idOf(hand);
        if (id == null) {
            hand = player.getInventory().getItemInOffHand();
            id = registry.idOf(hand);
        }
        if (id == null) return;

        proj.getPersistentDataContainer().set(shotKey, PersistentDataType.STRING, id);
        tracker.track(proj);
        engine.fireItem(Activators.PROJECTILE_THROW, player, hand, event, proj);
        if (proj instanceof Trident) {
            engine.fireItem(Activators.PROJECTILE_LAUNCH, player, hand, event, proj);
            engine.fireItem(Activators.PROJECTILE_LAUNCH_TRIDENT, player, hand, event, proj);
        }
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        tracker.untrack(projectile); // it has landed; the tick tracker is done with it
        String id = projectile.getPersistentDataContainer().get(shotKey, PersistentDataType.STRING);
        if (id == null) return;
        if (!(projectile.getShooter() instanceof Player player)) return;

        Object target = event.getHitEntity() != null ? event.getHitEntity() : event.getHitBlock();
        fireById(Activators.PROJECTILE_HIT, player, id, event, target);
        if (event.getHitEntity() instanceof Player) {
            fireById(Activators.PROJECTILE_HIT_PLAYER, player, id, event, target);
        } else if (event.getHitEntity() != null) {
            fireById(Activators.PROJECTILE_HIT_ENTITY, player, id, event, target);
        } else if (event.getHitBlock() != null) {
            fireById(Activators.PROJECTILE_HIT_BLOCK, player, id, event, target);
        }
        // projectile_enter_liquid is handled by ProjectileTracker (projectiles pass through liquids).
    }

    @EventHandler(ignoreCancelled = true)
    public void onReadyArrow(PlayerReadyArrowEvent event) {
        // Fires as the player begins drawing a bow / loading a crossbow; item = the weapon.
        engine.fireItem(Activators.READY_ARROW, event.getPlayer(), event.getBow(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCrossbowLoad(EntityLoadCrossbowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        engine.fireItem(Activators.CROSSBOW_LOAD, player, event.getCrossbow(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRiptide(PlayerRiptideEvent event) {
        engine.fireItem(Activators.RIPTIDE, event.getPlayer(), event.getItem(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEgg(PlayerEggThrowEvent event) {
        engine.fireItem(Activators.EGG_THROW, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, event.getEgg());
    }

    private void fireById(String activator, Player player, String id, ProjectileHitEvent event, Object target) {
        // The firing item may no longer be in hand, so fire by id with no stack (skips native cooldown).
        engine.fire(activator, new AbilityContext(plugin, player, null, id, event, target, registry));
    }
}
