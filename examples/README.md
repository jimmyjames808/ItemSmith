# ItemSmith — Example Items

Eight self-contained showcase items, each built to flex a different corner of the
ItemSmith engine. Every file here parses cleanly against the real schema — no
made-up components or parameters.

## How to use them

1. Copy any `.yml` from `examples/items/` into your server's
   **`plugins/ItemSmith/items/`** folder (the file name is the item id).
2. Run **`/itemsmith reload`**.
3. Grab one for testing with **`/itemsmith get <id>`** (or
   **`/itemsmith give <player> <id>`**), then use it as described below.

> The items are also **obtainable in survival** — each ships with a recipe,
> mob drop, or loot-table injection so real players can earn them. Custom
> recipes appear in the recipe book once the item is loaded.
>
> **Textures:** every item renders as its base material (a netherite sword, a
> clock, etc.). To give it a bespoke model, add your own resource pack and set
> `item-model:` or `custom-model-data:` in the file — otherwise the base look is
> intentional, not a bug.

An item is a base material + display text + a list of **abilities**. Each ability
is a pipeline: **activator** (when) → **conditions** (if) → **targeter** (who) →
**actions** (what), with optional **gates** (cooldowns, charges, costs, regions).

---

## The items

### 1. `stormforged_greatsword.yml` — legendary melee weapon
A netherite sword with a layered on-hit combo and a right-click AoE.

- **Shows off:** a multi-action on-hit combo (`add_damage` + `wither` +
  `enchanted_hit` particles + sound); a **`chance`** proc that calls down real
  lightning; an **`if` gate on the wielder's health** (`health_percent_below`)
  that triggers a `lifesteal` + `regeneration` "desperation" burst under 35% HP;
  and a two-ability **right-click "Thunderclap"** — a self-targeted visual cast
  plus a **`radius` AoE targeter** that `damage`s, `knockback`s and `slowness`es
  every nearby mob, both on a **per-ability 8s `cooldown`** (shows the vanilla
  cooldown sweep).
- **Obtain:** smithing table — **netherite sword + nether star** under a
  Netherite Upgrade Smithing Template.
- **Use:** left-click mobs for the combo; **right-click** to unleash Thunderclap.

### 2. `earthshaker_drill.yml` — charged mining tool
A netherite pickaxe powered by burning fuel cells.

- **Shows off:** a **charge counter as a power cell** (`charges` / `max-charges`
  / `durability-bar`, `on-depletion: keep_inert` so it survives and recharges);
  **two mining modes on one `block_break` trigger**, split by an `is_sneaking`
  condition (one uses **`invert: true`**) — normal mining runs
  **`auto_smelt_drops`** (ores drop smelted), sneak-mining runs
  **`vein_break`** (rips the whole seam) — each with a different **`charge-cost`**;
  and a refuel ability paid with an **item cost** (`cost.items`).
- **Obtain:** craft (shaped) — netherite ingots, diamonds and redstone blocks.
- **Use:** mine to auto-smelt ores; **sneak + mine** to vein-mine; **sneak +
  right-click holding Coal** to recharge (+50).

### 3. `windwalker_staff.yml` — utility movement wand
A blaze rod that turns momentum into distance.

- **Shows off:** a movement ability behind a **full gate stack at once** —
  **`charge-cost`** + **`cost: xp-levels`** + **`cooldown-group`** (a named 2s
  lockout); an **`if`/`else`** inside the ability (sneak → **`blink`** through
  walls, otherwise **`leap`** forward); and **two recipes for one item** (a
  shaped craft *and* a stonecutter cut from a Breeze Rod).
- **Obtain:** craft (shaped) — phantom membrane + blaze rod + feather — **or**
  stonecut a Breeze Rod.
- **Use:** **right-click** to Windstep forward; **sneak + right-click** to Blink;
  **sneak + left-click holding 2 Feathers** to recharge. Each step costs a charge
  and 1 XP level.

### 4. `phoenix_elixir.yml` — consumable
A golden apple distilled into rebirth.

- **Shows off:** an **`item_consume`** ability with **timed flow control** —
  an instant `heal` + `fire_resistance` + `regeneration` on drink, a
  **`repeat` + `delay`** loop that pulses a flame aura over ~3 seconds, then a
  **`delay`** that blooms a delayed "second wind" `absorption` shield.
- **Obtain:** **smoke a Glistering Melon Slice** in a smoker.
- **Use:** eat it. Watch the aura pulse, then the shield arrive a few seconds later.

### 5. `aegis_bulwark.yml` — wearable armor
A netherite chestplate that buffs while worn and bites back when struck.

- **Shows off:** the full armor lifecycle — **`equip`** (announce + `end_rod`
  ring), **`equip_tick`** (re-applies `resistance` + `fire_resistance` every
  second while worn), and **`unequip`** (`remove_effect` strips them instantly);
  plus a reactive **`player_take_damage_by_entity`** ability that has a `chance`
  to `knockback` + singe the attacker — proving **defense triggers fire for worn
  gear**, not just held items.
- **Obtain:** craft (shaped) — netherite ingots around a diamond block.
- **Use:** wear it. Passives apply automatically; attackers get repelled.

### 6. `warden_heart.yml` — boss-drop trophy
A still-beating sculk heart you can only earn.

- **Shows off:** an item with **no recipe** — it **drops from killing a Warden**
  (`drops.mobs` with `require-player-kill: true`) and is **injected into
  deep-structure chest loot** (`loot` → `chests/ancient_city`,
  `chests/woodland_mansion`, `chests/end_city_treasure` at 15%); plus a flashy
  right-click **cosmetic** (`spawn_firework` + `particle_sphere` + `send_bossbar`)
  capped with a **`random` weighted flourish** so no two pulses look alike.
- **Obtain:** kill a Warden, or loot an Ancient City / Mansion / End City chest.
  (Use `/itemsmith get warden_heart` to test.)
- **Use:** **right-click** to make it pulse.

### 7. `chronomancers_clock.yml` — nested-flow-control showcase
A clock that fractures time — the engine's scripting stress test.

- **Shows off:** deep flow nesting in one ability — a **`repeat`** of 6 pulses,
  where each pulse contains an **`if`/`else`** (sneak → slow yourself, stand →
  hasten), a **`random` weighted** flourish, and a **`delay`** so the pulses play
  out over time; then a trailing `delay` + wind-down after the loop.
- **Obtain:** craft (shaped) — a clock ringed by amethyst shards.
- **Use:** **right-click** to trigger Time Fracture. Sneak while it runs to feel
  the branch flip.

### 8. `sunforged_ingot.yml` — multi-recipe trinket
One gold bar, three ways to make it.

- **Shows off:** **three recipes on a single item** — shaped craft, **`blasting`**
  from raw gold, and **`stonecutting`** from a gold block — plus a **world
  condition gate**: its warming right-click only works by day (`is_day`), with a
  `deny-message` after dusk.
- **Obtain:** craft it, blast raw gold in a blast furnace, or stonecut a gold block.
- **Use:** **right-click by day** for a burst of flame and `fire_resistance`.

---

## Feature coverage at a glance

| Engine feature | Demonstrated in |
| --- | --- |
| On-hit combo (`add_damage`, effect, particles, sound) | stormforged_greatsword |
| `chance` action / `if` + `else` flow | greatsword, windwalker, chronomancers_clock |
| Health condition (`health_percent_below`) | stormforged_greatsword |
| AoE targeter (`radius`) | stormforged_greatsword |
| Per-ability `cooldown` (client sweep) | greatsword, warden_heart, chronomancers_clock |
| `cooldown-group` (named lockout) | windwalker_staff |
| Charges (`charge-cost`, `keep_inert`, `durability-bar`) | earthshaker_drill, windwalker_staff |
| `auto_smelt_drops` + `vein_break` | earthshaker_drill |
| `is_sneaking` condition + `invert: true` | earthshaker_drill |
| Cost gates (`xp-levels`, `cost.items`) | windwalker_staff, earthshaker_drill |
| Movement (`leap`, `blink`) | windwalker_staff |
| `item_consume` + `repeat`/`delay` timed ticks | phoenix_elixir |
| Armor lifecycle (`equip`/`equip_tick`/`unequip`) | aegis_bulwark |
| Worn-gear defense trigger (`player_take_damage_by_entity`) | aegis_bulwark |
| Mob drops (`require-player-kill`) + loot injection | warden_heart |
| `random` weighted branches | warden_heart, chronomancers_clock |
| Nested `repeat` → `if`/`random`/`delay` | chronomancers_clock |
| Multi-recipe (`shaped` + `blasting` + `stonecutting`) | sunforged_ingot |
| Smithing / smoking / stonecutting recipes | greatsword / phoenix_elixir / windwalker |
| World condition gate (`is_day`) + `deny-message` | sunforged_ingot |
