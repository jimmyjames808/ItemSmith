# Conditions & Targeters

[← Back to index](README.md) · Related: [Activators](activators.md) · [Actions](actions.md) ·
[YAML Reference](yaml-reference.md)

Two stages of the pipeline live here:

- **Conditions** — the `if`. Pass/fail checks that decide whether an ability fires.
- **Targeters** — the `who`. They resolve the list of targets the actions run against.

---

# Conditions

There are **~70 conditions**. Add them under an ability's `conditions:` list; **all must pass**
(logical AND) for the ability to fire. An empty/absent list means "always fires".

```yaml
conditions:
  - type: is_sneaking            # (required) condition id
  - type: health_below
    value: 6                     # condition params, flat
  - type: target_is_player
    invert: true                 # reserved flag — negate this check
```

## The `invert` flag

`invert: true` is a **reserved** key (not a condition parameter). It wraps *any* condition so
the ability reads the opposite result:

```yaml
- type: is_in_water
  invert: true        # passes when the caster is NOT in water
```

## "Caster" vs "target" conditions

Most conditions check the **caster** (the holding player). A family of `target_*` /
`entity_type_is` / `is_baby` / `is_tamed` conditions check the **current target** instead —
these fail closed (deny) when there's no entity target. Inside an `if` action within an area
targeter, target conditions are evaluated **per resolved entity**.

---

### Player state (caster)

| id | Passes while the caster… | Params |
|---|---|---|
| `is_sneaking` | is crouching | — |
| `is_sprinting` | is sprinting | — |
| `is_swimming` | is swimming | — |
| `is_flying` | is flying | — |
| `is_gliding` | is gliding with an elytra | — |
| `is_blocking` | is blocking with a raised shield | — |
| `is_on_ground` | is standing on the ground | — |
| `is_in_water` | is in water | — |
| `is_in_lava` | is standing in lava | — |
| `is_burning` | is on fire | — |
| `is_sleeping` | is sleeping in a bed | — |

### Health & vitals (caster)

| id | Passes while… | Params |
|---|---|---|
| `health_above` | health is over a threshold | `value` |
| `health_below` | health is under a threshold | `value` |
| `health_percent_above` | health is over a % of max | `percent` |
| `health_percent_below` | health is under a % of max | `percent` |
| `absorption_above` | absorption hearts are over a threshold | `value` |
| `food_above` | hunger is over a threshold | `value` |
| `food_below` | hunger is under a threshold | `value` |
| `air_above` | remaining air is over a threshold | `ticks` |
| `xp_level_above` | XP level is over a threshold | `level` |
| `xp_level_below` | XP level is under a threshold | `level` |

### Identity (caster)

| id | Passes while… | Params |
|---|---|---|
| `has_permission` | the caster holds a permission node | `permission` |
| `is_op` | the caster is a server operator | — |
| `name_is` | the caster's name matches | `name` |
| `gamemode_is` | the caster is in a game mode | `mode` |
| `has_potion_effect` | the caster has an effect at/above an amplifier | `effect`, `min_amplifier` |

### Item (triggering item / caster's hand)

| id | Passes while… | Params |
|---|---|---|
| `holding_item` | the caster holds a material in main hand | `material` |
| `wearing` | the caster wears a material in an armor slot | `material`, `slot` |
| `in_slot` | the selected hotbar slot matches | `slot` |
| `item_name_contains` | the item's display name contains a substring | `text` |
| `has_enchant` | the item has an enchant at/above a level | `enchant`, `min_level` |
| `durability_above` | remaining durability is above a value | `value` |
| `durability_below` | remaining durability is below a value | `value` |
| `durability_percent_below` | durability % is below a value | `percent` |

### Charges & cooldown

| id | Passes when… | Params |
|---|---|---|
| `charges_above` | the item has more than N charges | `amount` |
| `charges_below` | the item has fewer than N charges | `amount` |
| `cooldown_ready` | a named cooldown has elapsed | `key`, `seconds` |

### Stats (persistent item state)

Read a named [stat](yaml-reference.md#stats) off the triggering item. Put one of these in an
ability's `conditions:` to gate it behind a threshold — that's how leveling/evolving items work
(see the [worked example](actions.md#persistent-stats--leveling--evolving-items)). Numeric compares
read a non-numeric/unset stat as 0; `stat_equals` compares as text (so it also handles string stats).

| id | Passes when… | Params |
|---|---|---|
| `stat_above` | a numeric stat is greater than `amount` | `name`, `amount` |
| `stat_below` | a numeric stat is less than `amount` | `name`, `amount` |
| `stat_between` | a numeric stat is within `min`..`max` (inclusive) | `name`, `min`, `max` |
| `stat_equals` | a stat equals `value` (text compare) | `name`, `value` |

### Economy (needs [Vault](integrations.md#vault))

| id | Passes when the caster's balance is… | Params |
|---|---|---|
| `has_money` | at least the amount | `amount` |
| `balance_above` | above the amount | `amount` |
| `balance_below` | below the amount | `amount` |

### Time (caster's world)

| id | Passes while… | Params |
|---|---|---|
| `is_day` | it is daytime | — |
| `is_night` | it is nighttime | — |
| `time_of_day` | the world time is within a tick range | `min`, `max` |
| `moon_phase` | the world is on a moon phase (0–7) | `phase` |

### World & environment (caster)

| id | Passes when… | Params |
|---|---|---|
| `world_is` | the caster is in the named world | `world` |
| `dimension_is` | the caster is in the chosen dimension | `dimension` |
| `biome_is` | the caster stands in the biome | `biome` |
| `y_above` | the caster is above a height | `value` |
| `y_below` | the caster is below a height | `value` |
| `light_above` | light level at the caster is above a threshold | `level` |
| `light_below` | light level at the caster is below a threshold | `level` |
| `can_see_sky` | there is open sky directly above | — |
| `is_raining` | it is raining | — |
| `is_thundering` | a thunderstorm is active | — |
| `weather_is` | the world's weather matches a state | `weather` |
| `block_at` | the block at a chosen location matches | `material`, `where` |

### Region (needs [WorldGuard](integrations.md#worldguard))

| id | Passes when… | Params |
|---|---|---|
| `in_region` | the caster is inside a WorldGuard region | `region` |
| `is_region_member` | the caster owns or is a member of the region | `region` |
| `can_build` | the caster may build where they stand | — |

### Target (the current target entity)

| id | Passes when the target… | Params |
|---|---|---|
| `target_is_living` | is a living entity | — |
| `target_is_mob` | is a hostile mob | — |
| `target_is_player` | is a player | — |
| `target_is_on_fire` | is burning | — |
| `entity_type_is` | matches an entity type | `entity_type` |
| `is_baby` | is a baby entity | — |
| `is_tamed` | is a tamed entity | — |
| `target_health_above` | health is above a value | `value` |
| `target_health_below` | health is below a value | `value` |
| `target_health_percent_below` | health % is below a value | `percent` |
| `target_has_effect` | has a given potion effect | `effect` |
| `target_distance_below` | is within a distance of the caster | `distance` |
| `target_name_contains` | its name contains text | `text` |

### Meta

| id | Passes… | Params |
|---|---|---|
| `chance` | a configurable fraction of the time (0–1) | `chance` |

```yaml
# Only crit sneaking mobs below half health, 30% of the time
conditions:
  - type: is_sneaking
  - type: target_health_percent_below
    percent: 0.5
  - type: chance
    chance: 0.30
```

---

# Targeters

There are **~18 targeters**. The targeter resolves *which* targets the actions run against — the
action list runs **once per resolved target**. Set it with `targeter:` on the ability; the
default is `target`.

```yaml
targeter: self                 # bare string (no params)
```

```yaml
targeter:                      # section form (with params)
  type: radius
  radius: 6
  living_only: true
```

A targeter can return a single entity, a single block/location, several entities, or several
points in space. Actions that need a living entity no-op on point/block targets, and vice
versa — so match the targeter to the actions.

| id | Returns | Params |
|---|---|---|
| `target` | The trigger's natural target (the mob you hit, the block you broke). Empty for air clicks. | — |
| `self` | The caster (the holding player). | — |
| `radius` | All entities within a radius of the centre. | `radius`, `living_only`, `include_self`, `relative_to` |
| `nearby_entities` | All entities within a radius, up to a max. | `radius`, `max`, `relative_to` |
| `nearby_players` | All other players within a radius. | `radius`, `relative_to` |
| `nearby_monsters` | All hostile monsters within a radius. | `radius`, `relative_to` |
| `nearest_entity` | The single closest entity within a radius. | `radius`, `living_only`, `relative_to` |
| `nearest_player` | The single closest other player within a radius. | `radius`, `relative_to` |
| `looking_at_entity` | The entity the caster is directly looking at. | `range` |
| `looking_at_block` | The block the caster is directly looking at. | `range` |
| `looking_at` | The entity in the caster's crosshair if there is one, else the block behind it. Picks exactly one, so a single ability can behave differently for a mob vs a block. | `range` |
| `looking_direction` | A point a set distance ahead of the caster's eyes. | `distance` |
| `cone` | Entities in a cone in front of the caster. | `range`, `angle`, `living_only` |
| `line` | Points sampled along the caster's line of sight. | `distance`, `step` |
| `ring` | A ring of evenly-spaced points around the caster or target. | `radius`, `points`, `relative_to` |
| `offset` | A single point offset from the caster or target. | `dx`, `dy`, `dz`, `relative_to`, `space` |
| `location_of_self` | The caster's own location. | — |
| `location_of_target` | The location of the trigger's natural target. | — |
| `block_below_target` | The block directly beneath the target (or the caster). | — |

> **`relative_to: self | target`** — the area and ring/offset targeters take this. `self` (the
> default) centres on the caster; `target` centres on the trigger's target — the mob you hit, the
> block a projectile struck, etc. Use `target` for anything that should affect the area around an
> *impact* rather than around the thrower. When the trigger had no target (e.g. an air click), a
> `target`-relative targeter returns nothing.

### Examples

```yaml
# AoE nova: hit everything within 6 blocks when you sneak-right-click
- activator: sneak_right_click
  targeter:
    type: radius
    radius: 6
    living_only: true
    include_self: false
  actions:
    - type: damage
      amount: 4
    - type: knockback
      strength: 1.2
```

```yaml
# Ranged zap: strike whatever you're looking at up to 30 blocks away
- activator: right_click
  targeter:
    type: looking_at_entity
    range: 30
  cooldown: 1.5
  actions:
    - type: strike_lightning
```

```yaml
# Cone breath weapon
- activator: right_click
  targeter:
    type: cone
    range: 6
    angle: 45
    living_only: true
  actions:
    - type: ignite
      seconds: 3
    - type: particle
      particle: flame
```

> **Point vs entity targeters:** `looking_direction`, `line`, `ring`, `offset`,
> `location_of_*` and `block_below_target` resolve to **locations/blocks**, not entities —
> perfect for `set_block`, `spawn_entity`, `particle`, `strike_lightning`, `explosion`, etc.

---

## See also

- [Actions](actions.md) — including the `if` flow action, which reuses this condition library
  per-target.
- [Gates & Economics](gates.md) — permission/region/cost checks that gate an ability *before*
  conditions even matter.
