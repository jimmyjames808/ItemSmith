# Troubleshooting

[← Back to index](README.md)

ItemSmith is **defensive by design**: a bad value logs a warning and is skipped, rather than
failing the whole item or the plugin. So your first stop for almost any problem is the server
console after a `/itemsmith reload`.

---

## How to read reload warnings

Run `/itemsmith reload` and watch the console. Warnings name the item and the exact problem,
for example:

```
[ItemSmith] Item 'fire_wand' has an unknown action 'ignit'; skipping it.
[ItemSmith] Item 'fire_wand' has an unknown or missing activator 'righclick'; skipping ability.
[ItemSmith] Item 'loot_gem' loot rule has no 'tables'; skipping it.
```

The **skip levels** tell you how much was lost:

| Warning shape | What was skipped |
|---|---|
| *"…invalid or missing 'material'; skipping."* | The **whole item** (material is required). |
| *"…unknown or missing activator '…'; skipping ability."* | Just that **ability**. |
| *"…unknown action/condition '…'; skipping it."* | Just that **component**; the rest of the ability still runs. |
| *"…skipping recipe / drop / loot rule."* | Just that one recipe/drop/loot entry. |

A **mistyped parameter key** produces *no* warning — it's silently ignored and the default is
used. If an action "does nothing", double-check the param names against
[Actions](actions.md) / [Conditions & Targeters](conditions-targeters.md).

`/itemsmith reload` prints the final count (*"Reloaded N item(s)."*). If N is lower than your
file count, an item was skipped — check the warnings above it.

---

## An item won't load at all

- **Invalid or missing `material`.** It must be a valid **item** material (block-only materials
  are rejected). Check spelling; `minecraft:` prefix is optional, case doesn't matter.
- **Invalid file id.** The file name (minus `.yml`) must be lowercase `a-z`, `0-9`, `_`. A file
  like `Fire Wand.yml` is skipped — rename it `fire_wand.yml`.
- **YAML syntax error.** A malformed file logs a parse error. Watch your indentation (spaces,
  not tabs) and quoting. Paste into a YAML validator if unsure.

---

## An ability doesn't fire

Work down the pipeline:

1. **Activator** — is it the right [trigger](activators.md)? Remember triggers with no natural
   target (air clicks, `hold_tick`, `join`) resolve to **nothing** under the default `target`
   targeter — give those abilities a `self` / `radius` / `looking_at_*` targeter instead.
2. **Conditions** — all must pass. Temporarily remove them to isolate the problem. Check the
   [`invert`](conditions-targeters.md#the-invert-flag) flag isn't backwards.
3. **Targeter** — did it resolve anyone? An area targeter with too small a `radius`, or
   `looking_at_entity` not aimed at a mob, yields **no targets**, so the actions never run.
4. **Gate** — a [gate](gates.md) denial is **silent by default**. Add a
   `deny-message: "<red><reason></red>"` to see *why* (permission, region, cooldown, charges,
   money, xp, hunger, items).
5. **Cooldown** — still on the per-ability `cooldown` or a `cooldown-group`? Wait it out or
   test with `itemsmith.bypass.cooldown`.

---

## A recipe won't register / doesn't craft

- **Reload after editing.** Recipes register on load/reload — run `/itemsmith reload`.
- **Check for a recipe warning.** Shaped recipes need 1–3 `shape` rows and single-character
  ingredient keys; cooking/smithing/stonecutting need valid materials. A bad recipe is skipped
  with a warning and the rest of the item still loads.
- **Reserved-but-unsupported types.** `brewing` and `anvil` are recognized names but **not
  implemented** — they won't produce a working recipe.
- **Recipe book.** Recipes are craftable immediately; players can unlock the book entry from
  **Add to recipe book** in the [catalog](obtaining.md#the-player-catalog).

See [Obtaining → Recipes](obtaining.md#recipes) for the exact keys per family.

---

## Drops aren't appearing

- **Chance.** `chance` is 0–1 per event — a `0.02` drop is *supposed* to be rare. Test with
  `chance: 1.0` first.
- **Mob drops need a player kill by default.** `require-player-kill` defaults to `true`, so
  suffocation/fall/lava kills won't drop. Set it `false` to allow non-player kills.
- **Peaceful / difficulty.** On Peaceful, hostile mobs don't spawn to be killed — verify the
  target mob is actually present.
- **Block drops only fire when the break drops items.** Creative-mode breaks, and any break
  that yields no drops, won't trigger a block drop.
- **Silk Touch policy.** `silk-touch: require` drops only *with* Silk Touch; `forbid` only
  *without*. It checks the **main-hand tool**. If nothing drops, your tool may be on the wrong
  side of the policy.
- **Reload.** Drop rules are re-indexed on `/itemsmith reload`.

See [Obtaining → Drops](obtaining.md#mob--block-drops).

---

## Loot injection isn't working

- **Target the right table.** Chests use their real key (`chests/simple_dungeon`); **mob loot**
  uses `entities/<type>`; **fishing** uses exactly `gameplay/fishing`. See
  [Obtaining → Loot injection](obtaining.md#loot-table-injection).
- **Patterns are prefix-based.** `chests/` (or `chests/*`) matches every chest; a too-specific
  pattern that's *longer* than the actual key won't match (e.g. `gameplay/fishing/fish` won't
  match the `gameplay/fishing` key ItemSmith uses).
- **`tables` is required.** A loot rule with no `tables` is skipped with a warning.

---

## The GUI creator input doesn't open

The creator's text/number/option dialogs use Paper's native Dialog API, which needs a
**1.21.6+ client**. On an older client ItemSmith shows:

> *The ItemSmith creator needs a 1.21.6+ client for this input.*

…and returns you to the chest menu. Update your client (the server itself only needs Paper
1.21.8). The read-only [catalog](obtaining.md#the-player-catalog) works on any client. Also
confirm you have `itemsmith.admin` — the creator is admin-only.

---

## Custom textures show as the base item (or a missing-texture)

ItemSmith does **not** ship or generate resource packs (that's the roadmap item M8). `item-model`
and `custom-model-data` are **bring-your-own-pack**:

- With **no matching resource pack**, the item renders as its **base material** — this is
  expected, not a bug.
- If the item shows the purple/black **missing-texture**, the `item-model` key resolved but your
  pack doesn't actually contain that model. Fix or add the model in your pack.

The GUI itself uses a native look and needs no pack.

---

## A money cost is never charged

Money features need **Vault + an economy provider** (e.g. EssentialsX). Without them, ItemSmith
**intentionally skips** money costs (so an item can't be bricked on a non-economy server) and
money actions become no-ops. Check the boot log for `Vault=true`; if it's false, install the
economy stack and restart. See [Integrations → Vault](integrations.md#vault).

---

## A region-gated ability denies everywhere (or works everywhere)

- **`in-region` requires WorldGuard.** Without it, the region requirement **fails closed** — the
  ability is denied everywhere. Install WorldGuard, or remove the region key.
- **`can-build` fails open** when no protection plugin is present — it "works everywhere". Add
  WorldGuard/GriefPrevention if you actually want it enforced.

See [Integrations → WorldGuard](integrations.md#worldguard) and [Gates → Region](gates.md#region).

---

## The server lags when I hold/use an item

High-frequency activators — `move`, `input`, and the tick activators (`hold_tick`,
`inventory_tick`, `equip_tick`) — fire *constantly*. Always throttle them:

- add a per-ability `cooldown` (even `0.25`–`1.0` seconds helps a lot),
- and/or gate them with [conditions](conditions-targeters.md) so the actions only run when they
  should,
- avoid heavy actions (large `area_break`, big particle counts, entity spawns) every tick.

---

## Still stuck?

- Re-read the relevant reference: [YAML Reference](yaml-reference.md), [Activators](activators.md),
  [Actions](actions.md), [Conditions & Targeters](conditions-targeters.md), [Gates](gates.md),
  [Obtaining](obtaining.md).
- Compare against the shipped examples `venom_blade.yml` and `frost_axe.yml` in
  `plugins/ItemSmith/items/`.
- Confirm you're on **Paper 1.21.8 / Java 21** — ItemSmith won't run on Spigot/CraftBukkit.
