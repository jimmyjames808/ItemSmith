# Obtaining Items

[← Back to index](README.md) · Related: [YAML Reference](yaml-reference.md) ·
[The GUI Creator](gui-creator.md)

Beyond `/itemsmith give`, ItemSmith lets players *earn* your custom items three ways:
**recipes** (crafting/cooking/smithing/…), **drops** (from mobs and blocks), and **loot-table
injection** (into chests, mob loot and fishing). A player-facing **catalog** shows how to get
each one.

All of this is optional, per-item YAML. Everything below reloads with `/itemsmith reload`.

---

## Recipes

Give an item one or more recipes with a `recipes:` list. Each entry has a `type` (the recipe
family) and family-specific keys. All families are backed by real Bukkit recipes, so they show
up in the vanilla recipe book and crafting UIs.

```yaml
recipes:
  - type: shaped
    # ...
  - type: blasting
    # ...
```

> An item can carry **several recipes of different families** — e.g. craftable *and* smeltable,
> or several routes to the same item. Each is registered under its own key
> (`recipe_<id>_<n>`), and they're cleanly re-registered on every reload. Bad recipes warn and
> skip without breaking the rest of the item.
>
> A legacy single `recipe:` section (one recipe, no list) is still read for backwards
> compatibility, but prefer the `recipes:` list.

### Shaped

A 1–3 row grid. `shape` rows map single-letter keys to materials via `ingredients`. Spaces mean
"empty".

```yaml
recipes:
  - type: shaped
    shape:
      - " F "
      - " D "
      - " S "
    ingredients:
      F: FERMENTED_SPIDER_EYE
      D: DIAMOND
      S: STICK
```

### Shapeless

An unordered list of ingredient materials.

```yaml
recipes:
  - type: shapeless
    ingredients:
      - IRON_AXE
      - PACKED_ICE
      - PACKED_ICE
      - PACKED_ICE
```

### Cooking — `furnace` / `blasting` / `smoking` / `campfire`

A single `input` smelted/cooked into the item. `experience` and `cook-time` (ticks) are
optional and default per family.

```yaml
recipes:
  - type: blasting
    input: RAW_GOLD
    experience: 0.5        # default 0.1
    cook-time: 100         # default depends on type (see table)
```

| `type` | Station | Default `cook-time` |
|---|---|---|
| `furnace` | Furnace | 200 ticks |
| `blasting` | Blast Furnace | 100 ticks |
| `smoking` | Smoker | 100 ticks |
| `campfire` | Campfire | 600 ticks |

### Smithing (transform)

A smithing-table transform: `template` + `base` + `addition` → the item.

```yaml
recipes:
  - type: smithing
    template: NETHERITE_UPGRADE_SMITHING_TEMPLATE
    base: DIAMOND_SWORD
    addition: NETHERITE_INGOT
```

### Stonecutting

A single `input` block cut into the item on a stonecutter.

```yaml
recipes:
  - type: stonecutting
    input: STONE
```

> **Reserved but not yet supported:** `brewing` and `anvil` recipe families are recognized
> names but not implemented (no Bukkit recipe API for them). Don't rely on them.

### Recipe book

Because recipes are native Bukkit recipes, they're craftable immediately and appear in the
recipe book once unlocked. Players can unlock the recipe from the [catalog](#the-player-catalog)
via **Add to recipe book**.

---

## Mob & block drops

A `drops:` section makes an item drop from killing mobs or breaking blocks. It has two optional
lists, `mobs:` and `blocks:`.

```yaml
drops:
  mobs:
    - entities: [ZOMBIE, HUSK]
      chance: 0.05
      min: 1
      max: 1
      require-player-kill: true
  blocks:
    - blocks: [DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE]
      chance: 0.10
      silk-touch: forbid
```

### Mob drops

Fires on entity death. Each rule:

| Key | Default | Meaning |
|---|---|---|
| `entities` | *(empty = any mob)* | Entity types this rule applies to (e.g. `[ZOMBIE, SKELETON]`). |
| `chance` | `1.0` | 0–1 probability, rolled per death. |
| `min` / `max` | `1` / `= min` | Stack-count range (uniform, inclusive, floored at 1). |
| `require-player-kill` | `true` | Only drop when a player got the kill credit. |

### Block drops

Fires on block break, and **only when the break actually drops items** — creative breaks and
programmatic no-drop breaks won't trigger it. Each rule:

| Key | Default | Meaning |
|---|---|---|
| `blocks` | **required** | Block materials this rule applies to. |
| `chance` | `1.0` | 0–1 probability, rolled per break. |
| `min` / `max` | `1` / `= min` | Stack-count range. |
| `silk-touch` | `any` | Tool requirement: `any` (always), `require` (only *with* Silk Touch, harvest-style), `forbid` (only *without*, ore-style). |

> Silk Touch is checked against the **main-hand tool**. The `require` / `forbid` values also
> accept the friendly spellings `required`/`true`/`yes` and `forbidden`/`false`/`no`.

---

## Loot-table injection

Inject an item into vanilla (and datapack) loot tables — chest loot, mob loot, and fishing —
with a top-level `loot:` list. Each rule targets one or more table **patterns**.

```yaml
loot:
  - tables: ["chests/simple_dungeon", "chests/abandoned_mineshaft"]
    chance: 0.15
    min: 1
    max: 1
  - tables: ["entities/zombie"]      # mob loot
    chance: 0.02
  - tables: ["gameplay/fishing"]     # fishing
    chance: 0.05
```

| Key | Default | Meaning |
|---|---|---|
| `tables` | **required** | One or more loot-table key patterns (see below). |
| `chance` | `1.0` | 0–1 probability, rolled per generation. |
| `min` / `max` | `1` / `= min` | Stack-count range. |

### Which source each table hits

| Source | How it's generated | Table key to target |
|---|---|---|
| **Containers** (chests, barrels, minecart chests, structure loot) | On loot generation. | The real table key, e.g. `chests/simple_dungeon`, `chests/village/village_weaponsmith`. |
| **Mob loot** | On mob death. | `entities/<type>`, e.g. `entities/zombie`, `entities/enderman`. |
| **Fishing** | On a successful catch. | `gameplay/fishing` (the single fishing key ItemSmith exposes). |

### Table pattern matching

Patterns are matched leniently:

- Matching is **case-insensitive** and **prefix-based**. A pattern matches if it equals or is a
  prefix of either the path (`chests/simple_dungeon`) or the full key
  (`minecraft:chests/simple_dungeon`).
- A trailing `*` is supported (and is just stripped — matching is prefix-based anyway). So
  `chests/*` and `chests/` both match **every** chest table.

| Pattern | Matches |
|---|---|
| `chests/*` or `chests/` | Every chest/container table. |
| `chests/simple_dungeon` | Exactly the simple-dungeon chest (and anything sharing that prefix). |
| `entities/zombie` | Zombie mob loot. |
| `entities/` | All mob-loot tables. |
| `gameplay/fishing` | Fishing catches. |
| `minecraft:chests/end_city_treasure` | The full-key form works too. |

> Fishing uses exactly the `gameplay/fishing` key — target that, not the vanilla sub-tables
> like `gameplay/fishing/fish`.

---

## The player catalog

Players with `itemsmith.catalog` (default: **everyone**) can browse your custom items:

```
/itemsmith catalog
```

The catalog is **read-only** — it never gives items or edits them. For each item it shows:

- a display preview,
- **how to obtain it** — tags like *Craftable*, *Dropped*, *Loot* (or *Admin-only* if there's
  no obtain route), with details, and
- an **Add to recipe book** button that unlocks the item's craftable recipe in the player's
  recipe book.

For the admin-only creator GUI (which *does* create/edit/give), see
[The GUI Creator](gui-creator.md).

---

## See also

- [YAML Reference](yaml-reference.md) — where `recipes`, `drops` and `loot` sit in the schema.
- [Commands & Permissions](commands-permissions.md) — `give`, `get`, `catalog` and the perms.
- [Troubleshooting → Recipes / drops](troubleshooting.md) — when a recipe or drop doesn't show.
