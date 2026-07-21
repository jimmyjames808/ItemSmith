# Commands & Permissions

[← Back to index](README.md) · Related: [The GUI Creator](gui-creator.md) · [Gates](gates.md)

---

## Commands

The base command is **`/itemsmith`**, with aliases **`/ismith`** and **`/citems`**. Everything
is a subcommand:

```
/itemsmith <catalog|open|create|get|give|list|reload>
```

Running `/itemsmith` with no arguments prints usage — the full admin line for admins, or just
`/itemsmith catalog` for everyone else.

| Subcommand | Usage | Permission | Who | What it does |
|---|---|---|---|---|
| `catalog` | `/itemsmith catalog` | `itemsmith.catalog` | player | Opens the read-only [player catalog](obtaining.md#the-player-catalog). |
| `open` | `/itemsmith open` | `itemsmith.admin` | player | Opens the [GUI creator](gui-creator.md) home (the item list). |
| `create` | `/itemsmith create` | `itemsmith.admin` | player | Opens the [creator](gui-creator.md#creating-a-new-item) template picker for a new item. |
| `get` | `/itemsmith get <id>` | `itemsmith.admin` | player | Gives the item to **yourself**. |
| `give` | `/itemsmith give <id> [player]` | `itemsmith.admin` | player/console | Gives the item to **another player** (or yourself if `[player]` is omitted and you're a player; console must name a player). |
| `list` | `/itemsmith list` | `itemsmith.admin` | anyone | Lists all loaded item ids. |
| `reload` | `/itemsmith reload` | `itemsmith.admin` | anyone | Reloads every item file, re-registers recipes, re-indexes drops & loot; reports the item count. |

### Notes

- **`get` vs `give`.** `get <id>` always targets you. `give <id> [player]` targets the named
  player; with no name it defaults to you (as a player) — from the console you must name a
  player.
- **Unknown id.** `get`/`give` with an unknown id replies *"Unknown item id '<id>'. Try
  /itemsmith list."*
- **Permission gate.** `catalog` is checked first (so non-admins can browse but reach nothing
  else); every other subcommand requires `itemsmith.admin`.
- **Tab completion:**
  - First argument → the subcommands you're allowed to use (`catalog` only shows with
    `itemsmith.catalog`; the rest only with `itemsmith.admin`).
  - Second argument of `get`/`give` → loaded item ids.
  - Third argument of `give` → online player names.

---

## Permissions

All permissions are declared in `plugin.yml`.

| Permission | Default | Grants |
|---|---|---|
| `itemsmith.admin` | `op` | The creator GUI and admin commands: `open`, `create`, `get`, `give`, `list`, `reload`. |
| `itemsmith.catalog` | `true` (everyone) | The read-only `/itemsmith catalog` browser. |
| `itemsmith.bypass.all` | `false` | Skips **all** ability gates except permission: region, cooldown groups, and the whole cost block (money, XP, hunger, items, **and charges**). |
| `itemsmith.bypass.cost` | `false` | Skips ability use-costs — money, XP, items, hunger, **and charges**. |
| `itemsmith.bypass.cooldown` | `false` | Skips ability `cooldown-group` checks. |
| `itemsmith.bypass.region` | `false` | Skips ability region gating (`in-region`, `can-build`). |

### About the bypass permissions

- They affect the **[gate](gates.md)** stage only — the per-item cost/cooldown/region rules an
  item author sets on an ability.
- **Permission gates are never bypassed** — there is no `itemsmith.bypass.permission`. A
  `permission:` requirement on an ability always applies.
- `itemsmith.bypass.cost` (and `.all`) also skip **charges** — charged uses are neither checked
  nor decremented for those players.
- The per-ability `cooldown` (the client greyed-out sweep) is **not** a gate and is not skipped
  by `itemsmith.bypass.cooldown`; only the shareable `cooldown-group` is.

See [Gates → Bypass permissions](gates.md#bypass-permissions) for the full breakdown.

---

## Quick reference

```text
/itemsmith catalog                 # browse items (everyone)
/itemsmith open                    # creator home (admin)
/itemsmith create                  # new item wizard (admin)
/itemsmith get <id>                # give yourself an item (admin)
/itemsmith give <id> [player]      # give someone an item (admin)
/itemsmith list                    # list item ids (admin)
/itemsmith reload                  # apply file changes (admin)
```

---

## See also

- [The GUI Creator](gui-creator.md) — what `open` / `create` launch.
- [Obtaining Items](obtaining.md) — the catalog and how players earn items.
- [Gates & Economics](gates.md) — what the bypass permissions skip.
