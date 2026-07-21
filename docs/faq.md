# ItemSmith — Frequently Asked Questions

The questions server owners actually ask. If yours isn't here, check the
[troubleshooting guide](troubleshooting.md) or the [docs index](README.md).

> **Status:** ItemSmith is **pre-release** — feature-complete through obtaining (M0–M6) and in polish
> toward its first public release. It is **not downloadable yet**.

## The basics

### What does it do, in one sentence?
It lets you create custom weapons, tools, armor, wearables and consumables with deep scripted
abilities, crafting recipes, drops and loot, all through a full **in-game chest-GUI item creator**.

### What Minecraft versions does it support?
**Paper 1.21.8** (built against `paper-api` 1.21.8-R0.1, API version 1.21), on **Java 21**. Paper
forks on the same API (Purpur, etc.) should work. It is **not** a Spigot/Bukkit-only plugin — it uses
Paper APIs. Other major versions are not supported by the first release.

### Does it need a resource pack?
**No — not to work.** Items use their base material's normal look by default, and the creator GUI uses
a native, vanilla-style look. You only need a resource pack if *you* want **custom textures/models** on
your items, in which case you supply your own pack and point an item at it with `item-model` (1.21.4+)
or `custom-model-data`. **Automatic resource-pack generation is planned but not in the first release**
— today ItemSmith is bring-your-own-pack.

### Is it downloadable now?
Not yet. ItemSmith is pre-release. When it launches it will be a single drop-in jar on Modrinth. Star
or watch the project to be notified.

## Setup & dependencies

### What do I need installed?
Just **Paper 1.21.8** and **Java 21**. Everything else is optional.

### Does it require any other plugin (like a separate engine)?
No. ItemSmith is a **single drop-in jar** with **no required companion plugin**.

### Which integrations does it support, and are they required?
All optional **soft-depends**, and ItemSmith runs fine without any of them:
- **Vault** — ability use-costs / economy (money, and it also handles XP/items/hunger costs natively).
- **WorldGuard** — region-restricted abilities.
- **GriefPrevention** — claim build-checks.
- **PlaceholderAPI** — detected if present. *Note:* placeholders exposed **by** ItemSmith are a
  planned (M7) feature, not in the first release.

If an integration plugin isn't installed, the features that depend on it simply don't apply — nothing
errors. Region checks are the one exception that **fails closed**: a region-locked ability won't fire
if WorldGuard isn't present, so a lock can't be silently bypassed.

## Using it

### Can players (not just admins) make items?
The **item creator is admin-only** (`itemsmith.admin`, op by default) — item creation is a staff/build
task. Regular players get the **catalog** (`itemsmith.catalog`, granted by default): a GUI that shows
the custom items and how to obtain them. Players get items by crafting, drops, loot, or being given
them — not by opening the creator.

### Do I have to use the GUI, or can I edit files?
Either — they're the same thing. Every item is a human-readable YAML file at
`plugins/ItemSmith/items/<id>.yml`. The GUI reads and writes those exact files, so you can build in
the GUI, hand-edit in a text editor, or mix both, then `/itemsmith reload`.

### How do I share an item with another server?
Copy its `.yml` file from `plugins/ItemSmith/items/` to the other server's `items/` folder and run
`/itemsmith reload`. Items are self-contained and portable. (A dedicated import/export feature is
planned for later.)

### How complex can an item's abilities get?
Very. An item can have **any number of abilities**, and each ability is a pipeline of
**Activator → Conditions → Targeter → Actions** with an optional **Gate**. You get 150 activators,
163 actions (including nested `delay` / `repeat` / `if` / `random` flow control), 73 conditions, and
18 targeters to compose. There is **no one-ability limit**.

### Can I gate an item behind a rank, cost, or region?
Yes. Abilities support **permission gates**, **world/region restriction** (WorldGuard), **per-use
costs** via Vault (money, XP levels or points, items, hunger — any combination), **charges** and
**custom durability**, and shared **cooldown groups**.

### How do players obtain custom items?
Several ways, all configurable: **crafting** (shaped, shapeless, furnace/blast/smoker/campfire,
smithing, stonecutter — multiple recipes per item, shown in the vanilla recipe book), **mob/block
drops** (with chance, count, player-kill requirement, silk-touch policy), **loot-table injection**
(chests, mobs, fishing), and admin commands (`/itemsmith get`/`give`).

## Performance & operations

### Will it lag my server?
ItemSmith precompiles item pipelines at load, uses cheap persistent-data lookups to identify items,
and keeps per-event listener paths light; tick/passive abilities are opt-in per item. A performance
pass on the hot paths is part of the pre-release hardening. As always, heavy abilities (large-radius
targeters, frequent tick effects) cost more than simple ones — measure on your own hardware with your
own items.

### Does it work with Folia?
**Unconfirmed.** ItemSmith targets Paper 1.21.8. Folia's threading model hasn't been validated yet, so
don't assume Folia support at launch.

### Will updating ItemSmith break my items?
The YAML schema is designed to be stable once released, and items are plain files you control.
**During pre-release**, component names and defaults can still change — pin a build and re-test after
updating. After 1.0.0, breaking changes will be called out in the [changelog](../CHANGELOG.md).

## Roadmap

### What's on the roadmap?
Next up after launch: PlaceholderAPI placeholders and deeper integrations (M7), resource-pack
generation (M8), then custom blocks and mobs. These are **plans, not promises** — see
[ROADMAP.md](../ROADMAP.md).
