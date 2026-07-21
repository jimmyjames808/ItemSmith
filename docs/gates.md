# Gates & Economics

[← Back to index](README.md) · Related: [YAML Reference](yaml-reference.md) ·
[Integrations](integrations.md) · [Commands & Permissions](commands-permissions.md)

A **gate** governs *whether an ability is allowed to fire and what it costs*. It runs before
the targeter and actions, so a denied ability does nothing and **charges nothing**. Gates are
how you build permission-locked, priced, cooldown-limited and region-restricted items.

Gate keys are written **flat on the ability** (like `cooldown`), and every one is optional:

```yaml
abilities:
  - activator: sneak_right_click
    targeter: self
    actions:
      - type: blink
        distance: 8

    # ---- the gate ----
    permission: myserver.blink
    charge-cost: 1
    cooldown-group:
      key: blink
      seconds: 5
    region:
      can-build: true
      respect-claims: true
    cost:
      money: 20
      xp-levels: 1
      hunger: 2.0
      items:
        - material: ENDER_PEARL
          amount: 1
    deny-message: "<red>Can't blink: <reason></red>"
```

---

## Evaluation order

Checks run **in this exact order** and **fail fast** — the first failing check denies the
ability. Because the checks are pure (no mutation), and every cost is only committed *after all
checks pass*, gating is **atomic**: there's no partial charge and no refund path.

```
permission → region → cooldown-group → charges → money → xp-levels → xp → hunger → items
```

On denial the ability is skipped, nothing is consumed, and the optional `deny-message` is sent.

---

## Permission

```yaml
permission: myserver.vip.firesword
```

The caster must hold this permission node or the ability is denied. **Permission is pure access
control and is never skipped by the [bypass permissions](#bypass-permissions)** — there is no
`itemsmith.bypass.permission`.

This is distinct from a [`has_permission` condition](conditions-targeters.md#identity-caster):
the gate denies the *whole ability* (and can show a deny message), while a condition just
gates the actions.

---

## Region

Region gating restricts an ability by *where the player is standing*. It uses
[WorldGuard](integrations.md#worldguard) (and optionally [GriefPrevention](integrations.md#griefprevention)
claims for the build check).

```yaml
region:
  in-region: spawn          # must be inside the WorldGuard region "spawn"
  can-build: true           # must be allowed to build at their location
  respect-claims: true      # block-editing actions honor claims/build rights
```

| Key | Requires | Behavior when the plugin is absent |
|---|---|---|
| `in-region` | Standing inside the named **WorldGuard** region. | **Fails closed** — the ability is denied (ItemSmith can't verify the region, so it won't silently become free). |
| `can-build` | Being allowed to build at the caster's own location (WorldGuard BUILD flag / GriefPrevention claim). | **Fails open** — allowed when no protection plugin objects. |
| `respect-claims` | *(not a gate check)* — when `true`, the ability's block-editing actions (`set_block`, `break_block`, `strike_lightning`, `explosion`, …) consult protection before editing, so they can't grief protected land. | No effect without a protection plugin. |

> `respect-claims` is opt-in and defaults off to preserve existing behavior. Turn it on for any
> world-altering item you want to be claim-safe.

---

## Use-cost

A `cost:` block charges the player **at the moment the ability fires** (start of execution).
Every sub-field is optional; all present costs must be affordable or the ability is denied and
**nothing** is taken.

```yaml
cost:
  money: 25                 # Vault currency  (needs Vault + an economy)
  xp-levels: 2              # whole XP levels
  xp: 100                   # raw XP points
  hunger: 3.0               # food + saturation drained
  items:                    # consumed inventory ingredients
    - material: EMERALD
      amount: 2
    - material: GLOWSTONE_DUST
      amount: 1
```

| Field | Checks | Charges | Needs |
|---|---|---|---|
| `money` | Vault balance ≥ amount | Withdraws via Vault | [Vault + economy](integrations.md#vault) |
| `xp-levels` | XP level ≥ amount | Subtracts whole levels | — |
| `xp` | Total XP points ≥ amount | Deducts raw points | — |
| `hunger` | food + saturation ≥ amount | Drains saturation first, then food | — |
| `items` | Inventory has each `{material, amount}` | Removes them | — |

> **No Vault, no problem.** If Vault or an economy provider is missing, a `money` cost is
> silently **skipped** (never charged) rather than blocking the item — see
> [Integrations](integrations.md#vault). The XP, hunger and item costs need no plugin.

---

## Charges

Charges are a per-item counter (think wand "casts" or a talisman's uses). Define the counter at
the [top level of the item](yaml-reference.md#top-level-fields), then spend it per ability via
`charge-cost`.

```yaml
charges: 5                  # starting charges
max-charges: 5              # capacity (defaults to `charges`) — used by add_charges
on-depletion: keep_inert    # what happens when charges hit 0
durability-bar: true        # show charges as the vanilla durability bar

abilities:
  - activator: right_click
    charge-cost: 1          # this cast spends one charge
    targeter: looking_at_entity
    actions:
      - type: strike_lightning
    deny-message: "<red>Out of charge.</red>"
```

- A use is **denied** when the item's current charges are below `charge-cost`.
- On a successful use the charges are decremented. When they reach **0**, the
  **`on-depletion`** policy decides what happens to the item:

| `on-depletion` | Effect at 0 charges |
|---|---|
| `consume` *(default)* | Remove one from the stack — the "used it up" behavior. |
| `break` | Remove one from the stack **and** play the item-break effect (a wand "snaps"). |
| `keep_inert` | Leave the item at 0 charges. It stays in the inventory but its charged abilities keep failing the charge check until it's recharged. |

Recharge with the [`add_charges`](actions.md#economy--resources) (clamped to `max-charges`) or
`set_charges` actions — e.g. a passive `hold_tick` ability that trickles charges back in
daylight (see the [Stormcaller example](yaml-reference.md#a-complete-annotated-example)).

- **Showing the count.** Charges track and deplete regardless of lore. Put `<charges>` /
  `<max_charges>` tokens anywhere in your lore to print the numbers where you want them; if the
  item has charges but you include no `<charges>` token (and no `durability-bar`), ItemSmith
  appends a `Charges: N/M` line automatically so the counter is always visible.
- **`durability-bar: true`** mirrors the charge counter onto the vanilla durability bar, so
  players see charges as a depleting green→red bar.

---

## Cooldowns

ItemSmith has **two** independent cooldown systems — pick the right one:

### Per-ability `cooldown`

A plain number of seconds set directly on the ability. Each ability cools down **independently
and per-player**, and it drives the vanilla greyed-out **cooldown sweep** on the item.

```yaml
- activator: right_click
  cooldown: 3            # 3 seconds, this ability only
  # ...
```

Use this for the common "this ability has a cooldown" case.

### Gate `cooldown-group`

A **named, shareable** cooldown. Any ability — on the same item *or a different item* — that
declares the same `key` shares one timer per player. Perfect for a family of items that should
share a cooldown (e.g. every teleport item on one `teleport` cooldown).

```yaml
cooldown-group:
  key: teleport         # the shared name
  seconds: 30
```

| | Per-ability `cooldown` | Gate `cooldown-group` |
|---|---|---|
| Scope | This one ability | Every ability sharing the `key` |
| Unit | seconds | seconds |
| Per-player | yes | yes |
| Client sweep | yes (greyed-out item) | no |
| Bypassable | no | yes (`itemsmith.bypass.cooldown`) |
| Shared across items | no | yes, by `key` |

You can use both at once — e.g. a short per-ability `cooldown` for the client sweep plus a long
shared `cooldown-group` for balance.

---

## Deny messages

By default a denied ability is **silent**. Add `deny-message` to tell the player why. Three
tokens are substituted:

```yaml
deny-message: "<red>Denied (<reason>). Need <needed>.</red>"
```

| Token | Expands to |
|---|---|
| `<reason>` | the failing check: `permission`, `region`, `cooldown`, `charges`, `money`, `xp`, `hunger`, or `items` |
| `<needed>` | what was required (e.g. `5 levels`, `2x emerald`, remaining cooldown seconds) |
| `<remaining>` | same value as `<needed>` (handy phrasing for cooldowns) |

The message renders as MiniMessage.

---

## Bypass permissions

Four permissions let staff skip gate checks. All default to `false`.

| Permission | Skips |
|---|---|
| `itemsmith.bypass.all` | region **and** cooldown-group **and** the entire cost block (money, XP, hunger, items, **and** charges). Does **not** skip `permission`. |
| `itemsmith.bypass.region` | region checks (`in-region`, `can-build`). |
| `itemsmith.bypass.cooldown` | the `cooldown-group` check (does not affect the per-ability `cooldown` client sweep). |
| `itemsmith.bypass.cost` | the whole cost block — money, XP, hunger, items, **and charges** (charges live inside the cost stage, so they are neither checked nor decremented). |

See [Commands & Permissions](commands-permissions.md#permissions) for the full permission list.

---

## See also

- [YAML Reference → The Gate](yaml-reference.md#the-gate-optional-governance) — the raw schema.
- [Integrations](integrations.md) — exactly how Vault / WorldGuard / GriefPrevention back the
  money and region features, and how absence degrades.
- [Actions → Economy & Resources](actions.md#economy--resources) — the money/charge/cooldown
  actions.
