package mastrjimbo.itemsmith.listener;

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

/**
 * The movement / state family: jump, block-move, sneak, sprint, fly, glide,
 * swim, teleport, mount/dismount, bed, elytra boost, input and pose. All keyed
 * to the player's main-hand item. {@code move} and {@code input} are high-
 * frequency — only items that actually declare them do any work.
 */
public final class MovementListener implements Listener {

    private final AbilityEngine engine;

    public MovementListener(AbilityEngine engine) {
        this.engine = engine;
    }

    private void fireMain(String activator, Player player, org.bukkit.event.Event event, Object target) {
        engine.fireItem(activator, player, player.getInventory().getItemInMainHand(), event, target);
    }

    @EventHandler(ignoreCancelled = true)
    public void onJump(PlayerJumpEvent event) {
        fireMain(Activators.JUMP, event.getPlayer(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) return;
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ()) return; // same block — skip the per-pixel spam
        fireMain(Activators.MOVE, event.getPlayer(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        fireMain(event.isSneaking() ? Activators.SNEAK : Activators.UNSNEAK, event.getPlayer(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSprint(PlayerToggleSprintEvent event) {
        fireMain(event.isSprinting() ? Activators.SPRINT : Activators.UNSPRINT, event.getPlayer(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFlight(PlayerToggleFlightEvent event) {
        fireMain(event.isFlying() ? Activators.FLY_START : Activators.FLY_STOP, event.getPlayer(), event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onGlide(EntityToggleGlideEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        fireMain(event.isGliding() ? Activators.GLIDE_START : Activators.GLIDE_STOP, player, event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSwim(EntityToggleSwimEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        fireMain(event.isSwimming() ? Activators.SWIM_START : Activators.SWIM_STOP, player, event, null);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        // Portal travel has its own portal/change_world activators — don't double-fire generic teleport.
        PlayerTeleportEvent.TeleportCause cause = event.getCause();
        if (cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL
                || cause == PlayerTeleportEvent.TeleportCause.END_PORTAL
                || cause == PlayerTeleportEvent.TeleportCause.END_GATEWAY) {
            return;
        }
        fireMain(Activators.TELEPORT, event.getPlayer(), event, event.getTo());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMount(EntityMountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        fireMain(Activators.MOUNT, player, event, event.getMount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        fireMain(Activators.DISMOUNT, player, event, event.getDismounted());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedEnter(PlayerBedEnterEvent event) {
        fireMain(Activators.BED_ENTER, event.getPlayer(), event, event.getBed());
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        fireMain(Activators.BED_LEAVE, event.getPlayer(), event, event.getBed());
    }

    @EventHandler(ignoreCancelled = true)
    public void onElytraBoost(PlayerElytraBoostEvent event) {
        // The item that triggers this is the firework rocket used to boost, not the held item.
        engine.fireItem(Activators.ELYTRA_BOOST, event.getPlayer(), event.getItemStack(), event, null);
    }

    @EventHandler
    public void onInput(PlayerInputEvent event) {
        fireMain(Activators.INPUT, event.getPlayer(), event, null);
    }

    @EventHandler
    public void onPose(EntityPoseChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        fireMain(Activators.POSE_CHANGE, player, event, null);
    }
}
