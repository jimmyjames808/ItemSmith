# Changelog

All notable changes to **ItemSmith** are documented here.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project
aims to follow [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

> **Note:** ItemSmith is **pre-release**. Version 1.0.0 is in polish and has **not been published**
> yet — the entry below describes what the initial public release will contain. Until 1.0.0 ships,
> the YAML schema, component names, and defaults may still change without notice.

## [Unreleased]

Polish toward the 1.0.0 public release: documentation, examples, packaging, and final hardening.
Planned before release: performance pass on listener hot paths, a Folia compatibility note, and the
Modrinth page + gallery. See [ROADMAP.md](ROADMAP.md) for what comes after launch.

## [1.0.0] — unreleased (initial public release)

The first public build of ItemSmith: a schema-driven custom-items plugin for
Paper 1.21.8 (Java 21), delivered as a single drop-in jar. This release covers development
milestones **M0 through M6**.

### Added

**Engine (M0).**
- Schema-driven component engine. An item is a base material + display text + a list of **abilities**;
  each ability is a pipeline of **Activator → Conditions → Targeter → Actions**, with an optional
  **Gate**.
- Every component declares its own typed parameters, so the YAML (de)serializer and the GUI editors
  are generated from one schema and can't drift apart.
- Per-item YAML as the source of truth: `plugins/ItemSmith/items/<id>.yml`. Hand-editable, shareable
  between servers, and reloadable at runtime.
- Item identity via Paper `ItemMeta` + persistent data (PDC). No NBT-API dependency.
- Unlimited abilities per item and unlimited actions per ability.

**Activators (M1) — ~150 triggers.**
- Interaction: left/right click (air/block), sneak-clicks, arm swing, click entity/player, inventory
  open/close/click/drag.
- Combat: hit/kill entity or player, take damage (by entity/player/projectile), shield block, being
  targeted, and specific damage causes (fall, fire, lava, drowning, and more).
- Plus movement, world/block, equip, projectile, consume, tick/passive and lifecycle triggers.

**Actions (M2) — ~162 actions, including flow control.**
- Combat, movement, potion/effects, world/block, player-state, economy, command, and sound/visual
  actions.
- Nested flow control: `delay`, `repeat`, `if`, and `random` — build multi-step, timed and
  conditional ability logic.

**Conditions & targeters (M3) — ~73 conditions, 18 targeters.**
- Conditions across player-state, health/food/XP, identity/permission, chance, cooldown, economy
  balance, charges, and region membership.
- Targeters: self, the triggering target, nearest/nearby entities/players/monsters, look-at
  block/entity, and geometric selectors (radius, ring, cone, line, offset, and more).

**Gating & economics (M4).**
- Permission-gated abilities.
- World/region restriction via **WorldGuard** (fails closed when WorldGuard is absent so region locks
  can't be bypassed).
- Per-use costs via **Vault**: money, XP (levels or raw points), items, and hunger — in any
  combination.
- Charges and custom durability (damage-per-use), with a configurable consume-on-depletion policy.
- Shared, named cooldown groups across abilities and items.
- `itemsmith.bypass.*` permissions for staff to bypass cost, cooldown, and region gates.

**In-game GUI creator (M5).**
- Admin-only chest-GUI item creator built on triumph-gui plus the native Paper Dialog API.
- Template and blank starts, a schema-driven item editor, an activator editor, and categorized +
  searchable pickers for activators, conditions, targeters and actions.
- Generic parameter editors per parameter type, with a chat-prompt fallback for long or MiniMessage
  values.
- Immutable item id with a **Save As** flow; every edit writes back to the same YAML files, so
  GUI-built and hand-written items are fully interchangeable.

**Obtaining (M6).**
- Recipes for all Bukkit-native families: shaped, shapeless, the smelting family
  (furnace / blasting / smoking / campfire), smithing transform, and stonecutter — multiple recipes
  per item, surfaced through the vanilla recipe book.
- Direct drops from mobs and blocks with chance, count, player-kill requirement, and a silk-touch
  policy.
- Loot-table injection into chests, mob drops, and fishing, matched by exact key, namespaced key, or
  prefix/wildcard.
- Player-facing catalog GUI (behind `itemsmith.catalog`, granted by default) showing each item and
  how to obtain it.
- In-GUI recipe / drops / loot editors and admin give.

**Commands.**
- `/itemsmith` with subcommands `catalog`, `open`, `create`, `get`, `give`, `list`, `reload`.
- Aliases `/ismith` and `/citems`.

**Integrations (optional soft-depends, all graceful when absent).**
- Vault, WorldGuard, GriefPrevention, and PlaceholderAPI are detected at enable and used only if
  present; ItemSmith runs fully without any of them.

### Packaging

- Single drop-in jar. The GUI stack (`triumph-gui-paper`) is shaded and relocated under
  `mastrjimbo.itemsmith.libs.triumph` so it can't clash with another plugin's copy.
- Targets Paper API 1.21, tested on Paper 1.21.8, compiled for Java 21.

### Known limitations at 1.0.0

- **Resource-pack generation is not included yet.** Custom textures/models are **bring-your-own-pack**
  (point `item-model` or `custom-model-data` at your own pack); the creator GUI uses a native look.
- **PlaceholderAPI placeholders** exposed *by* ItemSmith, deeper MythicMobs integration,
  localization, and item import/export are not in this release.
- **Custom blocks and custom mobs** are post-launch milestones — 1.0.0 is items-only.
- **Folia** compatibility is unconfirmed.
- Brewing-stand and anvil recipe types are reserved names but not yet supported.

[Unreleased]: #
[1.0.0]: #
