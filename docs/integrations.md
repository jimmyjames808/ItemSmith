# Integrations

[← Back to index](README.md) · Related: [Gates & Economics](gates.md) ·
[Installation](installation.md)

ItemSmith runs on a bare Paper server with **no required dependencies**. Four plugins are
**optional soft-depends** — install them only for the features they unlock. ItemSmith detects
each one at boot and **degrades gracefully** when it's missing: a feature that needs an absent
plugin becomes a safe no-op (or a documented fail-open / fail-closed default) rather than
breaking the item.

At startup you'll see which were detected, e.g.:

```
[ItemSmith] Integrations: Vault=true WorldGuard=true GriefPrevention=false
```

| Plugin | Unlocks | When absent |
|---|---|---|
| [Vault](#vault) (+ an economy) | Money costs, economy conditions & actions | Money costs skipped; money actions no-op |
| [WorldGuard](#worldguard) | Region gate & region conditions | `in-region` fails closed; `can-build` fails open |
| [GriefPrevention](#griefprevention) | Claim-aware build checks & `respect-claims` | Build checks fail open (allowed) |
| [PlaceholderAPI](#placeholderapi) | *(nothing yet — planned for M7)* | No change |

---

## Vault

**Unlocks the money economy features.** Vault is an economy *bridge* — it needs an actual
economy provider behind it (e.g. **EssentialsX** with its economy module). ItemSmith considers
the economy available only when **both** Vault *and* a provider are present.

What it enables:

- The **[`money` use-cost](gates.md#use-cost)** in an ability gate.
- The **economy conditions** — [`has_money`, `balance_above`, `balance_below`](conditions-targeters.md#economy-needs-vault).
- The **economy actions** — [`give_money`, `take_money`, `set_money`, `pay_target`](actions.md#economy--resources).

**When Vault or the provider is absent** (graceful degradation):

- A `money` **cost is skipped** — the ability still fires, and the player is simply not
  charged (money never blocks an item on a non-economy server).
- Economy **conditions** report a balance of 0 (so `has_money` / `balance_above` are false,
  `balance_below` is true).
- Economy **actions** are no-ops.

Everything non-money about the item works normally. To enable: install Vault + an economy
plugin and restart; confirm `Vault=true` in the boot log.

---

## WorldGuard

**Unlocks region gating.** Requires WorldGuard (and its dependency WorldEdit).

What it enables:

- The **[region gate](gates.md#region)** keys `in-region` and `can-build`.
- The **region conditions** — [`in_region`, `is_region_member`, `can_build`](conditions-targeters.md#region-needs-worldguard).

Behavior:

- `in-region` / `in_region` / `is_region_member` require standing inside the named region.
  **When WorldGuard is absent these fail closed** (the ability is denied / the condition is
  false) — an unverifiable region-lock must not silently become free.
- `can-build` / `can_build` check whether the player may build at their location. This respects
  a player's WorldGuard **bypass** permission (bypassing players always "can build"). **When no
  protection plugin is present, the build check fails open** (allowed).

To enable: install WorldGuard + WorldEdit and restart; confirm `WorldGuard=true`.

---

## GriefPrevention

**Adds claim-awareness to the build checks.** When GriefPrevention is installed, the
"can build here" logic also consults GP land claims (via its `allowBuild` API), on top of any
WorldGuard result.

What it affects:

- The **`can-build`** gate / `can_build` condition — now also denied inside claims the player
  can't build in.
- The **[`respect-claims`](gates.md#region)** gate flag — block-editing actions (`set_block`,
  `break_block`, `strike_lightning`, `explosion`, …) consult claims before editing, so a
  claim-respecting item can't grief protected land.

**When GriefPrevention (and WorldGuard) are both absent**, build checks **fail open** — there's
no protection to enforce, so nothing is blocked. GP is detected by presence; no configuration
is needed.

---

## PlaceholderAPI

Declared as a soft-depend for a **future** milestone (M7), but **not yet wired**. ItemSmith
does not currently register any PlaceholderAPI expansion or placeholders — installing
PlaceholderAPI changes nothing in this build. Don't rely on `%itemsmith_...%` placeholders yet.

> **Not to be confused with** ItemSmith's own `{player}` and `{item}` tokens in the
> [command actions](actions.md#command) (`run_command_player`, `broadcast`, etc.). Those are
> ItemSmith's built-in substitutions and work with or without PlaceholderAPI.

---

## How detection works

At enable, ItemSmith checks whether each soft-depend plugin is loaded and wires a *null-object*
adapter when it isn't — so the missing-plugin path is a first-class, tested code path, not a
crash. All the WorldGuard/WorldEdit class references are isolated so the plugin links cleanly on
servers that don't have them. You never need to configure which integrations are on; it's
automatic and reflected in the boot log.

---

## See also

- [Gates & Economics](gates.md) — the cost/region features these plugins back.
- [Installation → Optional integration setup](installation.md#optional-integration-setup).
- [Troubleshooting](troubleshooting.md) — when a money/region feature isn't behaving.
