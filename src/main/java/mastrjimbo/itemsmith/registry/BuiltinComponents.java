package mastrjimbo.itemsmith.registry;

import mastrjimbo.itemsmith.component.action.AddDamageAction;
import mastrjimbo.itemsmith.component.action.ConsoleLogAction;
import mastrjimbo.itemsmith.component.action.MessageAction;
import mastrjimbo.itemsmith.component.action.PotionEffectAction;
// --- M4 gating & economics ---
import mastrjimbo.itemsmith.component.action.charges.AddChargesAction;
import mastrjimbo.itemsmith.component.action.charges.SetChargesAction;
import mastrjimbo.itemsmith.component.action.cooldown.SetCooldownAction;
import mastrjimbo.itemsmith.component.action.economy.GiveMoneyAction;
import mastrjimbo.itemsmith.component.action.economy.PayTargetAction;
import mastrjimbo.itemsmith.component.action.economy.SetMoneyAction;
import mastrjimbo.itemsmith.component.action.economy.TakeMoneyAction;
import mastrjimbo.itemsmith.component.action.player.*;
import mastrjimbo.itemsmith.component.condition.charges.ChargesAboveCondition;
import mastrjimbo.itemsmith.component.condition.charges.ChargesBelowCondition;
import mastrjimbo.itemsmith.component.condition.cooldown.CooldownReadyCondition;
import mastrjimbo.itemsmith.component.condition.economy.BalanceAboveCondition;
import mastrjimbo.itemsmith.component.condition.economy.BalanceBelowCondition;
import mastrjimbo.itemsmith.component.condition.economy.HasMoneyCondition;
import mastrjimbo.itemsmith.component.condition.region.CanBuildCondition;
import mastrjimbo.itemsmith.component.condition.region.InRegionCondition;
import mastrjimbo.itemsmith.component.condition.region.IsRegionMemberCondition;
import mastrjimbo.itemsmith.component.action.combat.AbsorptionAction;
import mastrjimbo.itemsmith.component.action.combat.BleedAction;
import mastrjimbo.itemsmith.component.action.combat.ShootProjectileAction;
import mastrjimbo.itemsmith.component.action.combat.ThrowItemAction;
import mastrjimbo.itemsmith.component.action.combat.DamageAction;
import mastrjimbo.itemsmith.component.action.combat.DamageNearbyAction;
import mastrjimbo.itemsmith.component.action.combat.DamageNoKnockbackAction;
import mastrjimbo.itemsmith.component.action.combat.DamagePercentAction;
import mastrjimbo.itemsmith.component.action.combat.DisarmAction;
import mastrjimbo.itemsmith.component.action.combat.ExplosionAction;
import mastrjimbo.itemsmith.component.action.combat.ExtinguishAction;
import mastrjimbo.itemsmith.component.action.combat.ForceDropAction;
import mastrjimbo.itemsmith.component.action.combat.FreezeAction;
import mastrjimbo.itemsmith.component.action.combat.GlowAction;
import mastrjimbo.itemsmith.component.action.combat.HealAction;
import mastrjimbo.itemsmith.component.action.combat.HealPercentAction;
import mastrjimbo.itemsmith.component.action.combat.IgniteAction;
import mastrjimbo.itemsmith.component.action.combat.InvulnerabilityAction;
import mastrjimbo.itemsmith.component.action.combat.KillAction;
import mastrjimbo.itemsmith.component.action.combat.KnockbackAction;
import mastrjimbo.itemsmith.component.action.combat.LaunchEntityAction;
import mastrjimbo.itemsmith.component.action.combat.LifestealAction;
import mastrjimbo.itemsmith.component.action.combat.PullAction;
import mastrjimbo.itemsmith.component.action.combat.SetHealthAction;
import mastrjimbo.itemsmith.component.action.combat.SetMaxHealthAction;
import mastrjimbo.itemsmith.component.action.combat.StealItemAction;
import mastrjimbo.itemsmith.component.action.combat.StrikeLightningAction;
import mastrjimbo.itemsmith.component.action.combat.TrueDamageAction;
import mastrjimbo.itemsmith.component.action.combat.UnfreezeAction;
import mastrjimbo.itemsmith.component.action.movement.BlinkAction;
import mastrjimbo.itemsmith.component.action.movement.CustomDashAction;
import mastrjimbo.itemsmith.component.action.movement.DashBackwardAction;
import mastrjimbo.itemsmith.component.action.movement.DashForwardAction;
import mastrjimbo.itemsmith.component.action.movement.FireworkBoostAction;
import mastrjimbo.itemsmith.component.action.movement.JumpAction;
import mastrjimbo.itemsmith.component.action.movement.LeapAction;
import mastrjimbo.itemsmith.component.action.movement.PropelAction;
import mastrjimbo.itemsmith.component.action.movement.SetFlySpeedAction;
import mastrjimbo.itemsmith.component.action.movement.SetGravityAction;
import mastrjimbo.itemsmith.component.action.movement.SetPitchAction;
import mastrjimbo.itemsmith.component.action.movement.SetRotationAction;
import mastrjimbo.itemsmith.component.action.movement.SetWalkSpeedAction;
import mastrjimbo.itemsmith.component.action.movement.SetYawAction;
import mastrjimbo.itemsmith.component.action.movement.SpinAction;
import mastrjimbo.itemsmith.component.action.movement.SpinTargetAction;
import mastrjimbo.itemsmith.component.action.movement.SwapPositionsAction;
import mastrjimbo.itemsmith.component.action.movement.TeleportAction;
import mastrjimbo.itemsmith.component.action.movement.TeleportCursorAction;
import mastrjimbo.itemsmith.component.action.movement.TeleportRelativeAction;
import mastrjimbo.itemsmith.component.action.movement.ToggleFlightAction;
import mastrjimbo.itemsmith.component.action.movement.VelocityAction;
import mastrjimbo.itemsmith.component.action.movement.WorldTeleportAction;
import mastrjimbo.itemsmith.component.action.effect.ClearEffectsAction;
import mastrjimbo.itemsmith.component.action.effect.CopyEffectsAction;
import mastrjimbo.itemsmith.component.action.effect.ExhaustionAction;
import mastrjimbo.itemsmith.component.action.effect.FeedAction;
import mastrjimbo.itemsmith.component.action.effect.OxygenAction;
import mastrjimbo.itemsmith.component.action.effect.PotionEffectSelfAction;
import mastrjimbo.itemsmith.component.action.effect.RemoveEffectAction;
import mastrjimbo.itemsmith.component.action.effect.SaturationAction;
import mastrjimbo.itemsmith.component.action.world.AreaBreakAction;
import mastrjimbo.itemsmith.component.action.world.AutoSmeltDropsAction;
import mastrjimbo.itemsmith.component.action.world.BonemealBlockAction;
import mastrjimbo.itemsmith.component.action.world.BreakBlockAction;
import mastrjimbo.itemsmith.component.action.world.BreakBlockNoDropAction;
import mastrjimbo.itemsmith.component.action.world.DrainLiquidAction;
import mastrjimbo.itemsmith.component.action.world.GrowCropAction;
import mastrjimbo.itemsmith.component.action.world.IgniteBlockAction;
import mastrjimbo.itemsmith.component.action.world.OpenDoorAction;
import mastrjimbo.itemsmith.component.action.world.PlaceLiquidAction;
import mastrjimbo.itemsmith.component.action.world.PushButtonAction;
import mastrjimbo.itemsmith.component.action.world.ReplaceNearBlocksAction;
import mastrjimbo.itemsmith.component.action.world.SetBlockAction;
import mastrjimbo.itemsmith.component.action.world.SetPlayerTimeAction;
import mastrjimbo.itemsmith.component.action.world.SetPlayerWeatherAction;
import mastrjimbo.itemsmith.component.action.world.SetTempBlockAction;
import mastrjimbo.itemsmith.component.action.world.SetTimeAction;
import mastrjimbo.itemsmith.component.action.world.SetWeatherAction;
import mastrjimbo.itemsmith.component.action.world.SpawnEntityAction;
import mastrjimbo.itemsmith.component.action.world.SpawnFallingBlockAction;
import mastrjimbo.itemsmith.component.action.world.ToggleLeverAction;
import mastrjimbo.itemsmith.component.action.world.VeinBreakAction;
import mastrjimbo.itemsmith.component.action.command.AddTagAction;
import mastrjimbo.itemsmith.component.action.command.BroadcastAction;
import mastrjimbo.itemsmith.component.action.command.CancelEventAction;
import mastrjimbo.itemsmith.component.action.command.RemoveTagAction;
import mastrjimbo.itemsmith.component.action.command.RunCommandConsoleAction;
import mastrjimbo.itemsmith.component.action.command.RunCommandOpAction;
import mastrjimbo.itemsmith.component.action.command.RunCommandPlayerAction;
import mastrjimbo.itemsmith.component.action.command.SetVariableAction;
import mastrjimbo.itemsmith.component.action.visual.FireworkEffectAction;
import mastrjimbo.itemsmith.component.action.visual.HurtAnimationAction;
import mastrjimbo.itemsmith.component.action.visual.ParticleAction;
import mastrjimbo.itemsmith.component.action.visual.ParticleBurstAction;
import mastrjimbo.itemsmith.component.action.visual.ParticleCircleAction;
import mastrjimbo.itemsmith.component.action.visual.ParticleHelixAction;
import mastrjimbo.itemsmith.component.action.visual.ParticleLineAction;
import mastrjimbo.itemsmith.component.action.visual.ParticleRingAction;
import mastrjimbo.itemsmith.component.action.visual.ParticleSphereAction;
import mastrjimbo.itemsmith.component.action.visual.PlaySoundAction;
import mastrjimbo.itemsmith.component.action.visual.PlaySoundAllAction;
import mastrjimbo.itemsmith.component.action.visual.SendActionbarAction;
import mastrjimbo.itemsmith.component.action.visual.SendBlankMessageAction;
import mastrjimbo.itemsmith.component.action.visual.SendBossbarAction;
import mastrjimbo.itemsmith.component.action.visual.SendCenteredMessageAction;
import mastrjimbo.itemsmith.component.action.visual.SendTitleAction;
import mastrjimbo.itemsmith.component.action.visual.ShootParticleAction;
import mastrjimbo.itemsmith.component.action.visual.SpawnFireworkAction;
import mastrjimbo.itemsmith.component.action.visual.StopSoundAction;
import mastrjimbo.itemsmith.component.action.visual.SwingHandAction;
import mastrjimbo.itemsmith.component.action.visual.SwingOffhandAction;
import mastrjimbo.itemsmith.component.action.visual.TotemAnimationAction;
import mastrjimbo.itemsmith.component.action.flow.AbortAction;
import mastrjimbo.itemsmith.component.action.flow.ChanceAction;
import mastrjimbo.itemsmith.component.action.flow.DelayAction;
import mastrjimbo.itemsmith.component.action.flow.IfAction;
import mastrjimbo.itemsmith.component.action.flow.NothingAction;
import mastrjimbo.itemsmith.component.action.flow.RandomAction;
import mastrjimbo.itemsmith.component.action.flow.RepeatAction;
import mastrjimbo.itemsmith.component.condition.ChanceCondition;
import mastrjimbo.itemsmith.component.condition.health.AbsorptionAboveCondition;
import mastrjimbo.itemsmith.component.condition.health.AirAboveCondition;
import mastrjimbo.itemsmith.component.condition.health.FoodAboveCondition;
import mastrjimbo.itemsmith.component.condition.health.FoodBelowCondition;
import mastrjimbo.itemsmith.component.condition.health.HealthAboveCondition;
import mastrjimbo.itemsmith.component.condition.health.HealthBelowCondition;
import mastrjimbo.itemsmith.component.condition.health.HealthPercentAboveCondition;
import mastrjimbo.itemsmith.component.condition.health.HealthPercentBelowCondition;
import mastrjimbo.itemsmith.component.condition.health.XpLevelAboveCondition;
import mastrjimbo.itemsmith.component.condition.health.XpLevelBelowCondition;
import mastrjimbo.itemsmith.component.condition.identity.GamemodeIsCondition;
import mastrjimbo.itemsmith.component.condition.identity.HasPermissionCondition;
import mastrjimbo.itemsmith.component.condition.identity.HasPotionEffectCondition;
import mastrjimbo.itemsmith.component.condition.identity.IsOpCondition;
import mastrjimbo.itemsmith.component.condition.identity.NameIsCondition;
import mastrjimbo.itemsmith.component.condition.item.DurabilityAboveCondition;
import mastrjimbo.itemsmith.component.condition.item.DurabilityBelowCondition;
import mastrjimbo.itemsmith.component.condition.item.DurabilityPercentBelowCondition;
import mastrjimbo.itemsmith.component.condition.item.HasEnchantCondition;
import mastrjimbo.itemsmith.component.condition.item.HoldingItemCondition;
import mastrjimbo.itemsmith.component.condition.item.InSlotCondition;
import mastrjimbo.itemsmith.component.condition.item.ItemNameContainsCondition;
import mastrjimbo.itemsmith.component.condition.item.WearingCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsBlockingCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsBurningCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsFlyingCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsGlidingCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsInLavaCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsInWaterCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsOnGroundCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsSleepingCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsSneakingCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsSprintingCondition;
import mastrjimbo.itemsmith.component.condition.playerstate.IsSwimmingCondition;
import mastrjimbo.itemsmith.component.condition.target.EntityTypeIsCondition;
import mastrjimbo.itemsmith.component.condition.target.IsBabyCondition;
import mastrjimbo.itemsmith.component.condition.target.IsTamedCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetDistanceBelowCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetHasEffectCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetHealthAboveCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetHealthBelowCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetHealthPercentBelowCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetIsLivingCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetIsMobCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetIsOnFireCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetIsPlayerCondition;
import mastrjimbo.itemsmith.component.condition.target.TargetNameContainsCondition;
import mastrjimbo.itemsmith.component.condition.time.IsDayCondition;
import mastrjimbo.itemsmith.component.condition.time.IsNightCondition;
import mastrjimbo.itemsmith.component.condition.time.MoonPhaseCondition;
import mastrjimbo.itemsmith.component.condition.time.TimeOfDayCondition;
import mastrjimbo.itemsmith.component.condition.world.BiomeIsCondition;
import mastrjimbo.itemsmith.component.condition.world.BlockAtCondition;
import mastrjimbo.itemsmith.component.condition.world.CanSeeSkyCondition;
import mastrjimbo.itemsmith.component.condition.world.DimensionIsCondition;
import mastrjimbo.itemsmith.component.condition.world.IsRainingCondition;
import mastrjimbo.itemsmith.component.condition.world.IsThunderingCondition;
import mastrjimbo.itemsmith.component.condition.world.LightAboveCondition;
import mastrjimbo.itemsmith.component.condition.world.LightBelowCondition;
import mastrjimbo.itemsmith.component.condition.world.WeatherIsCondition;
import mastrjimbo.itemsmith.component.condition.world.WorldIsCondition;
import mastrjimbo.itemsmith.component.condition.world.YAboveCondition;
import mastrjimbo.itemsmith.component.condition.world.YBelowCondition;
import mastrjimbo.itemsmith.component.targeter.BlockBelowTargetTargeter;
import mastrjimbo.itemsmith.component.targeter.ConeTargeter;
import mastrjimbo.itemsmith.component.targeter.LineTargeter;
import mastrjimbo.itemsmith.component.targeter.LocationOfSelfTargeter;
import mastrjimbo.itemsmith.component.targeter.LocationOfTargetTargeter;
import mastrjimbo.itemsmith.component.targeter.LookingAtBlockTargeter;
import mastrjimbo.itemsmith.component.targeter.LookingAtEntityTargeter;
import mastrjimbo.itemsmith.component.targeter.LookingDirectionTargeter;
import mastrjimbo.itemsmith.component.targeter.NearbyEntitiesTargeter;
import mastrjimbo.itemsmith.component.targeter.NearbyMonstersTargeter;
import mastrjimbo.itemsmith.component.targeter.NearbyPlayersTargeter;
import mastrjimbo.itemsmith.component.targeter.NearestEntityTargeter;
import mastrjimbo.itemsmith.component.targeter.NearestPlayerTargeter;
import mastrjimbo.itemsmith.component.targeter.OffsetTargeter;
import mastrjimbo.itemsmith.component.targeter.RadiusTargeter;
import mastrjimbo.itemsmith.component.targeter.RingTargeter;
import mastrjimbo.itemsmith.component.targeter.SelfTargeter;
import mastrjimbo.itemsmith.component.targeter.TriggerTargeter;
import mastrjimbo.itemsmith.engine.SimpleActivator;

import static mastrjimbo.itemsmith.registry.Activators.*;

/**
 * Registers every built-in component into the {@link Registries} at enable.
 * Each milestone adds its activators/conditions/targeters/actions here — this is
 * the single list of what ItemSmith can do. Every activator below is backed by a
 * real event handler in {@code listener/}.
 */
public final class BuiltinComponents {

    private BuiltinComponents() {
    }

    public static void registerAll(Registries r) {
        registerActivators(r);
        registerTargeters(r);
        registerConditions(r);
        registerActions(r);
    }

    private static void registerActivators(Registries r) {
        // Interact — clicks
        act(r, RIGHT_CLICK, Categories.INTERACT, "Right Click", "Right-click with the item (air or block).");
        act(r, RIGHT_CLICK_AIR, Categories.INTERACT, "Right Click Air", "Right-click aiming at nothing.");
        act(r, RIGHT_CLICK_BLOCK, Categories.INTERACT, "Right Click Block", "Right-click a block (block is the target).");
        act(r, LEFT_CLICK, Categories.INTERACT, "Left Click", "Left-click with the item (air or block).");
        act(r, LEFT_CLICK_AIR, Categories.INTERACT, "Left Click Air", "Left-click aiming at nothing.");
        act(r, LEFT_CLICK_BLOCK, Categories.INTERACT, "Left Click Block", "Left-click a block (block is the target).");
        act(r, ANY_CLICK, Categories.INTERACT, "Any Click", "Either mouse button.");
        act(r, SNEAK_RIGHT_CLICK, Categories.INTERACT, "Sneak + Right Click", "Right-click while sneaking.");
        act(r, SNEAK_LEFT_CLICK, Categories.INTERACT, "Sneak + Left Click", "Left-click while sneaking.");
        act(r, SNEAK_RIGHT_CLICK_BLOCK, Categories.INTERACT, "Sneak + Right Click Block", "Sneak-right-click a block.");
        act(r, SNEAK_LEFT_CLICK_BLOCK, Categories.INTERACT, "Sneak + Left Click Block", "Sneak-left-click a block.");
        act(r, ARM_SWING, Categories.INTERACT, "Arm Swing", "Any hand-swing animation.");
        // Interact — entities
        act(r, CLICK_ENTITY, Categories.INTERACT, "Click Entity", "Right-click a mob (entity is the target).");
        act(r, CLICK_PLAYER, Categories.INTERACT, "Click Player", "Right-click another player.");
        act(r, SNEAK_CLICK_ENTITY, Categories.INTERACT, "Sneak + Click Entity", "Sneak-right-click a mob.");
        // Inventory
        act(r, INVENTORY_CLICK, Categories.INTERACT, "Inventory Click", "Click the item inside an inventory GUI.");
        act(r, INVENTORY_DRAG, Categories.INTERACT, "Inventory Drag", "Drag the item across inventory slots.");
        act(r, OPEN_INVENTORY, Categories.INTERACT, "Open Inventory", "Open a container/inventory.");
        act(r, CLOSE_INVENTORY, Categories.INTERACT, "Close Inventory", "Close a container/inventory.");

        // Combat — attacking
        act(r, PLAYER_HIT_ENTITY, Categories.COMBAT, "On Hit Entity", "You strike an entity in melee.");
        act(r, PLAYER_HIT_PLAYER, Categories.COMBAT, "On Hit Player", "You strike another player.");
        act(r, PLAYER_KILL_ENTITY, Categories.COMBAT, "On Kill Entity", "You kill an entity.");
        act(r, PLAYER_KILL_PLAYER, Categories.COMBAT, "On Kill Player", "You kill another player.");
        // Combat — defense
        act(r, PLAYER_TAKE_DAMAGE, Categories.COMBAT, "On Take Damage", "You take damage from any source.");
        act(r, PLAYER_TAKE_DAMAGE_BY_ENTITY, Categories.COMBAT, "On Hit By Entity", "You take damage from a mob.");
        act(r, PLAYER_TAKE_DAMAGE_BY_PLAYER, Categories.COMBAT, "On Hit By Player", "You take damage from a player.");
        act(r, PLAYER_TAKE_DAMAGE_BY_PROJECTILE, Categories.COMBAT, "On Hit By Projectile", "A projectile hits you.");
        act(r, PLAYER_BLOCK_HIT, Categories.COMBAT, "On Shield Block", "You block a hit with a shield.");
        act(r, PLAYER_TARGETED, Categories.COMBAT, "On Targeted", "A mob starts targeting you.");
        // Damage causes
        act(r, TAKE_FALL_DAMAGE, Categories.COMBAT, "On Fall Damage", "You take fall damage.");
        act(r, TAKE_FIRE_DAMAGE, Categories.COMBAT, "On Fire Damage", "You take fire (direct) damage.");
        act(r, TAKE_FIRE_TICK_DAMAGE, Categories.COMBAT, "On Burn", "You take burning fire-tick damage.");
        act(r, TAKE_LAVA_DAMAGE, Categories.COMBAT, "On Lava Damage", "You take lava damage.");
        act(r, TAKE_DROWN_DAMAGE, Categories.COMBAT, "On Drown Damage", "You take drowning damage.");
        act(r, TAKE_EXPLOSION_DAMAGE, Categories.COMBAT, "On Explosion Damage", "You take explosion damage.");
        act(r, TAKE_VOID_DAMAGE, Categories.COMBAT, "On Void Damage", "You take void damage.");
        act(r, TAKE_LIGHTNING_DAMAGE, Categories.COMBAT, "On Lightning Damage", "Lightning strikes you.");
        act(r, TAKE_MAGIC_DAMAGE, Categories.COMBAT, "On Magic Damage", "You take magic/potion damage.");
        act(r, TAKE_WITHER_DAMAGE, Categories.COMBAT, "On Wither Damage", "You take wither damage.");
        act(r, TAKE_THORNS_DAMAGE, Categories.COMBAT, "On Thorns Damage", "Thorns armor hits you.");
        act(r, TAKE_FREEZE_DAMAGE, Categories.COMBAT, "On Freeze Damage", "Powder snow freezes you.");
        act(r, TAKE_SUFFOCATION_DAMAGE, Categories.COMBAT, "On Suffocate", "You suffocate in a block.");
        act(r, TAKE_CONTACT_DAMAGE, Categories.COMBAT, "On Contact Damage", "A cactus/berry bush hurts you.");
        act(r, TAKE_STARVATION_DAMAGE, Categories.COMBAT, "On Starve", "You take starvation damage.");
        act(r, TAKE_SONIC_BOOM_DAMAGE, Categories.COMBAT, "On Sonic Boom", "A warden's sonic boom hits you.");
        act(r, TAKE_DRAGON_BREATH_DAMAGE, Categories.COMBAT, "On Dragon Breath", "Dragon breath hurts you.");
        // Effects
        act(r, PLAYER_RECEIVE_EFFECT, Categories.COMBAT, "On Gain Effect", "You gain a potion effect.");
        act(r, PLAYER_EFFECT_EXPIRE, Categories.COMBAT, "On Effect Expire", "One of your effects ends.");

        // Player — death / health / xp / session
        act(r, PLAYER_DEATH, Categories.PLAYER, "On Death", "You die holding the item.");
        act(r, PLAYER_RESPAWN, Categories.PLAYER, "On Respawn", "You respawn.");
        act(r, REGAIN_HEALTH, Categories.PLAYER, "On Regain Health", "You regain health.");
        act(r, EXPERIENCE_CHANGE, Categories.PLAYER, "On XP Change", "Your experience changes.");
        act(r, LEVEL_UP, Categories.PLAYER, "On Level Up", "Your XP level increases.");
        act(r, LEVEL_DOWN, Categories.PLAYER, "On Level Down", "Your XP level decreases.");
        act(r, FOOD_CHANGE, Categories.PLAYER, "On Hunger Change", "Your food level changes.");
        act(r, TOTEM_USE, Categories.PLAYER, "On Totem", "A totem saves you from death.");
        act(r, JOIN, Categories.PLAYER, "On Join", "You log in with the item in your inventory.");
        act(r, QUIT, Categories.PLAYER, "On Quit", "You log out holding the item.");
        act(r, KICK, Categories.PLAYER, "On Kick", "You are kicked holding the item.");
        act(r, CHANGE_WORLD, Categories.PLAYER, "On Change World", "You switch worlds.");
        act(r, PORTAL, Categories.PLAYER, "On Portal", "You travel through a portal.");
        act(r, GAMEMODE_CHANGE, Categories.PLAYER, "On Gamemode Change", "Your game mode changes.");
        act(r, COMMAND, Categories.PLAYER, "On Command", "You run a chat command.");
        act(r, CHAT, Categories.PLAYER, "On Chat", "You send a chat message.");

        // Durability
        act(r, ITEM_DURABILITY_DAMAGE, Categories.ITEM, "On Durability Loss", "The item loses durability.");
        act(r, ITEM_BREAK, Categories.ITEM, "On Item Break", "The item breaks from durability.");
        act(r, ITEM_MEND, Categories.ITEM, "On Mend", "The item is repaired by Mending.");

        // Projectiles
        act(r, PROJECTILE_LAUNCH, Categories.PROJECTILE, "On Shoot", "You fire a bow/crossbow/trident.");
        act(r, PROJECTILE_LAUNCH_BOW, Categories.PROJECTILE, "On Shoot Bow", "You fire specifically a bow.");
        act(r, PROJECTILE_LAUNCH_CROSSBOW, Categories.PROJECTILE, "On Shoot Crossbow", "You fire specifically a crossbow.");
        act(r, PROJECTILE_LAUNCH_TRIDENT, Categories.PROJECTILE, "On Throw Trident", "You throw a trident.");
        act(r, READY_ARROW, Categories.PROJECTILE, "On Draw / Nock", "You start drawing a bow or loading a crossbow.");
        act(r, CROSSBOW_LOAD, Categories.PROJECTILE, "On Crossbow Loaded", "A crossbow finishes charging a projectile.");
        act(r, PROJECTILE_THROW, Categories.PROJECTILE, "On Throw Item", "You throw the item (snowball/potion/pearl).");
        act(r, PROJECTILE_HIT, Categories.PROJECTILE, "On Projectile Land", "A projectile you fired lands anywhere.");
        act(r, PROJECTILE_HIT_BLOCK, Categories.PROJECTILE, "On Projectile Hit Block", "Your projectile hits a block.");
        act(r, PROJECTILE_HIT_ENTITY, Categories.PROJECTILE, "On Projectile Hit Entity", "Your projectile hits a mob.");
        act(r, PROJECTILE_HIT_PLAYER, Categories.PROJECTILE, "On Projectile Hit Player", "Your projectile hits a player.");
        act(r, PROJECTILE_ENTER_LIQUID, Categories.PROJECTILE, "On Projectile Enter Liquid", "Your projectile hits water/lava.");
        act(r, RIPTIDE, Categories.PROJECTILE, "On Riptide", "You riptide with a trident.");
        act(r, EGG_THROW, Categories.PROJECTILE, "On Egg Throw", "You throw an egg.");

        // Blocks / world
        act(r, BLOCK_BREAK, Categories.BLOCK, "On Break Block", "You break a block with the item.");
        act(r, BLOCK_PLACE, Categories.BLOCK, "On Place Block", "You place the item as a block.");
        act(r, BLOCK_DAMAGE_START, Categories.BLOCK, "On Start Mining", "You begin breaking a block.");
        act(r, BLOCK_DAMAGE_STOP, Categories.BLOCK, "On Stop Mining", "You stop breaking a block.");
        act(r, HARVEST_BLOCK, Categories.BLOCK, "On Harvest", "You harvest a mature crop.");
        act(r, FERTILIZE_BLOCK, Categories.BLOCK, "On Bone-meal", "You bone-meal/fertilize a block.");
        act(r, BUCKET_FILL, Categories.BLOCK, "On Bucket Fill", "You fill a bucket.");
        act(r, BUCKET_EMPTY, Categories.BLOCK, "On Bucket Empty", "You empty a bucket.");
        act(r, BUCKET_ENTITY, Categories.BLOCK, "On Bucket Entity", "You bucket a mob (fish/axolotl).");
        act(r, SHEAR_ENTITY, Categories.BLOCK, "On Shear", "You shear a sheep/mooshroom.");
        act(r, ARMOR_STAND_MANIPULATE, Categories.BLOCK, "On Armor Stand Use", "You manipulate an armor stand.");
        act(r, ITEM_FRAME_CHANGE, Categories.BLOCK, "On Item Frame Change", "You place/rotate/remove an item frame item.");
        act(r, RING_BELL, Categories.BLOCK, "On Ring Bell", "You ring a bell.");
        act(r, SIGN_EDIT, Categories.BLOCK, "On Sign Edit", "You finish editing a sign.");

        // Movement / state
        act(r, JUMP, Categories.MOVEMENT, "On Jump", "You jump.");
        act(r, MOVE, Categories.MOVEMENT, "On Move (block)", "You move into a new block (heavy — gate it).");
        act(r, SNEAK, Categories.MOVEMENT, "On Sneak", "You start sneaking.");
        act(r, UNSNEAK, Categories.MOVEMENT, "On Unsneak", "You stop sneaking.");
        act(r, SPRINT, Categories.MOVEMENT, "On Sprint", "You start sprinting.");
        act(r, UNSPRINT, Categories.MOVEMENT, "On Stop Sprint", "You stop sprinting.");
        act(r, FLY_START, Categories.MOVEMENT, "On Start Fly", "You start flying.");
        act(r, FLY_STOP, Categories.MOVEMENT, "On Stop Fly", "You stop flying.");
        act(r, GLIDE_START, Categories.MOVEMENT, "On Start Glide", "You start elytra gliding.");
        act(r, GLIDE_STOP, Categories.MOVEMENT, "On Stop Glide", "You stop elytra gliding.");
        act(r, SWIM_START, Categories.MOVEMENT, "On Start Swim", "You start swimming.");
        act(r, SWIM_STOP, Categories.MOVEMENT, "On Stop Swim", "You stop swimming.");
        act(r, TELEPORT, Categories.MOVEMENT, "On Teleport", "You teleport.");
        act(r, MOUNT, Categories.MOVEMENT, "On Mount", "You mount an entity.");
        act(r, DISMOUNT, Categories.MOVEMENT, "On Dismount", "You dismount an entity.");
        act(r, BED_ENTER, Categories.MOVEMENT, "On Bed Enter", "You get into a bed.");
        act(r, BED_LEAVE, Categories.MOVEMENT, "On Bed Leave", "You get out of a bed.");
        act(r, ELYTRA_BOOST, Categories.MOVEMENT, "On Elytra Boost", "You boost with a firework while gliding.");
        act(r, INPUT, Categories.MOVEMENT, "On Input", "Your movement-key input changes (heavy).");
        act(r, POSE_CHANGE, Categories.MOVEMENT, "On Pose Change", "Your body pose changes.");

        // Item lifecycle
        act(r, ITEM_HOLD, Categories.ITEM, "On Select", "The item becomes your selected hotbar slot.");
        act(r, ITEM_UNHOLD, Categories.ITEM, "On Deselect", "You switch away from the item.");
        act(r, EQUIP, Categories.ITEM, "On Equip", "You equip the item as armor.");
        act(r, UNEQUIP, Categories.ITEM, "On Unequip", "You remove the armor item.");
        act(r, SWAP_HAND, Categories.ITEM, "On Swap Hands", "You swap the item between hands.");
        act(r, ITEM_DROP, Categories.ITEM, "On Drop", "You drop the item.");
        act(r, ITEM_PICKUP, Categories.ITEM, "On Pickup", "You pick the item up.");
        act(r, ITEM_CONSUME, Categories.ITEM, "On Consume", "You finish eating/drinking the item.");
        act(r, STOP_USING_ITEM, Categories.ITEM, "On Release Use", "You release a charging item (bow/food).");
        act(r, BOOK_EDIT, Categories.ITEM, "On Book Edit", "You save edits to a writable book (without signing).");
        act(r, BOOK_SIGN, Categories.ITEM, "On Book Sign", "You sign a book into a written book.");
        act(r, PICKUP_PROJECTILE, Categories.ITEM, "On Pickup Projectile", "You pick up any projectile (arrow/trident/etc).");
        act(r, PICKUP_ARROW, Categories.ITEM, "On Pickup Arrow", "You pick up an arrow.");
        act(r, PICKUP_TRIDENT, Categories.ITEM, "On Pickup Trident", "You pick up a landed trident.");
        act(r, PICKUP_SPECTRAL_ARROW, Categories.ITEM, "On Pickup Spectral Arrow", "You pick up a spectral arrow.");

        // Entity interactions
        act(r, FEED_ENTITY, Categories.ITEM, "On Feed", "You feed an animal breeding food (it enters love mode).");
        act(r, BREED_ENTITY, Categories.ITEM, "On Breed", "You breed two animals.");
        act(r, TAME_ENTITY, Categories.ITEM, "On Tame", "You tame an animal.");
        act(r, NAME_ENTITY, Categories.ITEM, "On Name Entity", "You name a mob with a name tag.");
        act(r, LEASH_ENTITY, Categories.ITEM, "On Leash", "You leash a mob.");
        act(r, TRADE, Categories.ITEM, "On Trade", "You complete a villager trade.");

        // Crafting / stations
        act(r, CRAFT_ITEM, Categories.ITEM, "On Craft", "The item is crafted.");
        act(r, ENCHANT_ITEM, Categories.ITEM, "On Enchant", "The item is enchanted at a table.");
        act(r, SMITH_ITEM, Categories.ITEM, "On Smith", "The item is produced at a smithing table.");
        act(r, RECIPE_DISCOVER, Categories.ITEM, "On Recipe Discover", "You unlock a recipe.");
        act(r, ADVANCEMENT, Categories.ITEM, "On Advancement", "You earn an advancement.");

        // Fishing
        act(r, FISH_CAST, Categories.ITEM, "On Fish Cast", "You cast the fishing rod.");
        act(r, FISH_BITE, Categories.ITEM, "On Fish Bite", "A fish bites your line.");
        act(r, FISH_CATCH, Categories.ITEM, "On Fish Catch", "You catch a fish/item.");
        act(r, FISH_CATCH_ENTITY, Categories.ITEM, "On Fish Catch Entity", "Your rod hooks an entity.");
        act(r, FISH_IN_GROUND, Categories.ITEM, "On Bobber In Ground", "Your bobber lands on a block.");
        act(r, FISH_REEL, Categories.ITEM, "On Reel In", "You reel in with no catch.");

        // Passive / timer
        act(r, HOLD_TICK, Categories.LIFECYCLE, "While Held (tick)", "Repeatedly while the item is in your main hand.");
        act(r, INVENTORY_TICK, Categories.LIFECYCLE, "While Carried (tick)", "Repeatedly while the item is anywhere in your inventory.");
        act(r, EQUIP_TICK, Categories.LIFECYCLE, "While Worn (tick)", "Repeatedly while the item is worn as armor.");
    }

    private static void registerTargeters(Registries r) {
        r.register(new BlockBelowTargetTargeter());
        r.register(new ConeTargeter());
        r.register(new LineTargeter());
        r.register(new LocationOfSelfTargeter());
        r.register(new LocationOfTargetTargeter());
        r.register(new LookingAtBlockTargeter());
        r.register(new LookingAtEntityTargeter());
        r.register(new LookingDirectionTargeter());
        r.register(new NearbyEntitiesTargeter());
        r.register(new NearbyMonstersTargeter());
        r.register(new NearbyPlayersTargeter());
        r.register(new NearestEntityTargeter());
        r.register(new NearestPlayerTargeter());
        r.register(new OffsetTargeter());
        r.register(new RadiusTargeter());
        r.register(new RingTargeter());
        r.register(new SelfTargeter());
        r.register(new TriggerTargeter());
    }

    private static void registerConditions(Registries r) {
        r.register(new ChanceCondition());
        // --- playerstate ---
        r.register(new IsBlockingCondition());
        r.register(new IsBurningCondition());
        r.register(new IsFlyingCondition());
        r.register(new IsGlidingCondition());
        r.register(new IsInLavaCondition());
        r.register(new IsInWaterCondition());
        r.register(new IsOnGroundCondition());
        r.register(new IsSleepingCondition());
        r.register(new IsSneakingCondition());
        r.register(new IsSprintingCondition());
        r.register(new IsSwimmingCondition());
        // --- health ---
        r.register(new AbsorptionAboveCondition());
        r.register(new AirAboveCondition());
        r.register(new FoodAboveCondition());
        r.register(new FoodBelowCondition());
        r.register(new HealthAboveCondition());
        r.register(new HealthBelowCondition());
        r.register(new HealthPercentAboveCondition());
        r.register(new HealthPercentBelowCondition());
        r.register(new XpLevelAboveCondition());
        r.register(new XpLevelBelowCondition());
        // --- identity ---
        r.register(new GamemodeIsCondition());
        r.register(new HasPermissionCondition());
        r.register(new HasPotionEffectCondition());
        r.register(new IsOpCondition());
        r.register(new NameIsCondition());
        // --- time ---
        r.register(new IsDayCondition());
        r.register(new IsNightCondition());
        r.register(new MoonPhaseCondition());
        r.register(new TimeOfDayCondition());
        // --- world ---
        r.register(new BiomeIsCondition());
        r.register(new BlockAtCondition());
        r.register(new CanSeeSkyCondition());
        r.register(new DimensionIsCondition());
        r.register(new IsRainingCondition());
        r.register(new IsThunderingCondition());
        r.register(new LightAboveCondition());
        r.register(new LightBelowCondition());
        r.register(new WeatherIsCondition());
        r.register(new WorldIsCondition());
        r.register(new YAboveCondition());
        r.register(new YBelowCondition());
        // --- target ---
        r.register(new EntityTypeIsCondition());
        r.register(new IsBabyCondition());
        r.register(new IsTamedCondition());
        r.register(new TargetDistanceBelowCondition());
        r.register(new TargetHasEffectCondition());
        r.register(new TargetHealthAboveCondition());
        r.register(new TargetHealthBelowCondition());
        r.register(new TargetHealthPercentBelowCondition());
        r.register(new TargetIsLivingCondition());
        r.register(new TargetIsMobCondition());
        r.register(new TargetIsOnFireCondition());
        r.register(new TargetIsPlayerCondition());
        r.register(new TargetNameContainsCondition());
        // --- item ---
        r.register(new DurabilityAboveCondition());
        r.register(new DurabilityBelowCondition());
        r.register(new DurabilityPercentBelowCondition());
        r.register(new HasEnchantCondition());
        r.register(new HoldingItemCondition());
        r.register(new InSlotCondition());
        r.register(new ItemNameContainsCondition());
        r.register(new WearingCondition());
        // --- M4: charges / cooldown / economy / region ---
        r.register(new ChargesAboveCondition());
        r.register(new ChargesBelowCondition());
        r.register(new CooldownReadyCondition());
        r.register(new HasMoneyCondition());
        r.register(new BalanceAboveCondition());
        r.register(new BalanceBelowCondition());
        r.register(new InRegionCondition());
        r.register(new CanBuildCondition());
        r.register(new IsRegionMemberCondition());
    }

    /** M2 expands this to 100+. */
    private static void registerActions(Registries r) {
        // --- M0/M1 seed actions ---
        r.register(new AddDamageAction());
        r.register(new PotionEffectAction());
        r.register(new MessageAction());
        r.register(new ConsoleLogAction());

        // --- Flow / control (nested action bodies + timed sequencing) ---
        r.register(new DelayAction());
        r.register(new RepeatAction());
        r.register(new IfAction());
        r.register(new RandomAction());
        r.register(new ChanceAction());
        r.register(new AbortAction());
        r.register(new NothingAction());

        // --- M2 leaf actions (Combat, Movement, Effects, World, Player, Command, Visual) ---
        // --- combat ---
        r.register(new AbsorptionAction());
        r.register(new BleedAction());
        r.register(new ShootProjectileAction());
        r.register(new ThrowItemAction());
        r.register(new DamageAction());
        r.register(new DamageNearbyAction());
        r.register(new DamageNoKnockbackAction());
        r.register(new DamagePercentAction());
        r.register(new DisarmAction());
        r.register(new ExplosionAction());
        r.register(new ExtinguishAction());
        r.register(new ForceDropAction());
        r.register(new FreezeAction());
        r.register(new GlowAction());
        r.register(new HealAction());
        r.register(new HealPercentAction());
        r.register(new IgniteAction());
        r.register(new InvulnerabilityAction());
        r.register(new KillAction());
        r.register(new KnockbackAction());
        r.register(new LaunchEntityAction());
        r.register(new LifestealAction());
        r.register(new PullAction());
        r.register(new SetHealthAction());
        r.register(new SetMaxHealthAction());
        r.register(new StealItemAction());
        r.register(new StrikeLightningAction());
        r.register(new TrueDamageAction());
        r.register(new UnfreezeAction());
        // --- movement ---
        r.register(new BlinkAction());
        r.register(new CustomDashAction());
        r.register(new DashBackwardAction());
        r.register(new DashForwardAction());
        r.register(new FireworkBoostAction());
        r.register(new JumpAction());
        r.register(new LeapAction());
        r.register(new PropelAction());
        r.register(new SetFlySpeedAction());
        r.register(new SetGravityAction());
        r.register(new SetPitchAction());
        r.register(new SetRotationAction());
        r.register(new SetWalkSpeedAction());
        r.register(new SetYawAction());
        r.register(new SpinAction());
        r.register(new SpinTargetAction());
        r.register(new SwapPositionsAction());
        r.register(new TeleportAction());
        r.register(new TeleportCursorAction());
        r.register(new TeleportRelativeAction());
        r.register(new ToggleFlightAction());
        r.register(new VelocityAction());
        r.register(new WorldTeleportAction());
        // --- effect ---
        r.register(new ClearEffectsAction());
        r.register(new CopyEffectsAction());
        r.register(new ExhaustionAction());
        r.register(new FeedAction());
        r.register(new OxygenAction());
        r.register(new PotionEffectSelfAction());
        r.register(new RemoveEffectAction());
        r.register(new SaturationAction());
        // --- world ---
        r.register(new AreaBreakAction());
        r.register(new AutoSmeltDropsAction());
        r.register(new BonemealBlockAction());
        r.register(new BreakBlockAction());
        r.register(new BreakBlockNoDropAction());
        r.register(new DrainLiquidAction());
        r.register(new GrowCropAction());
        r.register(new IgniteBlockAction());
        r.register(new OpenDoorAction());
        r.register(new PlaceLiquidAction());
        r.register(new PushButtonAction());
        r.register(new ReplaceNearBlocksAction());
        r.register(new SetBlockAction());
        r.register(new SetPlayerTimeAction());
        r.register(new SetPlayerWeatherAction());
        r.register(new SetTempBlockAction());
        r.register(new SetTimeAction());
        r.register(new SetWeatherAction());
        r.register(new SpawnEntityAction());
        r.register(new SpawnFallingBlockAction());
        r.register(new ToggleLeverAction());
        r.register(new VeinBreakAction());
        // --- player ---
        r.register(new AddEnchantAction());
        r.register(new AddLoreLineAction());
        r.register(new ClearEnchantsAction());
        r.register(new ClearLoreAction());
        r.register(new CloseInventoryAction());
        r.register(new ConsumeItemAction());
        r.register(new DamageItemAction());
        r.register(new DropCustomItemAction());
        r.register(new DropItemAction());
        r.register(new EquipSlotAction());
        r.register(new GiveCustomItemAction());
        r.register(new GiveItemAction());
        r.register(new GiveXpAction());
        r.register(new ModifyDurabilityAction());
        r.register(new OpenChestAction());
        r.register(new OpenEnderchestAction());
        r.register(new OpenWorkbenchAction());
        r.register(new RemoveEnchantAction());
        r.register(new RenameEntityAction());
        r.register(new RepairItemAction());
        r.register(new SetAdultAction());
        r.register(new SetBabyAction());
        r.register(new SetCompassTargetAction());
        r.register(new SetCustomModelDataAction());
        r.register(new SetGamemodeAction());
        r.register(new SetItemCooldownAction());
        r.register(new SetItemLoreAction());
        r.register(new SetItemModelAction());
        r.register(new SetItemNameAction());
        r.register(new SetLevelAction());
        r.register(new SetRespawnPointAction());
        r.register(new ShearEntityAction());
        r.register(new SwapHandsAction());
        r.register(new TakeXpAction());
        r.register(new UnequipSlotAction());
        r.register(new DropXpAction());
        // --- command ---
        r.register(new AddTagAction());
        r.register(new BroadcastAction());
        r.register(new CancelEventAction());
        r.register(new RemoveTagAction());
        r.register(new RunCommandConsoleAction());
        r.register(new RunCommandOpAction());
        r.register(new RunCommandPlayerAction());
        r.register(new SetVariableAction());
        // --- visual ---
        r.register(new FireworkEffectAction());
        r.register(new HurtAnimationAction());
        r.register(new ParticleAction());
        r.register(new ParticleBurstAction());
        r.register(new ParticleCircleAction());
        r.register(new ParticleHelixAction());
        r.register(new ParticleLineAction());
        r.register(new ParticleRingAction());
        r.register(new ParticleSphereAction());
        r.register(new PlaySoundAction());
        r.register(new PlaySoundAllAction());
        r.register(new SendActionbarAction());
        r.register(new SendBlankMessageAction());
        r.register(new SendBossbarAction());
        r.register(new SendCenteredMessageAction());
        r.register(new SendTitleAction());
        r.register(new ShootParticleAction());
        r.register(new SpawnFireworkAction());
        r.register(new StopSoundAction());
        r.register(new SwingHandAction());
        r.register(new SwingOffhandAction());
        r.register(new TotemAnimationAction());

        // --- M4: economy / charges / cooldown ---
        r.register(new GiveMoneyAction());
        r.register(new TakeMoneyAction());
        r.register(new SetMoneyAction());
        r.register(new PayTargetAction());
        r.register(new AddChargesAction());
        r.register(new SetChargesAction());
        r.register(new SetCooldownAction());
    }

    private static void act(Registries r, String id, String category, String display, String desc) {
        r.register(new SimpleActivator(id, category, display, desc));
    }
}
