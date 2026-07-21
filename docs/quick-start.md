# Quick Start

[← Back to index](README.md)

Let's make your first custom item **two ways**: entirely in-game with the GUI creator, and by
hand-writing a YAML file. Both produce the *same* file on disk — pick whichever you prefer.

Our example item, **Ember Wand**, will be a blaze rod that, on right-click, launches a small
fireball of effects: it damages and ignites the mob you're looking at, plays a flame burst,
and puts itself on a short cooldown.

---

## Path A — build it in the GUI

You need `itemsmith.admin` (default: server operators) and a **1.21.6+ client**.

1. **Open the creator.**
   ```
   /itemsmith create
   ```
   This opens the **template picker**.

2. **Pick a template.** Choose **Weapon** (it pre-fills an `IRON_SWORD` and a
   *when you hit an entity* ability), or **Blank** to start empty. A dialog asks for the new
   item **id** — type `ember_wand` (lowercase, `a-z 0-9 _` only; this becomes the file name).

3. **Set the base fields** in the **item editor**:
   - **Material** → pick `BLAZE_ROD`.
   - **Name** → type a MiniMessage name, e.g. `<gold>Ember Wand</gold>`.
   - **Lore** → add a line or two of flavour text.

4. **Edit the ability.** Open **Abilities → (the ability) →**:
   - **Activator** → choose **Right Click** (`right_click`) from the Interact folder.
   - **Targeter** → choose **Looking At Entity** (`looking_at_entity`) so it affects the mob
     you're aiming at.
   - **Cooldown** → set `2` (seconds).
   - **Actions** → open the **action tree** and add, in order:
     - **Damage** (`damage`) → amount `6`.
     - **Ignite** (`ignite`) → seconds `4`.
     - **Particle Burst** (`particle_burst`) → particle `flame`.
     - **Play Sound** (`play_sound`) → `entity.blaze.shoot`.

5. **Save.** Back on the item editor, click **Save**. ItemSmith writes
   `plugins/ItemSmith/items/ember_wand.yml` and reloads. Click **Give me one** to test it.

That's it — you never touched a file. Everything you did is now in `ember_wand.yml`, exactly
as if you'd typed it. For a full tour of every screen, see [The GUI Creator](gui-creator.md).

---

## Path B — write the YAML by hand

Create `plugins/ItemSmith/items/ember_wand.yml` with the following. Every line is annotated;
the schema is documented in full in the [YAML Reference](yaml-reference.md).

```yaml
# plugins/ItemSmith/items/ember_wand.yml
# The file name (ember_wand) IS the item id. Give it with: /itemsmith get ember_wand

material: BLAZE_ROD                       # base Minecraft material (must be a valid item)
name: "<gold>Ember Wand</gold>"           # MiniMessage display name
lore:                                     # MiniMessage lore lines
  - "<gray>Right-click to scorch your foe.</gray>"

abilities:                                # one item can have as many abilities as you like
  - activator: right_click                # WHEN: you right-click holding the item
    targeter: looking_at_entity           # WHO: the entity you're looking at
    cooldown: 2                           # seconds between uses (shows the client cooldown sweep)
    actions:                              # WHAT: runs top-to-bottom on the target
      - type: damage                      # deal flat damage...
        amount: 6                         # 6.0 = three hearts
        from_caster: true                 # credit you as the attacker (knockback/kill credit)
      - type: ignite                      # ...set the target on fire...
        seconds: 4
      - type: particle_burst              # ...a dense flame puff at the target...
        particle: flame
      - type: play_sound                  # ...and a blaze shot sound
        sound: entity.blaze.shoot

recipe:                                   # optional: how players craft it (see obtaining.md)
  type: shaped
  shape:
    - " B "
    - " B "
    - " N "
  ingredients:
    B: BLAZE_ROD
    N: NETHER_STAR
```

Save the file, then in-game run:

```
/itemsmith reload
```

You should see `Reloaded N item(s).` Give yourself one to test:

```
/itemsmith get ember_wand
```

Right-click a mob while looking at it — it takes damage, catches fire, and you'll see the
flame burst and hear the blaze sound, with a 2-second cooldown sweep on the wand.

> **Param names matter.** Each action/condition/targeter has its own parameter keys (here
> `amount`, `from_caster`, `seconds`, `particle`, `sound`). If you mistype a key it's simply
> ignored and the default is used; if you mistype a *component `type`* the whole component is
> skipped with a warning. See [Actions](actions.md) and
> [Conditions & Targeters](conditions-targeters.md) for the exact keys.

---

## Understanding what you built

The ability is a pipeline (this is the heart of ItemSmith — see the
[YAML Reference](yaml-reference.md) for the full anatomy):

```
right_click  →  (no conditions)  →  looking_at_entity  →  [ damage, ignite, particle_burst, play_sound ]
  WHEN               IF                    WHO                              WHAT
```

- **No conditions** here, so the actions always run. Add a `conditions:` list to require, say,
  the player be sneaking or above half health.
- The **targeter** decides *who* the actions hit. Swap `looking_at_entity` for `radius` to hit
  everything nearby, or `self` to buff yourself instead.
- The **actions** run in order, top to bottom. Wrap them in flow actions like `delay`,
  `repeat`, `if` or `random` for timed combos and branching — see
  [Actions → Flow control](actions.md#flow-control).

---

## Where to go next

| I want to… | Read |
|---|---|
| Know every field and how they nest | [YAML Reference](yaml-reference.md) |
| See all the triggers | [Activators](activators.md) |
| See everything an item can *do* | [Actions](actions.md) |
| Filter *when* an ability fires and *who* it hits | [Conditions & Targeters](conditions-targeters.md) |
| Add costs, cooldowns, permissions, charges | [Gates & Economics](gates.md) |
| Let players craft / find the item | [Obtaining Items](obtaining.md) |
| Do it all in-game | [The GUI Creator](gui-creator.md) |
