# ItemSmith Documentation

**ItemSmith** is a custom-items plugin for **Paper 1.21.8** (Java 21).
Build weapons, tools, trinkets and gadgets with rich, scriptable abilities — either by
hand-editing a small YAML file or entirely in-game through a full chest-GUI creator.

> **Status:** ItemSmith is **pre-release**. It is built from source and self-hosted; it is
> not yet published to a download site. These docs describe the current build (engine
> milestones M0–M6 complete).

---

## Why ItemSmith?

- **Minimal dependencies.** ItemSmith runs on a vanilla Paper server with nothing else
  installed. Vault, WorldGuard, GriefPrevention and PlaceholderAPI are all *optional*
  soft-depends that unlock extra features when present and degrade gracefully when absent.
- **A real in-game creator.** A complete chest-GUI item creator that reads and writes the
  *same* YAML files you can hand-edit.
- **A deep, schema-driven engine.** ~400 building blocks (~150 activators, ~160 actions,
  ~70 conditions, ~18 targeters) that snap together into abilities. Nested flow control
  (delay, repeat, if, random) lets you build timed combos and branching effects.

---

## How an item works

Every item is a base Minecraft material plus display text plus a list of **abilities**.
Each ability is a small pipeline:

```
Activator  →  Conditions  →  Targeter  →  Actions          (optionally governed by a Gate)
  (when)        (if)          (who)        (what)
```

- **Activator** — the trigger. *Right-click, hit an entity, take fall damage, every tick…*
- **Conditions** — pass/fail checks. *Only if sneaking, only below 5 hearts…*
- **Targeter** — who or what the actions apply to. *The mob you hit, everyone in a radius…*
- **Actions** — what happens. *Deal damage, apply poison, teleport, spawn particles…*
- **Gate** — optional cost/permission/cooldown/region rules that guard the whole ability.

Items live one-per-file at `plugins/ItemSmith/items/<id>.yml`. That file is the single
source of truth: hand-editable, shareable, and reloadable with `/itemsmith reload`. The
in-game GUI reads and writes the exact same file.

---

## Table of contents

### Getting started
- **[Installation](installation.md)** — requirements, install steps, first boot, and the
  optional integrations.
- **[Quick Start](quick-start.md)** — make your first item two ways: in the GUI and by hand.

### Authoring items
- **[YAML Reference](yaml-reference.md)** — the complete per-item schema: every top-level
  field, the ability pipeline, gate keys, and the reserved keys (`invert`, `cooldown`, …).
- **[Activators](activators.md)** — the full categorized list of ~150 triggers.
- **[Actions](actions.md)** — the ~160 actions by bucket, with the flow-control actions
  (delay / repeat / if / random) and nested examples.
- **[Conditions & Targeters](conditions-targeters.md)** — the ~70 conditions (plus the
  `invert` flag) and ~18 targeters, with what each returns.
- **[Gates & Economics](gates.md)** — permission, region, use-cost (money / XP / items /
  hunger), charges, custom durability, cooldown groups, and the bypass permissions.

### Distributing items
- **[Obtaining Items](obtaining.md)** — recipes (all families), mob & block drops, loot-table
  injection, and the player catalog.

### Tools & reference
- **[The GUI Creator](gui-creator.md)** — a guided tour of the in-game chest-GUI creator.
- **[Commands & Permissions](commands-permissions.md)** — every command and permission.
- **[Integrations](integrations.md)** — Vault, WorldGuard, GriefPrevention, PlaceholderAPI.
- **[Troubleshooting](troubleshooting.md)** — common problems and how to read reload warnings.

### Background
- **[FAQ](faq.md)** — the questions server owners actually ask.
- **[Roadmap](../ROADMAP.md)** · **[Changelog](../CHANGELOG.md)** — where ItemSmith is headed.

---

## Two example items

ItemSmith ships two ready-to-read example files (copied into `plugins/ItemSmith/items/` on
first run): **`venom_blade.yml`** (a shaped-recipe diamond sword that poisons on hit) and
**`frost_axe.yml`** (a shapeless-recipe iron axe that chills). They are the quickest way to
see the schema in action — see the [Quick Start](quick-start.md) for a full walkthrough.

---

## Roadmap (not yet built)

These are planned but **not** part of the current build — don't rely on them yet:

- **M7** — integrations polish: PlaceholderAPI placeholders, deeper MythicMobs support,
  `messages.yml` localization, item import/export.
- **M8** — resource-pack **generation**. Until then, custom textures/models are
  *bring-your-own-pack*: `item-model` / `custom-model-data` render as the base material
  unless your server ships a resource pack that provides the model. (The GUI itself uses a
  native look and needs no pack.)
- **Post-launch** — custom blocks and custom mobs. Folia compatibility is unconfirmed.
