# Contributing to ItemSmith

Thanks for your interest in ItemSmith. It's a solo-built custom-items plugin for
Paper 1.21.8, and it's currently **pre-release** — the code is moving toward its first public
release. Contributions, bug reports, and feedback are all welcome.

> Because ItemSmith is pre-release, the YAML schema and component names can still change. If you're
> planning a larger change, please open an issue to discuss it first so we don't collide.

## Ways to help

- **Report bugs.** Include your Paper version, Java version, the item YAML that reproduces it, and
  the relevant console output. A minimal `items/<id>.yml` that triggers the problem is gold.
- **Suggest components or features.** Missing an activator, action, condition, or targeter you'd
  expect? Say so — the engine is designed to add these cheaply.
- **Improve docs and examples.** Clear docs and good example items are as valuable as code.
- **Contribute code.** New components are the easiest, highest-value contribution (see below).

## Building from source

Requirements:
- **JDK 21** (the project compiles for Java 21 and pins the Gradle JVM to a JDK 21 runtime).
- Paper API `1.21.8-R0.1` is pulled automatically from the PaperMC repository.

```bash
git clone <this-repo>
cd ItemSmith
./gradlew build         # produces build/libs/ItemSmith-1.0.0.jar
./gradlew runServer     # launches a Paper 1.21.8 test server (run-paper)
```

The build shades **`triumph-gui-paper`** and relocates it to `mastrjimbo.itemsmith.libs.triumph`, so
the final jar is a single drop-in with no classpath clashes. Don't add heavyweight runtime
dependencies — "single jar, minimal deps" is a core promise of the project. Integrations
(Vault, WorldGuard, GriefPrevention, PlaceholderAPI) are **compileOnly soft-depends**: never shade
them; guard every use behind a presence check with a graceful fallback.

## Project layout

All code lives under `src/main/java/mastrjimbo/itemsmith/`:

| Package | Responsibility |
|---------|----------------|
| `engine` | The pipeline: `CustomItem`, `Ability`, `AbilityContext`, `ItemBuilder`, the four component interfaces (`Activator`, `Condition`, `Targeter`, `Action`), and the action executor |
| `registry` | `Registries` + `BuiltinComponents` (where every built-in component is registered) + `Categories` |
| `param` | The typed-parameter system (`ParamType`, `ParamDef`, `ParamSchema`, `ParamCodec`) shared by the YAML serializer and the GUI |
| `store` | Per-item YAML load/save: `ItemParser`, `ItemSerializer`, `ItemStore` |
| `listener` | One listener per Bukkit event family (combat, interact, block, inventory, equip, projectile, movement, durability, …) |
| `gate` | Gating & economics: `Gate`, `CostSpec`, `RegionSpec`, cooldown groups, charges/durability policies |
| `gui` | The in-game creator: screens, forms, the native dialog bridge, edit sessions |
| `recipe` / `drops` / `loot` | Obtaining: recipe specs & manager, mob/block drops, loot-table injection |
| `integration` | Guarded soft-depend adapters: `VaultHook`, `WorldGuardBridge`, `ProtectionHook` |
| `command` | `/itemsmith` (`ItemCommand`) |
| `util` | Shared helpers (text/MiniMessage, particles, effects, targeting, config) |

## The core principle: schema-driven components

Every activator, condition, targeter, and action is a component that **declares its own typed
parameters** via a `ParamSchema`. From that one schema:

- the **YAML (de)serializer** knows how to read and write the component, and
- the **GUI** renders an editor for it automatically.

That's how ItemSmith reaches ~400 components without hand-writing 400 parsers or 400 menus. **Adding a
component is roughly one class plus one registration line — and it gets YAML support and a GUI editor
for free.** Keep it that way: don't special-case a component in the parser or the GUI when a schema
change would do.

### Adding a new component (the common case)

1. Create the class in the right package, e.g.
   `component/action/<bucket>/MyCoolAction.java`, implementing the `Action` interface (or
   `Condition` / `Targeter`). Declare its parameters in `schema()` and do the work in the run/test/
   resolve method.
2. Register it in `registry/BuiltinComponents.java`:
   - Actions, conditions, and targeters: add one `r.register(new MyCoolAction());` line in the
     matching `register…` method (they're grouped and alphabetized within a bucket — keep that order).
   - Activators are registered with the `act(r, …, category, "Display Name", "description")` helper.
3. Give it a stable, lowercase `type`/id (this is the YAML key players will type — treat it as a
   public API once released).
4. Add or update an example under `examples/` and a line in the relevant `docs/` page.
5. Test it live: `./gradlew runServer`, build an item that uses it, and confirm the behavior in-game.

## Coding conventions

- Match the surrounding style; UTF-8 source; target Java 21 (records, sealed types, and switch
  patterns are used throughout — `RecipeSpec` is a good example).
- Player-facing text uses MiniMessage via the `util/Text` helper — don't hardcode legacy `§` codes.
- Fail safe on gates: region checks fail **closed** when their plugin is absent (a region lock must
  never silently open); build/claim checks fail **open**. Preserve these semantics.
- Keep listener hot paths cheap — they run per event. Precompute where you can.

## Verifying a change

ItemSmith is tested by running it, not just by unit tests. Before proposing a change:

1. `./gradlew build` cleanly.
2. `./gradlew runServer`, confirm a **clean boot** (no warnings), then exercise the changed feature
   live — give an item, trigger the ability, craft it, or open the creator.

## License / CLA

ItemSmith is released under the **[MIT License](LICENSE)**. By contributing you agree your contribution
is licensed under the same MIT terms. If you're unsure, ask in your issue/PR.
