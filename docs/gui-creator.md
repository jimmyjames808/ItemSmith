# The GUI Creator

[← Back to index](README.md) · Related: [YAML Reference](yaml-reference.md) ·
[Commands & Permissions](commands-permissions.md)

ItemSmith's headline feature: a complete **in-game chest-GUI creator**. Build and edit items
without ever opening a file — and everything you do writes the *same*
`plugins/ItemSmith/items/<id>.yml` you'd hand-author. It's the one free custom-items plugin
with a full item creator built in.

```
/itemsmith create      # start a brand-new item (template picker)
/itemsmith open        # the creator home — list & edit existing items
```

## Requirements

- **Permission:** `itemsmith.admin` (default: server operators). The creator creates, edits,
  saves and can give items. Non-admins only get the read-only
  [catalog](obtaining.md#the-player-catalog).
- **Client:** the chest menus work on any client, but the creator's **text/number/option
  inputs use Paper's native Dialog API, which needs a 1.21.6+ client.** If your client is
  older, ItemSmith tells you instead of leaving you stuck:
  > *The ItemSmith creator needs a 1.21.6+ client for this input.*

Under the hood the chest screens are built with a bundled GUI library (triumph-gui, relocated
inside the jar), and every value you type goes through a native Paper dialog — a clean,
resource-pack-free look.

---

## Creating a new item

1. **`/itemsmith create`** opens the **template picker** — six starting points:

   | Template | Pre-fills | Starter ability |
   |---|---|---|
   | **Weapon** | `IRON_SWORD` | fires on `player_hit_entity` |
   | **Tool** | `IRON_PICKAXE` | fires on `block_break` |
   | **Armor** | `IRON_CHESTPLATE` | fires on `equip` |
   | **Consumable** | `APPLE` | fires on `item_consume` |
   | **Wearable** | `CARVED_PUMPKIN` | fires on `equip` |
   | **Blank** | `PAPER` | no abilities |

2. Pick one → a dialog asks for the **item id** (`a-z 0-9 _`; this becomes the file name). It's
   validated for format and uniqueness.
3. You land in the **item editor** with the template's material and one starter ability
   (except Blank) ready to customize.

---

## The item editor

The hub for one item. A non-interactive preview tile shows the current item; the surrounding
tiles open editors:

- **Material** — pick any *item* material (block-only materials are rejected).
- **Name** — a MiniMessage text dialog.
- **Lore** — the [lore editor](#lore-editor).
- **Abilities** — the [ability list](#abilities).
- **Charges & settings** — the [settings screen](#charges--settings).
- **Recipes** — the [recipe editor](#recipes).
- **Drops** — the [drops editor](#drops).
- **Loot tables** — the [loot editor](#loot-tables).

Bottom bar: **Back to list**, **Give me one** (spawns the built item — save first if it's
new), **[Save As…](#save-vs-save-as)**, and **[Save](#save-vs-save-as)**.

### Lore editor

One row per lore line (MiniMessage). Left-click to edit a line, right/shift-right to move it
down/up, shift-left to remove, and **+ Add line**.

### Charges & settings

Toggle the [charge counter](gates.md#charges) on/off, set starting and max charges, choose the
**on-depletion** policy (`consume` / `break` / `keep_inert`), and toggle **durability bar**
(mirror charges onto the vanilla bar).

---

## Abilities

The **ability list** shows each ability (its activator and action/condition counts). Click to
edit, shift-click to remove, **+ Add ability** to seed a new one.

Opening an ability shows the **ability editor** — the pipeline hub:

- **Activator** → the [component picker](#the-pickers) over all triggers.
- **Conditions** → the [condition list](#condition-list).
- **Targeter** → left-click the picker to choose one; right-click to edit its params (if it has
  any) in the [param editor](#the-param-editor).
- **Cooldown** → a number dialog (per-ability seconds).
- **Gate · governance** → the [gate editor](#gate-editor).
- **Actions** → the [action-tree editor](#the-action-tree-editor).

### Condition list

Reused both for an ability's conditions and inside an `if` flow node. **+ Add condition** opens
the picker over all conditions; per entry you can left-click to edit params, **right-click to
toggle the `invert` flag**, and shift-click to remove. No conditions = the ability always fires.

---

## The action-tree editor

This edits one level of the action tree — a live, ordered list of actions. It's used for an
ability's root actions, a flow action's body, and a random branch's body, so **nesting is
uniform at every depth**.

- **+ Add action** → the picker over all actions.
- Per action: left-click **opens** it, right-click moves it down, shift-right moves it up,
  shift-left removes it.
- Opening a **leaf action** → the [param editor](#the-param-editor).
- Opening a **flow action** (`delay` / `repeat` / `if` / `random` / `chance`) → the
  [flow-node editor](#flow-nodes).

### Flow nodes

The flow-node editor is a hub tailored to the flow type. It shows only what that flow uses:

- **Parameters** (e.g. `ticks` for `delay`, `times` for `repeat`, `chance` for `chance`).
- **Conditions** — only for `if` (they decide `then` vs `else`), via the condition list.
- **Bodies** — each nested body (`do`, or `then`/`else` for `if`) drills back into a fresh
  action-tree editor.
- **Branches** — only for `random`, via the branch list.

### Branch list

A `random` node's weighted branches. **+ Add branch** (weight 1.0, empty body); per branch,
left-click to edit its actions, right-click to edit its weight (number dialog), shift-left to
remove.

---

## Gate editor

Edits the ability's [gate](gates.md). Tiles for: **Permission**, **Charge cost**, **Deny
message**, **Cooldown group** (key + seconds), **In-region** (WorldGuard) with **Must be able
to build** and **Respect claims** toggles, and the cost tiles **Money / XP levels / XP points /
Hunger**. (Item-ingredient costs are edited in the YAML for now.) The gate normalizes back to
"none" when you clear everything.

---

## Recipes

The **recipe list** holds an item's recipes — **+ Add recipe** appends one and opens the
**recipe editor**. Left-click to edit, shift-click to remove; empty recipes are pruned when you
go back.

The recipe editor has a **type selector** across every Bukkit-native family — `shaped`,
`shapeless`, `furnace`, `blasting`, `smoking`, `campfire`, `smithing`, `stonecutting` — and
reshapes itself accordingly: a 3×3 grid (shaped), an ingredient list (shapeless),
input + experience + cook-time (cooking), template + base + addition (smithing), or a single
input (stonecutting). Material cells open the value picker.

See [Obtaining → Recipes](obtaining.md#recipes) for what each family means.

---

## Drops

The **drops editor** has two lists — mob-death rules and block-break rules:

- **Mob drop** — pick mobs (multi-select; empty = any mob), chance, min/max, and **Require
  player kill**.
- **Block drop** — pick blocks (multi-select), chance, min/max, and **Silk Touch** policy
  (`any` / `require` / `forbid`).

See [Obtaining → Drops](obtaining.md#mob--block-drops).

---

## Loot tables

The **loot editor** holds loot-injection rules. Each rule has table-key patterns (with a
**+ Type pattern** button for wildcard prefixes like `chests/`), a chance, and a min/max. See
[Obtaining → Loot injection](obtaining.md#loot-table-injection).

---

## The pickers

Three reusable pickers power every "choose a…" step:

- **Component picker** — a paginated, **searchable** chest for choosing one engine component
  (an activator, condition, targeter or action). Entries are grouped by category with a
  per-category icon and sortable; the search button filters by id/name/category.
- **Value picker** — a paginated, searchable chest of registry values with icons, for
  materials, potion effects, particles, sounds, entity types, enchantments, biomes, worlds, and
  ItemSmith item references.
- **Collection picker** — multi-select for list values (mob types, block types, loot patterns),
  with an optional free-text entry for manual patterns.

### The param editor

Every component's parameters are edited by one **schema-driven** editor: one tile per
parameter, dispatched by its [type](yaml-reference.md#parameter-types) to the right input —
a checkbox for booleans, a number dialog for ints/doubles, an option dialog for enums, a value
picker for materials/effects/particles/etc., or a text dialog for strings and MiniMessage.
Head parameters (`head`, `trail_head` on the particle actions and `shoot_projectile`) open a
**heads-only** picker with a **"None (use particle)"** option that clears the head back to the
plain particle. Advanced parameters are hidden behind a **Simple/Advanced** toggle so common
edits stay simple.

---

## Save vs Save-As

- **Save** writes the current item to `<id>.yml` and reloads. You'll see
  *"Saved <id> — reloaded N item(s)."*
- **Save As…** prompts for a **new id**, then writes a **clone** to a new file with all the same
  fields, reloads, leaves the original untouched, and opens the clone for editing.

### The immutable-id rule

An item's **id is fixed at creation** — there is no "rename" control anywhere in the editor. The
id is also the YAML file name. To get a differently-named item, use **Save As**, which clones to
a new file. (This is enforced structurally: the draft's id has no setter, and the store derives
the id from the filename.)

---

## Same file, both ways

The GUI is not a separate format. When you open an item, ItemSmith parses the very same
`<id>.yml` into an editable draft; when you save, it serializes back to that file
(atomically) and reloads from disk. The round-trip is lossless — an item opened and saved
unchanged produces an identical file. That means you can freely mix workflows: rough something
out in the GUI, then fine-tune the YAML by hand (or vice-versa), and share the resulting
`.yml` with anyone.

---

## See also

- [Quick Start → Build it in the GUI](quick-start.md#path-a--build-it-in-the-gui).
- [YAML Reference](yaml-reference.md) — what every tile maps to on disk.
- [Commands & Permissions](commands-permissions.md) — `create`, `open`, and the admin perm.
