# Activators

[← Back to index](README.md) · Related: [Actions](actions.md) ·
[Conditions & Targeters](conditions-targeters.md) · [YAML Reference](yaml-reference.md)

An **activator** is the *trigger* of an ability — the event that makes it fire. It's the first
stage of the pipeline (`activator → conditions → targeter → actions`). Every activator listed
here is backed by a real event handler, so there are no "dead" triggers.

There are **150 activators**. Bind one by setting `activator:` on an ability:

```yaml
abilities:
  - activator: right_click     # <-- the trigger id
    targeter: self
    actions:
      - type: potion_effect_self
        effect: speed
        duration: 10
```

Activators take **no parameters** — you just write the id.

## The "natural target"

Many triggers come with a natural target — the thing the event acted on. The default
[`target` targeter](conditions-targeters.md#targeters) picks it up:

- **Entity** triggers (`player_hit_entity`, `click_entity`, `projectile_hit_entity`, …) → the
  entity.
- **Block** triggers (`block_break`, `right_click_block`, `harvest_block`, …) → the block.
- Triggers with no natural target (a click in the air, `hold_tick`, `join`, …) resolve to
  *nothing* under the `target` targeter, so those abilities should use a different targeter
  such as `self`, `radius`, or `looking_at_entity`.

Ability-level [conditions](conditions-targeters.md) see this natural target too.

> **Performance:** high-frequency triggers — `move`, `input`, and the tick activators
> (`hold_tick`, `inventory_tick`, `equip_tick`) — fire very often. Always gate them with a
> `cooldown`, conditions, or a `cooldown-group` so they don't run every tick.

---

## Interact — clicks & inventory

| id | Fires when |
|---|---|
| `right_click` | Right-click with the item (air or block). |
| `right_click_air` | Right-click aiming at nothing. |
| `right_click_block` | Right-click a block (block is the target). |
| `left_click` | Left-click with the item (air or block). |
| `left_click_air` | Left-click aiming at nothing. |
| `left_click_block` | Left-click a block (block is the target). |
| `any_click` | Either mouse button. |
| `sneak_right_click` | Right-click while sneaking. |
| `sneak_left_click` | Left-click while sneaking. |
| `sneak_right_click_block` | Sneak-right-click a block. |
| `sneak_left_click_block` | Sneak-left-click a block. |
| `arm_swing` | Any hand-swing animation. |
| `click_entity` | Right-click a mob (entity is the target). |
| `click_player` | Right-click another player. |
| `sneak_click_entity` | Sneak-right-click a mob. |
| `inventory_click` | Click the item inside an inventory GUI. |
| `inventory_drag` | Drag the item across inventory slots. |
| `open_inventory` | Open a container/inventory. |
| `close_inventory` | Close a container/inventory. |

## Combat — attack, defense, damage causes, effects

| id | Fires when |
|---|---|
| `player_hit_entity` | You strike an entity in melee. |
| `player_hit_player` | You strike another player. |
| `player_kill_entity` | You kill an entity. |
| `player_kill_player` | You kill another player. |
| `player_take_damage` | You take damage from any source. |
| `player_take_damage_by_entity` | You take damage from a mob. |
| `player_take_damage_by_player` | You take damage from a player. |
| `player_take_damage_by_projectile` | A projectile hits you. |
| `player_block_hit` | You block a hit with a shield. |
| `player_targeted` | A mob starts targeting you. |
| `take_fall_damage` | You take fall damage. |
| `take_fire_damage` | You take fire (direct) damage. |
| `take_fire_tick_damage` | You take burning fire-tick damage. |
| `take_lava_damage` | You take lava damage. |
| `take_drown_damage` | You take drowning damage. |
| `take_explosion_damage` | You take explosion damage. |
| `take_void_damage` | You take void damage. |
| `take_lightning_damage` | Lightning strikes you. |
| `take_magic_damage` | You take magic/potion damage. |
| `take_wither_damage` | You take wither damage. |
| `take_thorns_damage` | Thorns armor hits you. |
| `take_freeze_damage` | Powder snow freezes you. |
| `take_suffocation_damage` | You suffocate in a block. |
| `take_contact_damage` | A cactus/berry bush hurts you. |
| `take_starvation_damage` | You take starvation damage. |
| `take_sonic_boom_damage` | A warden's sonic boom hits you. |
| `take_dragon_breath_damage` | Dragon breath hurts you. |
| `player_receive_effect` | You gain a potion effect. |
| `player_effect_expire` | One of your effects ends. |

## Player — death, health, XP, session

| id | Fires when |
|---|---|
| `player_death` | You die holding the item. |
| `player_respawn` | You respawn. |
| `regain_health` | You regain health. |
| `experience_change` | Your experience changes. |
| `level_up` | Your XP level increases. |
| `level_down` | Your XP level decreases. |
| `food_change` | Your food level changes. |
| `totem_use` | A totem saves you from death. |
| `join` | You log in with the item in your inventory. |
| `quit` | You log out holding the item. |
| `kick` | You are kicked holding the item. |
| `change_world` | You switch worlds. |
| `portal` | You travel through a portal. |
| `gamemode_change` | Your game mode changes. |
| `command` | You run a chat command. |
| `chat` | You send a chat message. |

## Block / world

| id | Fires when |
|---|---|
| `block_break` | You break a block with the item. |
| `block_place` | You place the item as a block. |
| `block_damage_start` | You begin breaking a block. |
| `block_damage_stop` | You stop breaking a block. |
| `harvest_block` | You harvest a mature crop. |
| `fertilize_block` | You bone-meal/fertilize a block. |
| `bucket_fill` | You fill a bucket. |
| `bucket_empty` | You empty a bucket. |
| `bucket_entity` | You bucket a mob (fish/axolotl). |
| `shear_entity` | You shear a sheep/mooshroom. |
| `armor_stand_manipulate` | You manipulate an armor stand. |
| `item_frame_change` | You place/rotate/remove an item frame item. |
| `ring_bell` | You ring a bell. |
| `sign_edit` | You finish editing a sign. |

## Projectile

| id | Fires when |
|---|---|
| `projectile_launch` | You fire a bow/crossbow/trident. |
| `projectile_launch_bow` | You fire specifically a bow. |
| `projectile_launch_crossbow` | You fire specifically a crossbow. |
| `projectile_launch_trident` | You throw a trident. |
| `ready_arrow` | You start drawing a bow or loading a crossbow. |
| `crossbow_load` | A crossbow finishes charging a projectile. |
| `projectile_throw` | You throw the item (snowball/potion/pearl). |
| `projectile_hit` | A projectile you fired lands anywhere. |
| `projectile_hit_block` | Your projectile hits a block. |
| `projectile_hit_entity` | Your projectile hits a mob. |
| `projectile_hit_player` | Your projectile hits a player. |
| `projectile_enter_liquid` | Your projectile hits water/lava. |
| `riptide` | You riptide with a trident. |
| `egg_throw` | You throw an egg. |

> **Custom projectiles fire these too.** The [`shoot_projectile`](actions.md#combat) action launches an
> ItemSmith projectile (a floating head) that triggers `projectile_hit_entity` / `projectile_hit_block` on
> impact — so these hit activators work whether the projectile is a vanilla arrow or a custom one. Under
> `projectile_hit_entity` the [`target`](conditions-targeters.md#targeters) targeter is the entity that was hit.

## Movement / state

| id | Fires when |
|---|---|
| `jump` | You jump. |
| `move` | You move into a new block *(heavy — gate it)*. |
| `sneak` | You start sneaking. |
| `unsneak` | You stop sneaking. |
| `sprint` | You start sprinting. |
| `unsprint` | You stop sprinting. |
| `fly_start` | You start flying. |
| `fly_stop` | You stop flying. |
| `glide_start` | You start elytra gliding. |
| `glide_stop` | You stop elytra gliding. |
| `swim_start` | You start swimming. |
| `swim_stop` | You stop swimming. |
| `teleport` | You teleport. |
| `mount` | You mount an entity. |
| `dismount` | You dismount an entity. |
| `bed_enter` | You get into a bed. |
| `bed_leave` | You get out of a bed. |
| `elytra_boost` | You boost with a firework while gliding. |
| `input` | Your movement-key input changes *(heavy)*. |
| `pose_change` | Your body pose changes. |

## Item lifecycle

| id | Fires when |
|---|---|
| `item_durability_damage` | The item loses durability. |
| `item_break` | The item breaks from durability. |
| `item_mend` | The item is repaired by Mending. |
| `item_hold` | The item becomes your selected hotbar slot. |
| `item_unhold` | You switch away from the item. |
| `equip` | You equip the item as armor. |
| `unequip` | You remove the armor item. |
| `swap_hand` | You swap the item between hands. |
| `item_drop` | You drop the item. |
| `item_pickup` | You pick the item up. |
| `item_consume` | You finish eating/drinking the item. |
| `stop_using_item` | You release a charging item (bow/food). |
| `book_edit` | You save edits to a writable book (without signing). |
| `book_sign` | You sign a book into a written book. |
| `pickup_projectile` | You pick up any projectile (arrow/trident/etc). |
| `pickup_arrow` | You pick up an arrow. |
| `pickup_trident` | You pick up a landed trident. |
| `pickup_spectral_arrow` | You pick up a spectral arrow. |

## Entity interactions

| id | Fires when |
|---|---|
| `feed_entity` | You feed an animal breeding food (it enters love mode). |
| `breed_entity` | You breed two animals. |
| `tame_entity` | You tame an animal. |
| `name_entity` | You name a mob with a name tag. |
| `leash_entity` | You leash a mob. |
| `trade` | You complete a villager trade. |

## Crafting / stations

| id | Fires when |
|---|---|
| `craft_item` | The item is crafted. |
| `enchant_item` | The item is enchanted at a table. |
| `smith_item` | The item is produced at a smithing table. |
| `recipe_discover` | You unlock a recipe. |
| `advancement` | You earn an advancement. |

## Fishing

| id | Fires when |
|---|---|
| `fish_cast` | You cast the fishing rod. |
| `fish_bite` | A fish bites your line. |
| `fish_catch` | You catch a fish/item. |
| `fish_catch_entity` | Your rod hooks an entity. |
| `fish_in_ground` | Your bobber lands on a block. |
| `fish_reel` | You reel in with no catch. |

## Lifecycle — passive ticks

These repeat while a condition holds. **Always throttle them** with `cooldown` and/or
conditions.

| id | Fires |
|---|---|
| `hold_tick` | Repeatedly while the item is in your main hand. |
| `inventory_tick` | Repeatedly while the item is anywhere in your inventory. |
| `equip_tick` | Repeatedly while the item is worn as armor. |

---

## Tips

- One item can bind **many** abilities to different (or the same) activators — e.g. a weapon
  that does one thing on `player_hit_entity` and another on `sneak_right_click`.
- To re-fire safely, note the engine has a **re-entrancy guard**: an action that deals
  caster-attributed damage won't loop back and re-trigger your own `player_hit_entity` ability.
- Pair a trigger with the right [targeter](conditions-targeters.md#targeters): use `target`
  for entity/block triggers, `self` for self-buffs, and area targeters for AoE.
