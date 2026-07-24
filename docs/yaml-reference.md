# YAML Reference

[← Back to index](README.md)

This is the complete schema for a per-item file, `plugins/ItemSmith/items/<id>.yml`. One
file = one item; the file name (minus `.yml`) is the item **id**.

Parsing is **defensive**: an unknown component id or a bad value logs a warning and is skipped
rather than failing the whole item. Unknown *parameter* keys are silently ignored (the default
is used). See [Troubleshooting](troubleshooting.md) for how to read warnings.

---

## Top-level structure

```yaml
material: DIAMOND_SWORD          # (required) base material
item-model: mypack:venom_blade   # (optional) resource-pack model key  — BYO pack
custom-model-data: 10001         # (optional) legacy model data         — BYO pack
name: "<red>My Item</red>"       # (optional) MiniMessage display name
lore:                            # (optional) MiniMessage lore lines
  - "<gray>Line one</gray>"

charges: 5                       # (optional) charge counter
max-charges: 5                   # (optional) capacity (defaults to `charges`)
on-depletion: consume            # (optional) consume | break | keep_inert
durability-bar: false            # (optional) mirror charges onto the vanilla bar

stats:                           # (optional) persistent per-item values (numbers or text)
  level: 1
  uses: 0

abilities:                       # (optional) list of ability pipelines
  - activator: right_click
    # ...

recipes:                         # (optional) how it's crafted — see obtaining.md
  - type: shaped
    # ...

drops:                           # (optional) mob/block drops — see obtaining.md
  mobs: [...]
  blocks: [...]

loot:                            # (optional) loot-table injection — see obtaining.md
  - tables: ["chests/*"]
    # ...
```

Everything except `material` is optional. An item with only a `material` and `recipes` is a
perfectly valid "just a craftable cosmetic" item with no behavior.

---

## Top-level fields

| Key | Type | Default | Notes |
|---|---|---|---|
| `material` | Material | **required** | Any valid **item** material (e.g. `DIAMOND_SWORD`, `BLAZE_ROD`). Blocks that aren't items are rejected. Case-insensitive; `minecraft:` prefix optional. An invalid/missing material **skips the whole item**. |
| `item-model` | namespaced key | none | Points at a model in a resource pack (1.21.4+), e.g. `mypack:venom_blade` → `assets/mypack/items/venom_blade.json`. **Bring-your-own-pack:** with no matching pack the item renders as its base material (or the purple/black missing-texture if the key resolves but the pack lacks the model). |
| `custom-model-data` | int | none | Legacy resource-pack selector. Also BYO-pack. |
| `name` | MiniMessage | none | Display name. Supports full [MiniMessage](https://docs.advntr.dev/minimessage/format.html) (`<gradient>`, `<red>`, etc.). |
| `lore` | list of MiniMessage | none | Lore lines. Include the charge tokens `<charges>` / `<max_charges>` to place the counter yourself; if the item has charges but you include **no** `<charges>` token (and no `durability-bar`), a `Charges: N/M` line is appended automatically so the counter is always visible. A `<stat:name>` token shows a [stat's](#stats) live value and re-renders whenever the stat changes. |
| `charges` | int | none (no counter) | Starting charge count. Once set, charges track and deplete **regardless of lore** — you no longer need a `<charges>` line for them to count. Omitting the key entirely means the item has **no** charge counter. See [Gates → Charges](gates.md#charges). |
| `max-charges` | int | = `charges` | Charge capacity (used by `add_charges`). |
| `on-depletion` | enum | `consume` | What happens when charges hit 0: `consume` (remove one from the stack), `break` (remove one + play the item-break effect), `keep_inert` (leave it at 0, unusable until recharged). |
| `durability-bar` | boolean | `false` | Mirror the charge counter onto the vanilla durability bar so charges show as a depleting bar. |
| `stats` | map of name → value | none | <a id="stats"></a>Persistent per-item values, numbers or text, seeded here and then changed over the item's life by `set_stat` / `add_stat`, read by the `stat_above` / `stat_below` / `stat_equals` conditions, and shown in lore via a `<stat:name>` token. Names must be `a-z 0-9 _`. Each physical item carries its own values (survives drops/restarts), so use these on **unstackable** items. This is what powers leveling / evolving items — see the [worked example](actions.md#persistent-stats--leveling--evolving-items). |
| `abilities` | list | empty | The item's behavior. See below. |
| `recipes` / `recipe` | list / section | none | Crafting recipes. See [Obtaining](obtaining.md). |
| `drops` | section | none | Mob & block drops. See [Obtaining](obtaining.md). |
| `loot` | list | none | Loot-table injection. See [Obtaining](obtaining.md). |

---

## The ability pipeline

Each entry under `abilities:` is one pipeline:

```
Activator  →  Conditions  →  Targeter  →  Actions        (governed by an optional Gate)
  (when)        (if)          (who)        (what)
```

```yaml
abilities:
  - activator: player_hit_entity     # WHEN (required)
    conditions:                      # IF   (optional; all must pass)
      - type: target_is_mob
      - type: is_sneaking
        invert: true                 # reserved flag: negate this condition
    targeter: target                 # WHO  (optional; default "target")
    cooldown: 1.5                    # per-ability cooldown in seconds (optional)
    actions:                         # WHAT (runs in order on each resolved target)
      - type: damage
        amount: 4
    # --- optional gate keys (see Gates) live flat here ---
    permission: myserver.vip
    cost:
      money: 10
```

### `activator` (required)

The trigger id, e.g. `right_click`, `player_hit_entity`, `block_break`, `hold_tick`. If the id
is unknown or missing, the **ability is skipped** with a warning. The full list is in
[Activators](activators.md). Activators currently take **no parameters** — the id is all you
write.

### `conditions` (optional)

A list of pass/fail checks. **All must pass** (logical AND) for the ability to fire. An empty
or absent list means "always fires". Each entry:

```yaml
conditions:
  - type: health_below      # (required) condition id
    value: 6                # condition-specific params (flat)
    invert: false           # (reserved) negate the result — see below
```

- `type` — the condition id (see [Conditions & Targeters](conditions-targeters.md)). Unknown
  type → the condition is skipped with a warning.
- **`invert`** — a *reserved* key (not a condition parameter). `invert: true` wraps the
  condition so the ability reads the opposite: `health_below: 6` + `invert: true` means
  "health is **not** below 6". Works on any condition.

Ability-level conditions see the trigger's *natural* target (e.g. the mob you hit), before the
targeter resolves.

### `targeter` (optional, default `target`)

Decides *who/what* the actions apply to. The actions run once **per resolved target** — so a
`radius` targeter that finds 5 mobs runs the whole action list 5 times, once per mob.

Two syntaxes:

```yaml
targeter: self               # bare string form — no params
```

```yaml
targeter:                    # section form — with params
  type: radius
  radius: 6
  living_only: true
```

The default `target` (the trigger's natural target) is used when `targeter` is omitted. An
unknown targeter falls back to `target` with a warning. Full list in
[Conditions & Targeters](conditions-targeters.md#targeters).

### `cooldown` (optional)

A per-ability cooldown in **seconds** (decimal allowed). While on cooldown the ability won't
re-fire, and the item shows the vanilla greyed-out cooldown sweep. Each ability on an item
cools down **independently and per-player**. This is distinct from the gate's shareable
`cooldown-group` — see [Gates → Cooldowns](gates.md#cooldowns).

### `actions` (required to *do* anything)

An ordered list of what happens. Runs top-to-bottom against each target. Full catalog in
[Actions](actions.md).

```yaml
actions:
  - type: damage             # (required) action id
    amount: 6                # action-specific params (flat)
    from_caster: true
  - type: play_sound
    sound: entity.blaze.shoot
```

- `type` — the action id. Unknown type → the action is skipped with a warning.
- Params are written **flat** alongside `type`.
- **Flow-control** actions (`delay`, `repeat`, `if`, `random`, `chance`) nest further actions
  under body keys (`do`, `then`, `else`) or weighted `branches` — see
  [Actions → Flow control](actions.md#flow-control).

---

## Flow-control nesting

Flow actions carry nested action bodies. The body keys depend on the action:

| Flow action | Body keys | Extra |
|---|---|---|
| `delay` | *(none — waits, then the following siblings run)* | `ticks` |
| `repeat` | `do` | `times` |
| `chance` | `do` | `chance` (0–1) |
| `if` | `then`, `else` | `conditions:` on the node decide which branch |
| `random` | *(none)* | `branches:` — a list of `{ weight, do }` |

```yaml
actions:
  - type: if
    conditions:                 # the if's own gate (per-target)
      - type: target_is_player
    then:
      - type: message
        text: "<red>PvP hit!</red>"
    else:
      - type: damage
        amount: 2

  - type: repeat
    times: 3
    do:
      - type: particle_ring
        particle: flame
      - type: delay             # spacing between rings
        ticks: 4

  - type: random
    branches:
      - weight: 3               # 75% (3 of 4)
        do:
          - type: heal
            amount: 4
      - weight: 1               # 25%
        do:
          - type: strike_lightning
```

> **Inline `conditions:` on actions:** only the `if` action reads them (to choose
> `then`/`else`). On any *leaf* action, a `conditions:` block is parsed but **not** evaluated —
> to conditionally run a leaf, wrap it in an `if`, or put the check at the ability level.

---

## The Gate (optional governance)

Gate keys are written **flat on the ability map** (like `cooldown`). They are all reserved keys
the parameter codec ignores. Any subset may be present; omit the whole lot for an ungated
ability. Full semantics in [Gates & Economics](gates.md).

```yaml
abilities:
  - activator: right_click
    targeter: self
    actions:
      - type: potion_effect_self
        effect: speed
        duration: 10

    # ---- gate keys (all optional) ----
    permission: myserver.speedboots      # required permission node
    charge-cost: 1                        # charges consumed per use
    deny-message: "<red>Not yet: <reason></red>"   # shown on denial (silent if omitted)
    cooldown-group:                       # a shareable named cooldown
      key: dash
      seconds: 5
    region:                               # WorldGuard / claim gating
      in-region: spawn
      can-build: false
      respect-claims: true
    cost:                                 # use-cost, paid at fire-start
      money: 25                           # needs Vault + economy
      xp-levels: 1
      xp: 0                               # raw XP points
      hunger: 2.0
      items:
        - material: EMERALD
          amount: 1
```

Evaluation order (fail-fast; nothing is consumed unless *all* checks pass):
**permission → region → cooldown-group → charges → money → xp-levels → xp → hunger → items.**
Deny-message tokens: `<reason>`, `<needed>`, `<remaining>`.

---

## Reserved keys cheat-sheet

These keys are **structural** — they are not component parameters and cannot be used as param
names:

| Key | Where | Meaning |
|---|---|---|
| `activator` | ability | the trigger id |
| `conditions` | ability, action | list of condition entries |
| `targeter` | ability | the targeter (string or section) |
| `actions` | ability | the action list |
| `cooldown` | ability | per-ability cooldown seconds |
| `type` | condition, targeter, action, recipe | the component id / recipe family |
| `invert` | condition entry | negate the condition |
| `permission`, `charge-cost`, `deny-message`, `cooldown-group`, `region`, `cost` | ability | gate keys ([Gates](gates.md)) |
| `do`, `then`, `else`, `branches`, `weight` | flow action | nested action bodies |

---

## Parameter types

Every component parameter has a type. This is how the GUI knows which editor to show and how
the YAML is coerced. You mostly just write the natural value; the notable ones:

| Type | Write in YAML as | Example |
|---|---|---|
| `INT` / `DOUBLE` | a number | `amount: 4` , `chance: 0.25` |
| `BOOLEAN` | `true` / `false` | `from_caster: true` |
| `STRING` | plain text | `command: "say hi"` |
| `MINIMESSAGE` | MiniMessage text | `text: "<gold>Hi</gold>"` |
| `ENUM` | one of a fixed set | `mode: CREATIVE` |
| `MATERIAL` | a material name | `material: DIAMOND` |
| `EFFECT` | a potion effect id | `effect: poison` |
| `SOUND` | a namespaced sound | `sound: entity.blaze.shoot` |
| `PARTICLE` | a particle name | `particle: flame` |
| `HEAD` | a mob head or `player_head` | `head: creeper_head` |
| `ENTITY_TYPE` | an entity type | `entity: ZOMBIE` |
| `ENCHANTMENT` | an enchantment | `enchant: sharpness` |
| `BIOME` / `WORLD` | a biome / world name | `world: world_nether` |
| `ITEM_REF` | another ItemSmith item id | `item: venom_blade` |
| `STRING_LIST` | a list of strings | `lore: ["a", "b"]` |

> **`HEAD`** is a heads-only variant of `MATERIAL` used by the particle actions and `shoot_projectile`
> (`head`, `trail_head`). Write a head/skull material (e.g. `zombie_head`, `wither_skeleton_skull`) or
> `player_head`; for `player_head`, set the sibling `*_owner` string (e.g. `head_owner: Notch`) to choose
> whose skin. A blank value means "use the particle instead". In the GUI it's a picker showing only heads,
> with a **"None (use particle)"** option to clear it.

---

## A complete annotated example

```yaml
# plugins/ItemSmith/items/stormcaller.yml
material: TRIDENT
name: "<gradient:#4fc3f7:#b388ff>Stormcaller</gradient>"
lore:
  - "<gray>Charges: <charges>/<max_charges></gray>"
  - "<dark_gray>Sneak-right-click to call the storm.</dark_gray>"

charges: 3
max-charges: 3
on-depletion: keep_inert          # goes inert at 0 charges; recharge to reuse
durability-bar: true              # show charges as a durability bar

abilities:
  # Ability 1: the storm strike (sneak + right click)
  - activator: sneak_right_click
    targeter:
      type: nearby_monsters
      radius: 8
    cooldown: 1.0
    charge-cost: 1                 # each cast spends one charge
    deny-message: "<red>Out of charge.</red>"
    actions:
      - type: strike_lightning
      - type: knockback
        strength: 1.5
      - type: particle_burst
        particle: electric_spark

  # Ability 2: recharge slowly while simply holding it in daylight
  - activator: hold_tick
    conditions:
      - type: is_day
      - type: charges_below
        amount: 3
    targeter: self
    cooldown: 5.0                  # throttle the passive tick
    actions:
      - type: add_charges
        amount: 1
      - type: send_actionbar
        text: "<aqua>The trident hums with energy...</aqua>"

recipes:
  - type: smithing
    template: NETHERITE_UPGRADE_SMITHING_TEMPLATE
    base: TRIDENT
    addition: NETHER_STAR

drops:
  mobs:
    - entities: [DROWNED]
      chance: 0.02
      require-player-kill: true
```

---

## See also

- [Activators](activators.md) · [Actions](actions.md) ·
  [Conditions & Targeters](conditions-targeters.md) · [Gates](gates.md) ·
  [Obtaining](obtaining.md)
