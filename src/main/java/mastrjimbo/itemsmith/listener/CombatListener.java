package mastrjimbo.itemsmith.listener;

import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.registry.Activators;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * The combat event family: attacking, being hit (by source and by damage
 * cause), killing, dying and being targeted. Defense triggers fire across the
 * victim's held and worn items so reactive armour can respond.
 */
public final class CombatListener implements Listener {

    private final AbilityEngine engine;

    public CombatListener(AbilityEngine engine) {
        this.engine = engine;
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        ItemStack weapon = player.getInventory().getItemInMainHand();
        engine.fireItem(Activators.PLAYER_HIT_ENTITY, player, weapon, event, event.getEntity());
        if (event.getEntity() instanceof Player) {
            engine.fireItem(Activators.PLAYER_HIT_PLAYER, player, weapon, event, event.getEntity());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTakeDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Object attacker = event instanceof EntityDamageByEntityEvent byEntity ? byEntity.getDamager() : null;
        ItemStack[] gear = equipment(player);

        fireAll(Activators.PLAYER_TAKE_DAMAGE, player, gear, event, attacker);

        if (attacker instanceof Projectile) {
            fireAll(Activators.PLAYER_TAKE_DAMAGE_BY_PROJECTILE, player, gear, event, attacker);
        } else if (attacker != null) {
            fireAll(Activators.PLAYER_TAKE_DAMAGE_BY_ENTITY, player, gear, event, attacker);
            if (attacker instanceof Player) {
                fireAll(Activators.PLAYER_TAKE_DAMAGE_BY_PLAYER, player, gear, event, attacker);
            }
        }

        if (player.isBlocking()) {
            fireAll(Activators.PLAYER_BLOCK_HIT, player, gear, event, attacker);
        }

        String causeActivator = causeActivator(event.getCause());
        if (causeActivator != null) {
            fireAll(causeActivator, player, gear, event, attacker);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return; // player kills handled by onDeath
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;
        engine.fireItem(Activators.PLAYER_KILL_ENTITY, killer,
                killer.getInventory().getItemInMainHand(), event, event.getEntity());
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        engine.fireItem(Activators.PLAYER_DEATH, dead,
                dead.getInventory().getItemInMainHand(), event, dead.getKiller());
        Player killer = dead.getKiller();
        if (killer != null && killer != dead) {
            engine.fireItem(Activators.PLAYER_KILL_PLAYER, killer,
                    killer.getInventory().getItemInMainHand(), event, dead);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTargeted(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        engine.fireItem(Activators.PLAYER_TARGETED, player,
                player.getInventory().getItemInMainHand(), event, event.getEntity());
    }

    private void fireAll(String activator, Player player, ItemStack[] gear, EntityDamageEvent event, Object target) {
        for (ItemStack stack : gear) {
            engine.fireItem(activator, player, stack, event, target);
        }
    }

    private ItemStack[] equipment(Player player) {
        PlayerInventory inv = player.getInventory();
        return new ItemStack[]{
                inv.getItemInMainHand(), inv.getItemInOffHand(),
                inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots()
        };
    }

    /** Maps a damage cause to its specific activator id, or null if we don't expose one. */
    private String causeActivator(DamageCause cause) {
        return switch (cause) {
            case FALL -> Activators.TAKE_FALL_DAMAGE;
            case FIRE -> Activators.TAKE_FIRE_DAMAGE;
            case FIRE_TICK -> Activators.TAKE_FIRE_TICK_DAMAGE;
            case LAVA -> Activators.TAKE_LAVA_DAMAGE;
            case DROWNING -> Activators.TAKE_DROWN_DAMAGE;
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> Activators.TAKE_EXPLOSION_DAMAGE;
            case VOID -> Activators.TAKE_VOID_DAMAGE;
            case LIGHTNING -> Activators.TAKE_LIGHTNING_DAMAGE;
            case MAGIC -> Activators.TAKE_MAGIC_DAMAGE;
            case WITHER -> Activators.TAKE_WITHER_DAMAGE;
            case THORNS -> Activators.TAKE_THORNS_DAMAGE;
            case FREEZE -> Activators.TAKE_FREEZE_DAMAGE;
            case SUFFOCATION -> Activators.TAKE_SUFFOCATION_DAMAGE;
            case CONTACT -> Activators.TAKE_CONTACT_DAMAGE;
            case STARVATION -> Activators.TAKE_STARVATION_DAMAGE;
            case SONIC_BOOM -> Activators.TAKE_SONIC_BOOM_DAMAGE;
            case DRAGON_BREATH -> Activators.TAKE_DRAGON_BREATH_DAMAGE;
            default -> null;
        };
    }
}
