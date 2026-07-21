<!-- Badges are placeholders until first publish. Replace PROJECT_ID / URLs at release. -->
[![Modrinth](https://img.shields.io/badge/Modrinth-pre--release-1bd96a?logo=modrinth)](#)
[![Paper](https://img.shields.io/badge/Paper-1.21.8-orange)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-21-red?logo=openjdk)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

# ItemSmith

**Custom items for Paper 1.21.8 — built in-game, no config files required.**

ItemSmith lets you create custom weapons, tools, armor, wearables and consumables with deep,
scripted abilities, through a full in-game chest-GUI item creator.
Design a complete item, including nested ability logic, crafting recipes, mob/block drops and
loot-table injection, without ever opening a `.yml` file. And when you *do* want to hand-edit or
share an item, every item is a clean, human-readable YAML file you can drop into another server and
`/reload`.

> **Status: pre-release.** ItemSmith is feature-complete through obtaining (M0–M6) and in active
> polish toward its first public release. It is **not downloadable yet** — this repository is the
> development home. Star/watch to be notified at launch.

---

## Why ItemSmith

- **No heavyweight dependencies.** A single drop-in jar with no required companion plugin.
- **A real in-game creator.** Build items through a chest GUI backed by the
  native Paper dialog system — pick an activator, add conditions, choose a targeter, stack actions,
  set recipes and drops, all in-game. The GUI reads and writes the same YAML files, so GUI-built and
  hand-edited items are interchangeable.
- **A genuinely deep engine.** ~400 building blocks: **150 activators**, **163 actions** (including
  flow control — delay, repeat, if, random), **73 conditions**, and **18 targeters**. Compose them
  into as many abilities per item as you like.
- **Share by file.** Items live at `plugins/ItemSmith/items/<id>.yml`. Copy a file to another server,
  `/itemsmith reload`, done.

## The engine in one picture

An **item** is a base material plus display text plus a list of **abilities**. Each ability is a
small pipeline:

```
Activator  →  Conditions  →  Targeter  →  Actions      (+ optional Gate)
  (when)        (if)           (who)        (what)
```

- **Activator** — the trigger (right-click, hit an entity, take fall damage, sneak, a passive tick…).
- **Conditions** — must all pass (health below X, in water, has permission, random chance…).
- **Targeter** — who the actions apply to (self, the entity you hit, nearby monsters, a cone…).
- **Actions** — what happens (deal damage, apply a potion effect, launch, teleport, run a command…),
  including nested `delay` / `repeat` / `if` / `random` flow control.
- **Gate** (optional) — permission, world/region, use-cost (money / XP / items / hunger), charges,
  custom durability, and shared cooldown groups.

## A tiny example

`plugins/ItemSmith/items/frost_axe.yml`:

```yaml
material: IRON_AXE
name: "<gradient:#40c4ff:#e1f5fe>Frost Axe</gradient>"
lore:
  - "<gray>Chills foes to the bone.</gray>"
abilities:
  - activator: player_hit_entity   # when you hit a mob in melee
    targeter: target               # act on what you hit
    cooldown: 3
    actions:
      - type: potion_effect
        effect: slowness
        duration: 4
        amplifier: 1               # Slowness II

recipe:
  type: shapeless
  ingredients: [IRON_AXE, PACKED_ICE, PACKED_ICE, PACKED_ICE, PACKED_ICE, PACKED_ICE, PACKED_ICE, PACKED_ICE, PACKED_ICE]
```

`/itemsmith reload`, craft it (or `/itemsmith get frost_axe`), and hit a mob. That whole item can
also be built start-to-finish through the in-game creator.

## Features at a glance

| Area | What you get |
|------|--------------|
| **Abilities** | 150 activators · 163 actions · 73 conditions · 18 targeters · nested flow control · unlimited abilities per item |
| **In-game creator** | Chest GUI + native Paper dialogs; templates + blank start; categorized & searchable component pickers; immutable item id + "Save As"; reads/writes the same YAML |
| **Obtaining** | Crafting (shaped, shapeless, furnace/blast/smoker/campfire, smithing, stonecutter — multiple recipes per item, vanilla recipe-book unlock); mob & block drops (chance, count, player-kill, silk-touch policy); loot-table injection (chests, mobs, fishing); player catalog GUI |
| **Gating & economics** | Permission gates; world/region restriction (WorldGuard); use-cost via Vault (money / XP / items / hunger); charges & custom durability; consume-on-depletion; shared cooldown groups |
| **Data model** | One human-readable YAML file per item; hand-editable, shareable, `/reload`-able; the GUI is just another editor over the same files |
| **Integrations** | Optional soft-depends, all graceful if absent: Vault, WorldGuard, GriefPrevention, PlaceholderAPI |

## Requirements

- **Paper 1.21.8** (or a Paper fork on the same API). Built against `paper-api` `1.21.8-R0.1`.
- **Java 21**.
- Optional (soft-depend, only if you want those features): **Vault** (use-costs / economy),
  **WorldGuard** (region gating), **GriefPrevention** (claim build-checks), **PlaceholderAPI**.
  ItemSmith runs fine with none of them installed.

## Install (at launch)

1. Drop `ItemSmith-1.0.0.jar` into your server's `plugins/` folder.
2. Start the server. ItemSmith generates its config and `items/` folder.
3. `/itemsmith open` to launch the creator, or drop a `.yml` into `plugins/ItemSmith/items/` and
   `/itemsmith reload`.

See **[docs/installation.md](docs/installation.md)** and **[docs/quick-start.md](docs/quick-start.md)**
for the full walkthrough.

## Commands & permissions

| Command | Does |
|---------|------|
| `/itemsmith catalog` | Open the player-facing catalog (how to obtain each item) |
| `/itemsmith open` | Open the in-game item creator |
| `/itemsmith create` | Start a new item |
| `/itemsmith get <id>` | Give yourself an item |
| `/itemsmith give <player> <id>` | Give another player an item |
| `/itemsmith list` | List defined items |
| `/itemsmith reload` | Reload item files from disk |

Aliases: `/ismith`, `/citems`. Permissions: `itemsmith.admin` (op) for management;
`itemsmith.catalog` (default on) for the catalog; `itemsmith.bypass.*` (off by default) to bypass
cost/cooldown/region gates. Full reference in
**[docs/commands-permissions.md](docs/commands-permissions.md)**.

## Documentation

Full docs live in **[`docs/`](docs/)** — start with the [docs index](docs/README.md):

- [Installation](docs/installation.md) · [Quick start](docs/quick-start.md)
- [YAML reference](docs/yaml-reference.md) · [Activators](docs/activators.md) ·
  [Actions](docs/actions.md) · [Conditions & targeters](docs/conditions-targeters.md)
- [Gates (cost, cooldown, region, charges)](docs/gates.md) ·
  [Obtaining (recipes, drops, loot)](docs/obtaining.md)
- [In-game GUI creator](docs/gui-creator.md) · [Commands & permissions](docs/commands-permissions.md)
- [Integrations](docs/integrations.md) · [Troubleshooting](docs/troubleshooting.md) ·
  [FAQ](docs/faq.md)

## Roadmap

Shipped through **M6** (engine, activators, actions + flow, conditions + targeters, gating &
economics, in-game creator, obtaining). Next up: **M7** integration polish (PlaceholderAPI
placeholders, deeper protection/economy), **M8** resource-pack generation (custom textures/models —
until then ItemSmith is **bring-your-own-pack** and the GUI uses a native look), then **custom blocks
& mobs**. Details and honest caveats in **[ROADMAP.md](ROADMAP.md)**. Plans, not promises.

## Build from source

```bash
git clone <this-repo>
cd ItemSmith
./gradlew build        # output: build/libs/ItemSmith-1.0.0.jar
```

Requires a **JDK 21**. The build shades and relocates its GUI stack (`triumph-gui-paper`) so
ItemSmith stays a single conflict-free jar. `./gradlew runServer` launches a Paper 1.21.8 test
server. See **[CONTRIBUTING.md](CONTRIBUTING.md)** for project layout and how to add a component.

## License

Released under the **[MIT License](LICENSE)** — free to use, run, modify, and redistribute. Server
owners, forks, and downstream tooling can use it freely.

## Credits

Created by **MastrJimbo**. Built on [Paper](https://papermc.io/) and
[triumph-gui](https://github.com/TriumphTeam/triumph-gui) (MIT, © 2021 TriumphTeam),
which powers the in-game creator GUI and is bundled inside the jar. Full licenses for
bundled dependencies are in **[THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md)**.
