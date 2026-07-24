# Actions

[← Back to index](README.md) · Related: [Activators](activators.md) ·
[Conditions & Targeters](conditions-targeters.md) · [YAML Reference](yaml-reference.md)

**Actions** are what an ability *does*. They're the last stage of the pipeline
(`activator → conditions → targeter → actions`). The action list runs **top-to-bottom, once
per resolved target**.

There are **~160 actions**. This page lists every one, grouped into buckets, with its key
parameters — plus deeper coverage of the **flow-control** actions that let you nest, delay,
loop and branch.

```yaml
actions:
  - type: damage          # <-- action id
    amount: 6             # <-- params, written flat
    from_caster: true
```

- `type` is required. An unknown `type` skips that action with a warning.
- Params are optional — each has a sensible default. "Caster" = the player holding the item;
  "target" = the current resolved target from the [targeter](conditions-targeters.md#targeters).
- Actions are **fault-isolated**: if one errors it's logged and skipped, and the rest still run.

> **Param names:** the tables below show each action's parameter **keys**. Values follow the
> [parameter types](yaml-reference.md#parameter-types) (numbers, booleans, MiniMessage,
> material/effect/particle/sound names, etc.). Where a param isn't obvious it's noted.

---

## Combat

Damage, healing, status and crowd-control on the target.

| id | Does | Params |
|---|---|---|
| `add_damage` | Adds flat bonus damage to the triggering hit (use under a damage activator). | `amount` |
| `damage` | Deals flat damage to the target. | `amount`, `from_caster` |
| `damage_no_knockback` | Damage without applying knockback. | `amount` |
| `damage_percent` | Damage equal to a % of the target's max health. | `percent`, `from_caster` |
| `true_damage` | Armor-ignoring damage straight to health. | `amount` |
| `damage_nearby` | Damages living entities around the target. | `radius`, `amount`, `hit_caster` |
| `bleed` | Damage over time — a hit every interval. | `damage`, `interval`, `count` |
| `kill` | Instantly kills the target. | — |
| `heal` | Restores health to the target (up to its max). | `amount` |
| `heal_percent` | Restores a % of the target's max health. | `percent` |
| `set_health` | Sets the target's health to an exact value. | `amount` |
| `set_max_health` | Sets the target's max-health attribute. | `amount` |
| `absorption` | Grants golden-apple-style absorption hearts. | `amount`, `duration` |
| `lifesteal` | Heals the caster for a share of damage dealt. | `fraction` |
| `knockback` | Pushes the target away from the caster. | `strength`, `lift` |
| `pull` | Pulls the target toward the caster. `lift` adds upward velocity so a grounded target isn't braked to a stop by friction. | `strength`, `lift` |
| `launch_entity` | Launches the target upward and/or forward. | `up`, `forward` |
| `ignite` | Sets the target on fire. | `seconds` |
| `extinguish` | Puts out fire on the target. | — |
| `freeze` | Freezes the target (powder-snow frost). | `seconds` |
| `unfreeze` | Clears freeze ticks on the target. | — |
| `glow` | Makes the target glow. | `seconds` |
| `invulnerability` | Makes the target immune to damage for a while. | `seconds` |
| `disarm` | Drops the target's held item and empties its hand. | — |
| `force_drop` | Drops the target player's held item on the ground. | — |
| `steal_item` | Takes an item from the target and gives it to the caster. | — |
| `strike_lightning` | Calls lightning on the target (or a cosmetic flash). | `damage`, `damage_caster` |
| `explosion` | Creates an explosion at the target. | `power`, `fire`, `break_blocks` |
| `shoot_projectile` | Fires a flying head-on-armour-stand projectile that damages the first thing it hits, then can trigger the item's projectile-hit abilities. | `head`, `speed`, `range`, `damage`, `hit_radius`, `particle`, `gravity`, `trail_head`, `trail_head_owner` |
| `throw_item` | Throws **this item** as a boomerang: it flies out rendering the item's own model, strikes the first living entity it passes, then homes back to the caster and vanishes. Fires the item's `projectile_hit_entity` ability on impact. | `speed`, `range`, `damage`, `hit_radius`, `spin`, `face`, `pivot`, `particle`, `hit_sound`, `catch_sound`, `sound_pitch` |

```yaml
# On-hit lifesteal vampire blade
- activator: player_hit_entity
  targeter: target
  actions:
    - type: lifesteal
      fraction: 0.3
    - type: damage_percent
      percent: 0.1          # +10% of the mob's max HP
```

> **`strike_lightning` → `damage_caster`:** a real bolt hits everyone nearby, **including the caster**.
> Set `damage_caster: false` to spare the caster while everyone else is still struck; set `damage: false`
> for a purely cosmetic flash that hurts nothing.

### `shoot_projectile` — a projectile that "does stuff" on hit

`shoot_projectile` launches a floating head (an invisible armour stand wearing a mob or player head) that
flies from the caster, trails a particle or head each tick, and deals its `damage` to the first entity it
touches. Its real power is on impact: hitting an entity or block fires the item's own
**[`projectile_hit_entity` / `projectile_hit_block`](activators.md#projectile)** abilities, so a projectile
can run a whole ability on whatever it strikes.

```yaml
# A cursed bolt — the launcher deals no damage; everything happens on hit.
- activator: right_click
  targeter: self          # a right-click launcher must target self (there's no event target)
  cooldown: 0.75
  actions:
    - type: shoot_projectile
      head: wither_skeleton_skull
      speed: 1.0
      range: 40
      damage: 0             # all the effect comes from the hit ability below
      particle: witch
- activator: projectile_hit_entity
  targeter: target          # the entity the projectile struck
  actions:
    - type: damage
      amount: 4
    - type: potion_effect
      effect: wither
      duration: 5
```

### `throw_item` — a returning, model-accurate boomerang

Where `shoot_projectile` throws a *head*, `throw_item` throws **the item itself**. It spawns an item display
carrying a copy of the held stack, so whatever `item-model` the item uses is what flies through the air. It
travels out along your look direction, damages the first living entity within `hit_radius`, then turns and
homes in on wherever you are *now* before vanishing. It cleans itself up on logout, death, or after a hard
tick cap, and its damage is tagged so it can't re-trigger the item's own hit abilities.

Two params depend on **how the model was built**, not on the effect you want:

- **`face`** — which model axis is pointed along the direction of travel (`+x`, `-x`, `+y`, `-y`, `+z`, `-z`).
  A model's "forward" is whatever the modeller made it; if your item flies handle-first or sideways, change
  this. Try `+y` and `-y` first. Because the aim is derived from the direction of travel, the item
  automatically turns to face you on the return leg.
- **`pivot`** — blocks along the model's Y axis to its visual centre. Displays rotate about the entity
  origin, so a model whose geometry sits off-origin will visibly *orbit* that point instead of spinning in
  place. Set this to the offset to spin cleanly. Leave at `0` for centred models.

Because every other action in the ability runs the instant you throw, anything that should happen *later*
has to come from the action itself:

- **`hit_sound`** plays where it strikes, **`catch_sound`** plays on the caster when it lands back in their
  hand (only on a real catch, never when a throw is abandoned), both at `sound_pitch`.
- **On impact it fires the item's own [`projectile_hit_entity`](activators.md#projectile) ability** on
  whatever it struck — the same hook `shoot_projectile` uses. That's where the flare belongs: write the
  impact as a normal ability and it can do anything. Set `damage: 0` to let the hit ability do all the work.

```yaml
# Mjolnir — throw it, strike something, catch it on the way back.
- activator: right_click
  targeter: self          # a right-click launcher must target self (there's no event target)
  cooldown: 6.0
  actions:
    - type: throw_item
      face: '+y'          # this model's head points along +y
      speed: 0.7
      range: 20
      damage: 10
      hit_radius: 1.6
      particle: electric_spark
```

---

## Movement

Moving, dashing, and reorienting the caster (or, for a few, the target).

| id | Does | Params |
|---|---|---|
| `dash_forward` | Dashes the caster forward. | `strength` |
| `dash_backward` | Dashes the caster backward. | `strength` |
| `custom_dash` | Dashes by a vector (facing-relative by default). | `x`, `y`, `z`, `relative` |
| `blink` | Teleports the caster forward a short distance. | `distance` |
| `leap` | Launches the caster toward the target in an arc. | `power` |
| `jump` | Adds upward velocity to the caster. | `strength` |
| `propel` | Pushes the target along the caster's look direction. | `strength` |
| `pull_self` | Flings the **caster** toward the target's location, in one impulse. | `strength`, `arc`, `max_distance`, `min_distance` |
| `grapple` | Grappling hook: throws a hook to the target, then reels — the caster to a block, or a creature to the caster — holding the rope and hook for the whole trip. | `strength`, `launch_speed`, `arc`, `arrive_distance`, `max_ticks`, `tip_model`, `particle`, `points` |
| `implode` | Yanks the target toward a centre (the trigger point or the caster). The inward counterpart of `pull`. | `strength`, `lift`, `center`, `min_distance` |
| `velocity` | Sets or adds to the caster's velocity. | `x`, `y`, `z`, `add` |
| `firework_boost` | Boosts the caster forward, elytra-style. | `strength` |
| `teleport` | Teleports the caster to the target's location. | — |
| `teleport_relative` | Teleports the caster by an offset. | `x`, `y`, `z` |
| `teleport_cursor` | Teleports to the block the caster is looking at. | `range` (uses the cursor block) |
| `world_teleport` | Teleports the caster to a named world's spawn. | `world` |
| `swap_positions` | Swaps the caster and the target's locations. | — |
| `set_walk_speed` | Sets the caster's walking speed. | `speed` |
| `set_fly_speed` | Sets the caster's flying speed. | `speed` |
| `toggle_flight` | Grants or revokes the caster's ability to fly. | `enabled` |
| `set_gravity` | Enables/disables gravity on the target. | `enabled` |
| `set_rotation` | Sets the caster's yaw and pitch. | `yaw`, `pitch` |
| `set_yaw` | Sets the caster's yaw. | `yaw` |
| `set_pitch` | Sets the caster's pitch. | `pitch` |
| `spin` | Spins the caster's yaw cosmetically. | `degrees`, `steps` |
| `spin_target` | Spins the **target's** yaw cosmetically. Reads well on mobs; a player's client owns its own camera, so forcing another player's yaw looks jittery. | `degrees`, `steps` |

```yaml
# Ender-pearl style blink on sneak-right-click
- activator: sneak_right_click
  targeter: self
  cooldown: 3
  actions:
    - type: blink
      distance: 8
    - type: particle
      particle: portal
```

---

## Effects

Potion effects and vitals (hunger, saturation, air).

| id | Does | Params |
|---|---|---|
| `potion_effect` | Applies a potion effect to the **target**. | `effect`, `duration` (seconds), `amplifier` |
| `potion_effect_self` | Applies a potion effect to the **caster**, ignoring the target. | `effect`, `duration`, `amplifier` |
| `remove_effect` | Removes one named effect from the target. | `effect` |
| `clear_effects` | Removes all active effects from the target. | — |
| `copy_effects` | Copies the caster's effects onto the target. | — |
| `feed` | Restores hunger to the target (or caster). | `amount` |
| `saturation` | Adds saturation to the target (or caster). | `amount` |
| `exhaustion` | Adds exhaustion to the target (or caster). | `amount` |
| `oxygen` | Restores air to the target. | `seconds` |

> `duration` on potion effects is in **seconds** (converted to ticks internally); `amplifier`
> is 0-based (`0` = level I).

---

## World / Block

Modify blocks, liquids, time and weather around the target. Block-editing actions respect
protection when the ability opts into [`respect-claims`](gates.md#region).

| id | Does | Params |
|---|---|---|
| `set_block` | Changes the target block to a material. | `material` |
| `set_temp_block` | Sets the target block, then reverts it after a delay. | `material`, `ticks` |
| `break_block` | Breaks the target block, dropping items. | — |
| `break_block_no_drop` | Removes the target block with no drops. | — |
| `area_break` | Breaks every block in a cube around the target. | `size` |
| `vein_break` | Breaks connected same-type blocks from the target. | `limit` |
| `replace_near_blocks` | Replaces one block type with another in a cube. | `from`, `to`, `radius` |
| `spawn_falling_block` | Spawns a falling block above the target. | `material` |
| `grow_crop` | Instantly grows the target crop to maturity. | — |
| `bonemeal_block` | Applies bone meal to the target block. | — |
| `ignite_block` | Sets fire above the target block. | — |
| `open_door` | Opens/closes a door, trapdoor or gate. | `open` |
| `toggle_lever` | Flips the target block's powered state. | — |
| `push_button` | Powers the target block briefly, then releases. | — |
| `place_liquid` | Places a liquid at the target block. | `liquid` |
| `drain_liquid` | Removes water/lava within a cube of the target. | `radius` |
| `auto_smelt_drops` | Drops the smelted result instead of the raw block. | — |
| `spawn_entity` | Spawns entities at the target location. | `entity`, `count` |
| `set_time` | Sets the caster's world time-of-day. | `time` |
| `set_player_time` | Overrides the caster's personal time view. | `time` |
| `set_weather` | Changes world weather (clear/rain/thunder). | `weather` |
| `set_player_weather` | Overrides the caster's personal weather view. | `weather` |

---

## Player

Act on the caster, the triggering item, or the target entity's state.

| id                      | Does                                                                   | Params |
|-------------------------|------------------------------------------------------------------------|---|
| `message`               | Sends a MiniMessage chat message (to target if a player, else caster). | `text` |
| `give_item`             | Gives the caster a vanilla item.                                       | `material`, `amount` |
| `give_custom_item`      | Gives the caster another ItemSmith item.                               | `item`, `amount` |
| `drop_item`             | Drops a vanilla item at the caster's feet.                             | `material`, `amount` |
| `drop_custom_item`      | Drops another ItemSmith item at the caster's feet.                     | `item`, `amount` |
| `consume_item`          | Removes a vanilla item from the caster's inventory.                    | `material`, `amount` |
| `give_xp`               | Grants the caster XP points.                                           | `amount` |
| `drop_xp`               | Drops XP points at the targets location.                               | `amount` |
| `take_xp`               | Removes XP points from the caster.                                     | `amount` |
| `set_level`             | Sets the caster's XP level.                                            | `level` |
| `set_gamemode`          | Switches the caster's game mode.                                       | `mode` |
| `set_respawn_point`     | Sets the caster's respawn to their location.                           | — |
| `set_compass_target`    | Points the caster's compass at the target.                             | — |
| `open_chest`            | Opens a fresh 27-slot chest for the caster.                            | `title` |
| `open_enderchest`       | Opens the caster's ender chest.                                        | — |
| `open_workbench`        | Opens a virtual crafting table.                                        | — |
| `close_inventory`       | Closes the caster's open inventory.                                    | — |
| `swap_hands`            | Swaps main-hand and off-hand items.                                    | — |
| `equip_slot`            | Puts the triggering item into an armor slot.                           | `slot` |
| `unequip_slot`          | Clears an armor slot on the caster.                                    | `slot` |
| `set_item_name`         | Renames the triggering item.                                           | `name` |
| `set_item_lore`         | Replaces the triggering item's lore.                                   | `lore` |
| `add_lore_line`         | Appends a line to the triggering item's lore.                          | `line` |
| `clear_lore`            | Clears the triggering item's lore.                                     | — |
| `set_item_model`        | Sets the item's resource-pack model key.                               | `model` |
| `set_custom_model_data` | Sets the item's custom model data.                                     | `value` |
| `add_enchant`           | Adds an enchantment to the triggering item.                            | `enchant`, `level` |
| `remove_enchant`        | Removes an enchantment from the item.                                  | `enchant` |
| `clear_enchants`        | Removes all enchantments from the item.                                | — |
| `repair_item`           | Fully repairs the triggering item.                                     | — |
| `damage_item`           | Adds damage to the triggering item.                                    | `amount` |
| `modify_durability`     | Adjusts the item's damage by a relative amount.                        | `amount` |
| `set_item_cooldown`     | Puts a material on the caster's native cooldown.                       | `material`, `ticks` |
| `rename_entity`         | Sets a visible custom name on the target.                              | `name` |
| `set_baby`              | Turns the target into a baby.                                          | — |
| `set_adult`             | Turns the target into an adult.                                        | — |
| `shear_entity`          | Shears the target sheep.                                               | — |

---

## Command

Run commands, broadcast, tag, set variables, cancel the event.

| id | Does | Params |
|---|---|---|
| `run_command_player` | Runs a command **as the caster** (their perms). | `command` |
| `run_command_op` | Runs a command as the caster, temporarily op'd. | `command` |
| `run_command_console` | Runs a command **as console**. | `command` |
| `broadcast` | Broadcasts a message to the whole server. | `text` |
| `console_log` | Logs a line to the server console. | `text` |
| `add_tag` | Adds a scoreboard tag to the target. | `tag` |
| `remove_tag` | Removes a scoreboard tag from the target. | `tag` |
| `set_variable` | Stores a value in the ability's scratch variables. | `key`, `value` |
| `cancel_event` | Cancels the triggering event. | — |

> Command actions support the `{player}` and `{item}` placeholders (the caster's name and the
> item id). No leading slash on `command`.

> **`{player}` and unusual names.** A command using `{player}` is skipped if the caster's name isn't a
> plain `A-Z a-z 0-9 _` token. On online-mode servers every name already qualifies, so nothing changes.
> On offline-mode or Bedrock-via-Floodgate servers a player picks their own name, and a name containing
> spaces or punctuation spliced into a dispatched command could inject extra arguments — so the action
> refuses to run rather than dispatch something unintended. Commands that don't use `{player}` are
> unaffected.

```yaml
# A "recall" wand that sends you home
- activator: sneak_right_click
  targeter: self
  cooldown: 30
  actions:
    - type: run_command_player
      command: "spawn"
    - type: send_title
      title: "<gold>Recalled</gold>"
```

---

## Sound / Visual

Particles, sounds, titles, action bars, boss bars, fireworks and animations.

| id | Does | Params |
|---|---|---|
| `particle` | A puff of particles at the target. | `particle`, `count`, `spread`, `speed` |
| `particle_burst` | A dense burst at the target. | `particle`, `count`, `spread`, `speed` |
| `particle_ring` | A horizontal ring around the target. | `particle`, `radius`, `points` |
| `particle_circle` | A filled horizontal disc around the target. | `particle`, `radius`, `points` |
| `particle_sphere` | A hollow sphere around the target. | `particle`, `radius`, `points` |
| `particle_helix` | A rising helix from the target. | `particle`, `radius`, `height`, `points`, `turns` |
| `particle_line` | A line from the caster's eyes to the target. | `particle`, `points` |
| `shoot_particle` | A stream in the caster's look direction. | `particle`, `distance`, `points` |
| `play_sound` | Plays a sound at the target's location. | `sound`, `volume`, `pitch` |
| `play_sound_all` | Plays a sound at the caster, heard by everyone nearby. | `sound`, `volume`, `pitch` |
| `stop_sound` | Stops all sounds playing to the caster. | — |
| `send_title` | Shows a title/subtitle to the caster. | `title`, `subtitle` |
| `send_actionbar` | Shows an action-bar message to the caster. | `text` |
| `send_bossbar` | Shows a temporary boss bar to the caster. | `text`, `color`, `seconds` |
| `send_centered_message` | Sends a roughly centered chat line. | `text` |
| `send_blank_message` | Sends one or more empty chat lines. | `count` |
| `firework_effect` | A colored firework flash at the target. | `color`, `shape` |
| `spawn_firework` | Launches a firework rocket from the target. | `color`, `shape`, `power` |
| `hurt_animation` | Plays the hurt animation/sound on the target. | — |
| `totem_animation` | Plays the totem resurrection animation. | — |
| `swing_hand` | Plays the caster's main-hand swing. | — |
| `swing_offhand` | Plays the caster's off-hand swing. | — |

```yaml
# Pure cosmetic aura while held
- activator: hold_tick
  targeter: self
  cooldown: 0.5
  actions:
    - type: particle_helix
      particle: end_rod
      radius: 0.6
      height: 2.2
      turns: 2
```

> **Heads instead of particles:** every particle action above (and `shoot_projectile`'s trail) accepts an
> optional **`head`** param. Set it to a mob head or `player_head` and the effect renders **floating heads**
> instead of the particle. For `player_head`, set **`head_owner`** to the player name whose skin to use.
> Leave `head` blank to use the particle. In the GUI this is a heads-only picker with a **"None (use
> particle)"** option. See the [`HEAD` parameter type](yaml-reference.md#parameter-types).

```yaml
# A ring of floating creeper heads instead of a flame ring
- type: particle_ring
  head: creeper_head
  radius: 2
  points: 10
```

---

## Economy & Resources

Money (needs [Vault + economy](integrations.md#vault)), item charges, and named cooldowns.

| id | Does | Params |
|---|---|---|
| `give_money` | Deposits Vault money to the caster. | `amount` |
| `take_money` | Withdraws Vault money from the caster. | `amount` |
| `set_money` | Sets the caster's Vault balance. | `amount` |
| `pay_target` | Transfers money from caster to the target player. | `amount` |
| `add_charges` | Adds charges to the trigger item, up to its max. | `amount` |
| `set_charges` | Sets the trigger item's charge counter. | `amount` |
| `set_cooldown` | Starts a named cooldown on the caster. | `key`, `seconds` |
| `set_stat` | Sets a persistent [stat](yaml-reference.md#stats) on the trigger item (number or text). | `name`, `value` |
| `add_stat` | Adds to a numeric stat on the trigger item (negative subtracts). | `name`, `amount` |
| `multiply_stat` | Multiplies a numeric stat by a factor. | `name`, `factor` |
| `reset_stat` | Resets a stat to its declared initial value. | `name` |

See [Gates → Charges](gates.md#charges) and [Gates → Cooldowns](gates.md#cooldowns) for how
charges and named cooldowns interact with the gate.

### Persistent stats — leveling & evolving items

`stat`s are named values that live **on the individual item** and survive across uses, drops and
restarts (declared in a [`stats:`](yaml-reference.md#stats) block). Read them with the
`stat_above` / `stat_below` / `stat_equals` / `stat_between` [conditions](conditions-targeters.md#stats),
change them with `set_stat` / `add_stat` / `multiply_stat` / `reset_stat`, and show them in lore with
a `<stat:name>` token.

**Stats can drive the numbers, not just gate abilities.** Any param may embed a `<stat:name>` token,
resolved to the item's live value when the action runs — so effects *scale* with the item:

```yaml
- type: damage
  amount: '<stat:power>'      # deals damage equal to the item's current 'power' stat
- type: heal
  amount: '<stat:level>*2'    # simple trailing scale (* / + -) is supported too
```

A token resolves to `0` when the stat is unset or the ability has no item stack (e.g.
`projectile_hit*`), so numeric reads never throw. Tokens work in action **and** condition params (not
in targeter/activator params).

**Threshold hook.** The [`stat_reached`](activators.md#stat_reached) activator fires an ability *once*
the tick a stat rises across a `value`, so level-up/evolution logic is written in one place instead of
repeated across abilities.

Evolution needs **no special mechanism** — because abilities already take `conditions:`, gating a
stronger ability behind a stat threshold *is* the evolution. Count uses with `add_stat`, unlock
abilities with `stat_above`, and rename tiers with the existing `set_item_name`:

```yaml
stats:
  level: 1
  uses: 0
abilities:
  # Count every use.
  - activator: right_click
    targeter: self
    actions:
      - type: add_stat
        name: uses
        amount: 1
  # Every 5th use: level up and rename. Gated so it only fires on the threshold.
  - activator: right_click
    targeter: self
    conditions:
      - type: stat_equals
        name: uses
        value: '5'
    actions:
      - type: set_stat
        name: uses
        value: '0'
      - type: add_stat
        name: level
        amount: 1
      - type: set_item_name
        name: "<gold>Artifact Ring II</gold>"
  # The evolved power: only unlocks once level > 2.
  - activator: right_click
    targeter: self
    conditions:
      - type: stat_above
        name: level
        amount: 2
    actions:
      - type: dash_forward
        strength: 1.4
```

> Stats need the item's stack, so they work on hit/click/tick activators but **not** on
> `projectile_hit*` (those fire with no stack). Put stats on unstackable items — a stat lives per
> stack, so a stacked item would share one value.

---

## Flow control

Five **flow actions** turn a flat list into a program: they wait, loop, branch and randomize by
nesting *other* actions inside body keys. Two more (`abort`, `nothing`) are terminal helpers.
Flow actions can nest to any depth — an `if` inside a `repeat` inside a `random` is fine.

### `delay` — wait, then continue

`delay` waits a number of **ticks** (20 ticks = 1 second), then the *following* actions run.
This is how you build timed combos.

```yaml
actions:
  - type: teleport
  - type: delay
    ticks: 10          # half a second later...
  - type: explosion
    power: 2
```

### `repeat` — loop a body N times

Runs its `do` body `times` times, sequentially — each iteration (including any inner `delay`)
finishes before the next starts.

```yaml
- type: repeat
  times: 5
  do:
    - type: particle_ring
      particle: flame
    - type: delay
      ticks: 3          # rings 3 ticks apart, not all at once
```

### `if` — branch on conditions

Runs `then` when its own `conditions:` all pass, otherwise `else`. The conditions see the
**current resolved target**, so inside an area targeter an `if` filters per-entity.

```yaml
- type: if
  conditions:
    - type: target_is_player
  then:
    - type: message
      text: "<red>PvP!</red>"
  else:
    - type: damage
      amount: 2
```

### `chance` — probability sugar

Runs its `do` body a fraction of the time (`chance` is 0–1), then continues either way. Sugar
for "N% chance to do X".

```yaml
- type: chance
  chance: 0.25          # 25% of the time
  do:
    - type: strike_lightning
```

### `random` — weighted branches

Picks one of several weighted `branches` and runs its `do`. Higher `weight` = more likely. A
branch with an empty body (or a `nothing` action) is the classic "chance to do nothing".

```yaml
- type: random
  branches:
    - weight: 5         # ~62.5%  (5 of 8)
      do:
        - type: heal
          amount: 4
    - weight: 2         # ~25%
      do:
        - type: potion_effect_self
          effect: strength
          duration: 8
    - weight: 1         # ~12.5% — do nothing
      do:
        - type: nothing
```

### `abort` and `nothing`

| id | Does |
|---|---|
| `abort` | Stops the rest of this ability's actions from running (bail out early inside an `if`/`chance` branch). |
| `nothing` | A no-op placeholder — useful as an empty `random` branch. |

---

## See also

- [Conditions & Targeters](conditions-targeters.md) — gate *when* and choose *who*.
- [Gates & Economics](gates.md) — costs, cooldowns, permissions, charges.
- [YAML Reference → Flow-control nesting](yaml-reference.md#flow-control-nesting).
