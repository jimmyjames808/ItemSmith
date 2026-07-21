package mastrjimbo.itemsmith.store;

import mastrjimbo.itemsmith.engine.Ability;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.engine.ActionNode;
import mastrjimbo.itemsmith.engine.Activator;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.engine.Configured;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.engine.FlowAction;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.component.condition.meta.InvertingCondition;
import mastrjimbo.itemsmith.drops.BlockDrop;
import mastrjimbo.itemsmith.drops.DropSources;
import mastrjimbo.itemsmith.drops.MobDrop;
import mastrjimbo.itemsmith.drops.SilkTouchPolicy;
import mastrjimbo.itemsmith.gate.CostSpec;
import mastrjimbo.itemsmith.loot.LootInjection;
import mastrjimbo.itemsmith.loot.LootRule;
import mastrjimbo.itemsmith.gate.DepletionPolicy;
import mastrjimbo.itemsmith.gate.Gate;
import mastrjimbo.itemsmith.gate.RegionSpec;
import mastrjimbo.itemsmith.param.ParamCodec;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.recipe.RecipeSpec;
import mastrjimbo.itemsmith.registry.Registries;
import mastrjimbo.itemsmith.util.Cfg;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Parses one item's YAML into a {@link CustomItem}, resolving activator/
 * condition/targeter/action ids against the {@link Registries} and reading each
 * component's parameters through the shared {@link ParamCodec}. Parsing is
 * defensive: an unknown component or bad value logs a warning and is skipped
 * rather than failing the whole item.
 */
public final class ItemParser {

    private static final String DEFAULT_TARGETER = "target";

    private final Registries registries;
    private final ParamCodec codec;
    private final Consumer<String> warn;

    public ItemParser(Registries registries, ParamCodec codec, Consumer<String> warn) {
        this.registries = registries;
        this.codec = codec;
        this.warn = warn;
    }

    /** @return the parsed item, or null if the base definition is invalid (bad material). */
    public CustomItem parse(String id, ConfigurationSection s) {
        Material material = Material.matchMaterial(s.getString("material", ""));
        if (material == null || !material.isItem()) {
            warn.accept("Item '" + id + "' has an invalid or missing 'material'; skipping.");
            return null;
        }

        NamespacedKey itemModel = null;
        String modelStr = s.getString("item-model");
        if (modelStr != null && !modelStr.isBlank()) {
            itemModel = NamespacedKey.fromString(modelStr.toLowerCase(Locale.ROOT));
            if (itemModel == null) {
                warn.accept("Item '" + id + "' has an invalid 'item-model': " + modelStr);
            }
        }

        Integer customModelData = s.contains("custom-model-data") ? s.getInt("custom-model-data") : null;
        String name = s.getString("name", "");
        List<String> lore = s.getStringList("lore");

        // M4 charge counter (null charges = no counter). Box explicitly: a mixed int/Integer ternary
        // triggers binary numeric promotion and would unbox `charges` (null) -> NPE for non-charge items.
        Integer charges = s.contains("charges") ? Integer.valueOf(s.getInt("charges")) : null;
        Integer maxCharges = s.contains("max-charges") ? Integer.valueOf(s.getInt("max-charges")) : charges;
        DepletionPolicy onDepletion = DepletionPolicy.from(s.getString("on-depletion", "consume"));
        boolean durabilityBar = s.getBoolean("durability-bar", false);

        List<Ability> abilities = parseAbilities(id, s);
        List<RecipeSpec> recipes = parseRecipes(id, s);
        DropSources drops = parseDrops(id, s.getConfigurationSection("drops"));
        LootInjection loot = parseLoot(id, s);

        return new CustomItem(id, material, itemModel, customModelData, name, lore, recipes, abilities,
                charges, maxCharges, onDepletion, durabilityBar, drops, loot);
    }

    private List<Ability> parseAbilities(String id, ConfigurationSection s) {
        List<Ability> abilities = new ArrayList<>();
        for (Map<?, ?> raw : s.getMapList("abilities")) {
            Ability ability = parseAbility(id, Cfg.wrap(raw));
            if (ability != null) abilities.add(ability);
        }
        return abilities;
    }

    private Ability parseAbility(String id, ConfigurationSection s) {
        String activatorId = s.getString("activator");
        Activator activator = activatorId == null ? null : registries.activator(activatorId);
        if (activator == null) {
            warn.accept("Item '" + id + "' has an unknown or missing activator '" + activatorId + "'; skipping ability.");
            return null;
        }
        String where = "Item '" + id + "' activator '" + activatorId + "'";
        ParamValues activatorParams = codec.read(activator.schema(), s, where);

        List<Configured<Condition>> conditions = parseConditions(id, s);
        Configured<Targeter> targeter = parseTargeter(id, s);
        List<ActionNode> actions = parseActions(id, s);
        Gate gate = parseGate(id, s);
        double cooldown = s.getDouble("cooldown", 0);

        return new Ability(activatorId, activatorParams, conditions, targeter, actions, gate, cooldown);
    }

    /**
     * Reads the optional M4 gate keys off an ability section (all reserved raw keys the codec ignores,
     * following the {@code invert:} precedent): {@code permission}, {@code charge-cost},
     * {@code cooldown-group:{key,seconds}}, {@code region:{in-region,can-build,respect-claims}},
     * {@code cost:{money,xp-levels,xp,hunger,items:[{material,amount}]}}, {@code deny-message}.
     * Returns {@link Gate#NONE} when none are present.
     */
    private Gate parseGate(String id, ConfigurationSection s) {
        String permission = s.getString("permission", "");
        int chargeCost = s.getInt("charge-cost", 0);
        String denyMessage = s.getString("deny-message", "");

        String cooldownGroup = "";
        double cooldownSeconds = 0;
        ConfigurationSection cg = s.getConfigurationSection("cooldown-group");
        if (cg != null) {
            cooldownGroup = cg.getString("key", "");
            cooldownSeconds = cg.getDouble("seconds", 0);
        }

        RegionSpec region = null;
        ConfigurationSection rs = s.getConfigurationSection("region");
        if (rs != null) {
            String in = rs.getString("in-region", "");
            boolean canBuild = rs.getBoolean("can-build", false);
            boolean respect = rs.getBoolean("respect-claims", false);
            region = new RegionSpec(in, canBuild, respect);
        }

        CostSpec cost = parseCost(id, s.getConfigurationSection("cost"));

        Gate gate = new Gate(permission, region, cost, chargeCost, cooldownGroup, cooldownSeconds, denyMessage);
        return gate.isNoOp() && denyMessage.isEmpty() ? Gate.NONE : gate;
    }

    private CostSpec parseCost(String id, ConfigurationSection c) {
        if (c == null) return CostSpec.NONE;
        double money = c.getDouble("money", 0);
        int xpLevels = c.getInt("xp-levels", 0);
        int xpPoints = c.getInt("xp", 0);
        double hunger = c.getDouble("hunger", 0);
        List<CostSpec.ItemCost> items = new ArrayList<>();
        for (Map<?, ?> raw : c.getMapList("items")) {
            ConfigurationSection is = Cfg.wrap(raw);
            Material m = Material.matchMaterial(is.getString("material", ""));
            if (m == null) {
                warn.accept("Item '" + id + "' cost has an unknown material '" + is.getString("material") + "'; skipping it.");
                continue;
            }
            items.add(new CostSpec.ItemCost(m, Math.max(1, is.getInt("amount", 1))));
        }
        return new CostSpec(money, xpLevels, xpPoints, hunger, items);
    }

    private List<Configured<Condition>> parseConditions(String id, ConfigurationSection s) {
        List<Configured<Condition>> out = new ArrayList<>();
        for (Map<?, ?> raw : s.getMapList("conditions")) {
            ConfigurationSection cs = Cfg.wrap(raw);
            String type = cs.getString("type");
            Condition def = type == null ? null : registries.condition(type);
            if (def == null) {
                warn.accept("Item '" + id + "' has an unknown condition '" + type + "'; skipping it.");
                continue;
            }
            ParamValues params = codec.read(def.schema(), cs, "Item '" + id + "' condition '" + type + "'");
            // Universal negation flag: `invert: true` on any condition wraps it so the gate reads the
            // opposite. It's a reserved key the codec ignores (not in any schema), so read it raw here.
            if (cs.getBoolean("invert", false)) {
                def = new InvertingCondition(def);
            }
            out.add(new Configured<>(def, params));
        }
        return out;
    }

    private Configured<Targeter> parseTargeter(String id, ConfigurationSection s) {
        String type = DEFAULT_TARGETER;
        ConfigurationSection params = null;
        if (s.isString("targeter")) {
            type = s.getString("targeter");
        } else if (s.isConfigurationSection("targeter")) {
            params = s.getConfigurationSection("targeter");
            type = params.getString("type", DEFAULT_TARGETER);
        }
        Targeter def = registries.targeter(type);
        if (def == null) {
            warn.accept("Item '" + id + "' has an unknown targeter '" + type + "'; falling back to '"
                    + DEFAULT_TARGETER + "'.");
            def = registries.targeter(DEFAULT_TARGETER);
        }
        ParamValues values = codec.read(def.schema(), params, "Item '" + id + "' targeter '" + def.id() + "'");
        return new Configured<>(def, values);
    }

    private List<ActionNode> parseActions(String id, ConfigurationSection s) {
        return parseActionList(id, s.getMapList("actions"));
    }

    /**
     * Parses a list of action maps into {@link ActionNode}s, recursing into a flow action's nested
     * bodies (e.g. {@code do}/{@code then}/{@code else}) and weighted {@code branches}. Any action may
     * also carry an inline {@code conditions:} gate (used by {@code if}).
     */
    private List<ActionNode> parseActionList(String id, List<Map<?, ?>> rawList) {
        List<ActionNode> out = new ArrayList<>();
        for (Map<?, ?> raw : rawList) {
            ConfigurationSection as = Cfg.wrap(raw);
            String type = as.getString("type");
            Action def = type == null ? null : registries.action(type);
            if (def == null) {
                warn.accept("Item '" + id + "' has an unknown action '" + type + "'; skipping it.");
                continue;
            }
            ParamValues params = codec.read(def.schema(), as, "Item '" + id + "' action '" + type + "'");
            List<Configured<Condition>> conditions = parseConditions(id, as);

            Map<String, List<ActionNode>> bodies = new LinkedHashMap<>();
            List<ActionNode.Branch> branches = List.of();
            if (def instanceof FlowAction flow) {
                for (String key : flow.bodyKeys()) {
                    bodies.put(key, parseActionList(id, as.getMapList(key)));
                }
                if (flow.usesBranches()) {
                    branches = parseBranches(id, as.getMapList("branches"));
                }
            }
            out.add(new ActionNode(def, params, conditions, bodies, branches));
        }
        return out;
    }

    private List<ActionNode.Branch> parseBranches(String id, List<Map<?, ?>> rawList) {
        List<ActionNode.Branch> out = new ArrayList<>();
        for (Map<?, ?> raw : rawList) {
            ConfigurationSection bs = Cfg.wrap(raw);
            double weight = bs.getDouble("weight", 1.0);
            List<ActionNode> body = parseActionList(id, bs.getMapList("do"));
            out.add(new ActionNode.Branch(weight, body));
        }
        return out;
    }

    /**
     * Reads every recipe for an item. Accepts both the legacy single {@code recipe:} section (kept for
     * back-compat with hand-authored files) and a {@code recipes:} list of recipe maps; both feed the
     * same per-recipe parser. Bad recipes warn-and-skip rather than failing the item.
     */
    private List<RecipeSpec> parseRecipes(String id, ConfigurationSection s) {
        List<RecipeSpec> out = new ArrayList<>();
        ConfigurationSection legacy = s.getConfigurationSection("recipe");
        if (legacy != null) {
            RecipeSpec r = parseOneRecipe(id, legacy);
            if (r != null) out.add(r);
        }
        for (Map<?, ?> raw : s.getMapList("recipes")) {
            RecipeSpec r = parseOneRecipe(id, Cfg.wrap(raw));
            if (r != null) out.add(r);
        }
        return out;
    }

    private RecipeSpec parseOneRecipe(String id, ConfigurationSection s) {
        if (s == null) return null;
        String type = s.getString("type", "shaped").toLowerCase(Locale.ROOT);

        if (type.equals("shaped")) {
            List<String> shape = s.getStringList("shape");
            if (shape.isEmpty() || shape.size() > 3) {
                warn.accept("Item '" + id + "' shaped recipe needs 1-3 'shape' rows; skipping recipe.");
                return null;
            }
            Map<Character, Material> ingredients = new HashMap<>();
            ConfigurationSection ing = s.getConfigurationSection("ingredients");
            if (ing != null) {
                for (String key : ing.getKeys(false)) {
                    Material m = Material.matchMaterial(ing.getString(key, ""));
                    if (m == null || key.length() != 1) {
                        warn.accept("Item '" + id + "' recipe has a bad ingredient entry '" + key + "'.");
                        continue;
                    }
                    ingredients.put(key.charAt(0), m);
                }
            }
            return new RecipeSpec.Shaped(shape, ingredients);
        }

        if (type.equals("shapeless")) {
            List<Material> materials = new ArrayList<>();
            for (String matName : s.getStringList("ingredients")) {
                Material m = Material.matchMaterial(matName);
                if (m == null) {
                    warn.accept("Item '" + id + "' recipe has an unknown material '" + matName + "'.");
                    continue;
                }
                materials.add(m);
            }
            if (materials.isEmpty()) {
                warn.accept("Item '" + id + "' shapeless recipe has no valid ingredients; skipping recipe.");
                return null;
            }
            return new RecipeSpec.Shapeless(materials);
        }

        RecipeSpec.Cooking.Kind kind = RecipeSpec.Cooking.Kind.from(type);
        if (kind != null) {
            Material input = Material.matchMaterial(s.getString("input", ""));
            if (input == null) {
                warn.accept("Item '" + id + "' " + type + " recipe has an invalid 'input' material; skipping recipe.");
                return null;
            }
            float experience = (float) s.getDouble("experience", 0.1);
            int cookTime = s.getInt("cook-time", kind.defaultCookTime());
            return new RecipeSpec.Cooking(kind, input, experience, cookTime);
        }

        if (type.equals("smithing")) {
            Material template = Material.matchMaterial(s.getString("template", ""));
            Material base = Material.matchMaterial(s.getString("base", ""));
            Material addition = Material.matchMaterial(s.getString("addition", ""));
            if (template == null || base == null || addition == null) {
                warn.accept("Item '" + id + "' smithing recipe needs valid 'template', 'base' and 'addition'; skipping recipe.");
                return null;
            }
            return new RecipeSpec.Smithing(template, base, addition);
        }

        if (type.equals("stonecutting")) {
            Material input = Material.matchMaterial(s.getString("input", ""));
            if (input == null) {
                warn.accept("Item '" + id + "' stonecutting recipe has an invalid 'input' material; skipping recipe.");
                return null;
            }
            return new RecipeSpec.Stonecutting(input);
        }

        warn.accept("Item '" + id + "' has an unknown or unsupported recipe type '" + type + "'.");
        return null;
    }

    /**
     * Reads the optional {@code drops:} section into {@link DropSources}: a {@code mobs:} list (each with
     * {@code entities}, {@code chance}, {@code min}/{@code max}, {@code require-player-kill}) and a
     * {@code blocks:} list (each with {@code blocks}, {@code chance}, {@code min}/{@code max},
     * {@code silk-touch}). Bad entries warn-and-skip; empty section returns {@link DropSources#NONE}.
     */
    private DropSources parseDrops(String id, ConfigurationSection d) {
        if (d == null) return DropSources.NONE;

        List<MobDrop> mobs = new ArrayList<>();
        for (Map<?, ?> raw : d.getMapList("mobs")) {
            ConfigurationSection ms = Cfg.wrap(raw);
            Set<EntityType> entities = parseEntities(id, ms.getStringList("entities"));
            double chance = ms.getDouble("chance", 1.0);
            int min = ms.getInt("min", 1);
            int max = ms.getInt("max", min);
            boolean requirePlayerKill = ms.getBoolean("require-player-kill", true);
            mobs.add(new MobDrop(entities, chance, min, max, requirePlayerKill));
        }

        List<BlockDrop> blocks = new ArrayList<>();
        for (Map<?, ?> raw : d.getMapList("blocks")) {
            ConfigurationSection bs = Cfg.wrap(raw);
            Set<Material> mats = parseMaterials(id, bs.getStringList("blocks"));
            if (mats.isEmpty()) {
                warn.accept("Item '" + id + "' block drop has no valid 'blocks'; skipping it.");
                continue;
            }
            double chance = bs.getDouble("chance", 1.0);
            int min = bs.getInt("min", 1);
            int max = bs.getInt("max", min);
            SilkTouchPolicy silk = SilkTouchPolicy.from(bs.getString("silk-touch", "any"));
            blocks.add(new BlockDrop(mats, chance, min, max, silk));
        }

        if (mobs.isEmpty() && blocks.isEmpty()) return DropSources.NONE;
        return new DropSources(mobs, blocks);
    }

    private Set<EntityType> parseEntities(String id, List<String> names) {
        Set<EntityType> out = new LinkedHashSet<>();
        for (String name : names) {
            try {
                out.add(EntityType.valueOf(name.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                warn.accept("Item '" + id + "' drop has an unknown entity type '" + name + "'.");
            }
        }
        return out;
    }

    private Set<Material> parseMaterials(String id, List<String> names) {
        Set<Material> out = new LinkedHashSet<>();
        for (String name : names) {
            Material m = Material.matchMaterial(name);
            if (m == null) {
                warn.accept("Item '" + id + "' drop has an unknown material '" + name + "'.");
                continue;
            }
            out.add(m);
        }
        return out;
    }

    /**
     * Reads the optional top-level {@code loot:} list into {@link LootInjection}: each entry has
     * {@code tables} (key patterns), {@code chance} and {@code min}/{@code max}. A rule with no
     * {@code tables} warns and is skipped; no {@code loot:} returns {@link LootInjection#NONE}.
     */
    private LootInjection parseLoot(String id, ConfigurationSection s) {
        List<LootRule> rules = new ArrayList<>();
        for (Map<?, ?> raw : s.getMapList("loot")) {
            ConfigurationSection ls = Cfg.wrap(raw);
            List<String> tables = ls.getStringList("tables");
            if (tables.isEmpty()) {
                warn.accept("Item '" + id + "' loot rule has no 'tables'; skipping it.");
                continue;
            }
            double chance = ls.getDouble("chance", 1.0);
            int min = ls.getInt("min", 1);
            int max = ls.getInt("max", min);
            rules.add(new LootRule(new ArrayList<>(tables), chance, min, max));
        }
        return rules.isEmpty() ? LootInjection.NONE : new LootInjection(rules);
    }
}
