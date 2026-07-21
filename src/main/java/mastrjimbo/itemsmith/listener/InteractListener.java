package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * The interact event family: clicks (left/right, air/block, sneak variants),
 * arm-swing, and right-clicking entities/players. Click handling is filtered to
 * the main hand so the event's twice-per-click firing triggers abilities once.
 */
public final class InteractListener implements Listener {

    private final AbilityEngine engine;

    public InteractListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        ItemStack item = event.getItem();
        if (item == null) return;

        Action action = event.getAction();
        boolean right = action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
        boolean left = action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK;
        if (!right && !left) return; // PHYSICAL etc.

        boolean onBlock = action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK;
        boolean sneaking = event.getPlayer().isSneaking();
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        engine.fireItem(Activators.ANY_CLICK, player, item, event, block);
        if (right) {
            engine.fireItem(Activators.RIGHT_CLICK, player, item, event, block);
            engine.fireItem(onBlock ? Activators.RIGHT_CLICK_BLOCK : Activators.RIGHT_CLICK_AIR, player, item, event, block);
            if (sneaking) {
                engine.fireItem(Activators.SNEAK_RIGHT_CLICK, player, item, event, block);
                if (onBlock) engine.fireItem(Activators.SNEAK_RIGHT_CLICK_BLOCK, player, item, event, block);
            }
        } else {
            engine.fireItem(Activators.LEFT_CLICK, player, item, event, block);
            engine.fireItem(onBlock ? Activators.LEFT_CLICK_BLOCK : Activators.LEFT_CLICK_AIR, player, item, event, block);
            if (sneaking) {
                engine.fireItem(Activators.SNEAK_LEFT_CLICK, player, item, event, block);
                if (onBlock) engine.fireItem(Activators.SNEAK_LEFT_CLICK_BLOCK, player, item, event, block);
            }
        }
    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent event) {
        if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) return;
        engine.fireItem(Activators.ARM_SWING, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, null);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        var clicked = event.getRightClicked();

        engine.fireItem(Activators.CLICK_ENTITY, player, item, event, clicked);
        if (clicked instanceof Player) {
            engine.fireItem(Activators.CLICK_PLAYER, player, item, event, clicked);
        }
        if (player.isSneaking()) {
            engine.fireItem(Activators.SNEAK_CLICK_ENTITY, player, item, event, clicked);
        }
    }
}
