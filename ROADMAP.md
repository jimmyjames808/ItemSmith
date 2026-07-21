# ItemSmith Roadmap

This is where ItemSmith is going. It is a **statement of intent, not a set of promises** — scope,
order, and timing can change, and a solo developer builds one thing at a time. Nothing here is a
committed date. If a feature isn't in [CHANGELOG.md](CHANGELOG.md) under a shipped version, treat it
as "planned."

ItemSmith follows a **build-then-publish** approach: rather than a stream of half-features, it's
built to feature-completeness in internal milestones (each with a live in-game test) and
published once it's genuinely ready. The first public release is **1.0.0**, covering milestones
**M0–M6**.

## Shipped (targeted for the 1.0.0 release)

| Milestone | Delivered |
|-----------|-----------|
| **M0 — Engine** | Schema-driven component engine; per-item YAML store; item builder & identity |
| **M1 — Activators** | ~150 triggers across interaction, combat, movement, world, equip, projectile, lifecycle |
| **M2 — Actions** | ~162 actions including nested flow control (`delay`, `repeat`, `if`, `random`) |
| **M3 — Conditions & Targeters** | ~73 conditions, 18 targeters, wired into the ability pipeline |
| **M4 — Gating & Economics** | Permission, world/region (WorldGuard), use-cost (Vault: money/XP/items/hunger), charges, custom durability, cooldown groups |
| **M5 — GUI Creator** | In-game chest-GUI + native dialog item creator; templates; searchable pickers; live-save to YAML |
| **M6 — Obtaining** | All Bukkit-native recipe families; mob/block drops; loot-table injection; player catalog |

See [CHANGELOG.md](CHANGELOG.md) for the detailed 1.0.0 contents.

## In progress → 1.0.0 launch (M9 hardening)

The work between "feature-complete" and "published":

- Performance pass on listener hot paths and tick/passive activators.
- A Folia compatibility investigation and honest note (currently **unconfirmed**).
- Documentation, examples, and the Modrinth page + gallery.
- Final packaging and the release jar.

## Planned after launch

These are the next milestones, roughly in intended order. Order and scope may shift based on what
server owners ask for most.

### M7 — Integrations & polish
- **PlaceholderAPI placeholders** exposed *by* ItemSmith (item state in text, and in conditions).
- Deeper economy/protection integration and refinements to the existing Vault / WorldGuard /
  GriefPrevention hooks.
- Deeper **MythicMobs** interop.
- Localization (`messages.yml`) and item **import/export**.

### M8 — Resource-pack generation
- The big one people ask about. Ingest custom models/textures, assign `item_model` / CustomModelData,
  build a pack, and serve it (self-hosted HTTP or the server resource-pack), with pack management in
  the GUI.
- Until this lands, ItemSmith is **bring-your-own-pack**: you can already point items at a model in
  your own resource pack via `item-model` (1.21.4+) or `custom-model-data`, and the creator GUI uses
  a clean native look. This milestone just removes the manual pack step.
- It's the riskiest piece (asset pipeline + hosting + client push), so it's deliberately sequenced
  behind an already-shippable product.

### Custom blocks & custom mobs
- After items reach full depth: **custom blocks** and **custom mobs**, extending the same
  schema-driven engine to new content types. Post-launch, no committed timeline.

## Want to influence it?

Feature requests and feedback genuinely steer the order above. Open an
issue (once the repo is public) or reach out to **MastrJimbo**. See [CONTRIBUTING.md](CONTRIBUTING.md).
