# Installation

[← Back to index](README.md)

This page covers requirements, installing the jar, first boot, the items folder, and the
optional integrations.

---

## Requirements

| Requirement | Details |
|---|---|
| **Server software** | **Paper 1.21.8** (or a Paper fork such as Purpur). ItemSmith uses Paper-only APIs — it will **not** run on Spigot or CraftBukkit. |
| **Java** | **Java 21** or newer. |
| **Client** (players) | Any 1.21.8 client works for using items. The **in-game creator GUI** additionally needs a **1.21.6+ client** for its text/number input dialogs — see [The GUI Creator](gui-creator.md). |

> **Folia:** not confirmed. ItemSmith targets standard Paper. Do not assume Folia support.

ItemSmith has **no required dependencies** — it runs on a bare Paper server. Everything in
the [Integrations](integrations.md) list is optional.

---

## Installing the jar

> ItemSmith is currently **pre-release** and built from source. There is no public download
> yet. Build the jar (a single, self-contained drop-in) and install it like any plugin.

1. **Build** the plugin jar. From the project root:
   ```bash
   ./gradlew build
   ```
   The shaded, ready-to-run jar is produced under `build/libs/`. It bundles its one GUI
   library (triumph-gui) *relocated* inside the jar, so there is nothing else to install and
   no classpath conflicts.
2. **Drop** the jar into your server's `plugins/` folder.
3. **Start** (or restart) the server.

There is **no `config.yml`** to edit — ItemSmith is configured entirely through the per-item
YAML files described below and in the [YAML Reference](yaml-reference.md).

---

## First boot

On first enable, ItemSmith:

1. Registers its engine — you'll see a boot line reporting the component count, for example:
   ```
   [ItemSmith] ItemSmith enabled: 2 item(s), 404 engine component(s).
   ```
   The `404 engine component(s)` is the sum of all activators, conditions, targeters and
   actions in this build (150 + 73 + 18 + 163). The item count is however many `*.yml` files
   it loaded.
2. Logs which optional integrations it detected, for example:
   ```
   [ItemSmith] Integrations: Vault=true WorldGuard=true GriefPrevention=false
   ```
3. Creates the folder `plugins/ItemSmith/items/` **if it does not already exist**, and copies
   in the two example items **`venom_blade.yml`** and **`frost_axe.yml`**. (Defaults are only
   seeded on the very first run — once the folder exists, ItemSmith never overwrites it.)

If either boot line is missing or shows an error, see [Troubleshooting](troubleshooting.md).

---

## The items folder

```
plugins/
└── ItemSmith/
    └── items/
        ├── venom_blade.yml
        ├── frost_axe.yml
        └── <your_item>.yml
```

- **One item per file.** The file name (minus `.yml`) is the item's **id** — e.g.
  `venom_blade.yml` defines the item `venom_blade`.
- **Ids must be lowercase** and use only `a-z`, `0-9` and `_`. Files with an invalid id are
  skipped with a warning.
- Items load in filename order.
- These files are the **single source of truth**. They are hand-editable and shareable — drop
  someone else's `.yml` into your `items/` folder and run `/itemsmith reload`. The in-game GUI
  writes these same files.

To apply changes without restarting, run:

```
/itemsmith reload
```

This re-reads every file in `items/`, re-registers recipes, and re-indexes drops and loot.
Any parse problems are logged as warnings (the rest of the item still loads where possible).

---

## Optional integration setup

All integrations are **soft-depends**: install them only if you want the features they unlock.
ItemSmith detects them automatically at boot and degrades gracefully when they're absent. See
[Integrations](integrations.md) for the full behavior matrix.

### Vault + an economy (for money costs/rewards)

Money [use-costs](gates.md#use-cost) and the economy actions (`give_money`, `take_money`,
`set_money`, `pay_target`) need **Vault** *and* an economy provider (e.g. EssentialsX).

1. Install **Vault**.
2. Install an economy plugin that registers with Vault (e.g. **EssentialsX** with
   `EssentialsXEconomy`).
3. Restart. The boot log should show `Vault=true`.

If Vault or the economy provider is missing, money costs are simply **skipped** (never
charged) and money actions become no-ops — the item still works otherwise.

### WorldGuard (for region gating)

Region [gates](gates.md#region) (`in-region`, `can-build`) and the region conditions
(`in_region`, `is_region_member`, `can_build`) use **WorldGuard**.

1. Install **WorldGuard** (and its dependency **WorldEdit**).
2. Restart. The boot log should show `WorldGuard=true`.

Note: a region *name* requirement **fails closed** when WorldGuard is absent (the ability
won't fire, because ItemSmith can't verify the region). See [Gates](gates.md#region).

### GriefPrevention (for claim respect)

The `respect-claims` gate flag and `can-build` check can additionally consult
**GriefPrevention** claims when it's installed. With no protection plugin present, "can build"
checks **fail open** (allowed). See [Integrations](integrations.md#griefprevention).

### PlaceholderAPI

Declared as a soft-depend for the future, but **not yet wired** — ItemSmith does not register
any placeholders in this build (planned for M7). Installing PlaceholderAPI currently changes
nothing.

---

## Next steps

- **[Quick Start](quick-start.md)** — build your first item, in the GUI and by hand.
- **[Commands & Permissions](commands-permissions.md)** — the command set and who can use it.
