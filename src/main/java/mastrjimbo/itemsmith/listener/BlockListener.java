package mastrjimbo.itemsmith.listener;

import io.papermc.paper.event.block.BellRingEvent;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

/**
 * The block / world-interaction family: break, place, start/stop mining,
 * harvest, bone-meal, buckets, shear, armor stands, item frames, bells and
 * signs. Break/place carry a re-entrancy guard so a future block-breaking action
 * can't recursively re-trigger them.
 */
public final class BlockListener implements Listener {

    private final AbilityEngine engine;
    private final ItemRegistry registry;
    private boolean processing;

    public BlockListener(AbilityEngine engine, ItemRegistry registry) {
        this.engine = engine;
        this.registry = registry;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (processing) return;
        processing = true;
        try {
            engine.fireItem(Activators.BLOCK_BREAK, event.getPlayer(),
                    event.getPlayer().getInventory().getItemInMainHand(), event, event.getBlock());
        } finally {
            processing = false;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (processing) return;
        processing = true;
        try {
            engine.fireItem(Activators.BLOCK_PLACE, event.getPlayer(),
                    event.getItemInHand(), event, event.getBlockPlaced());
        } finally {
            processing = false;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamageStart(BlockDamageEvent event) {
        engine.fireItem(Activators.BLOCK_DAMAGE_START, event.getPlayer(),
                event.getItemInHand(), event, event.getBlock());
    }

    @EventHandler
    public void onDamageStop(BlockDamageAbortEvent event) {
        engine.fireItem(Activators.BLOCK_DAMAGE_STOP, event.getPlayer(),
                event.getItemInHand(), event, event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onHarvest(PlayerHarvestBlockEvent event) {
        engine.fireItem(Activators.HARVEST_BLOCK, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, event.getHarvestedBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onFertilize(BlockFertilizeEvent event) {
        if (event.getPlayer() == null) return;
        engine.fireItem(Activators.FERTILIZE_BLOCK, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        engine.fireItem(Activators.BUCKET_FILL, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        engine.fireItem(Activators.BUCKET_EMPTY, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketEntity(PlayerBucketEntityEvent event) {
        if (registry.idOf(event.getOriginalBucket()) == null) return; // vanilla bucket — leave it alone
        // Fire the trigger, then block the vanilla scoop so our custom bucket keeps its identity
        // (it never transforms into a plain fish bucket).
        engine.fireItem(Activators.BUCKET_ENTITY, event.getPlayer(),
                event.getOriginalBucket(), event, event.getEntity());
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        engine.fireItem(Activators.SHEAR_ENTITY, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorStand(PlayerArmorStandManipulateEvent event) {
        engine.fireItem(Activators.ARMOR_STAND_MANIPULATE, event.getPlayer(),
                event.getPlayerItem(), event, event.getRightClicked());
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemFrame(PlayerItemFrameChangeEvent event) {
        engine.fireItem(Activators.ITEM_FRAME_CHANGE, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, event.getItemFrame());
    }

    @EventHandler(ignoreCancelled = true)
    public void onBell(BellRingEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        engine.fireItem(Activators.RING_BELL, player,
                player.getInventory().getItemInMainHand(), event, event.getBlock());
    }

    @EventHandler(ignoreCancelled = true)
    public void onSign(SignChangeEvent event) {
        engine.fireItem(Activators.SIGN_EDIT, event.getPlayer(),
                event.getPlayer().getInventory().getItemInMainHand(), event, event.getBlock());
    }
}
