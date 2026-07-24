package mastrjimbo.itemsmith.store;

import mastrjimbo.itemsmith.component.condition.meta.InvertingCondition;
import mastrjimbo.itemsmith.drops.BlockDrop;
import mastrjimbo.itemsmith.drops.DropSources;
import mastrjimbo.itemsmith.drops.MobDrop;
import mastrjimbo.itemsmith.drops.SilkTouchPolicy;
import mastrjimbo.itemsmith.loot.LootInjection;
import mastrjimbo.itemsmith.loot.LootRule;
import mastrjimbo.itemsmith.engine.Ability;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.engine.ActionNode;
import mastrjimbo.itemsmith.engine.Activator;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.engine.Configured;
import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.engine.FlowAction;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.gate.CostSpec;
import mastrjimbo.itemsmith.gate.DepletionPolicy;
import mastrjimbo.itemsmith.gate.Gate;
import mastrjimbo.itemsmith.gate.RegionSpec;
import mastrjimbo.itemsmith.param.ParamCodec;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.recipe.RecipeSpec;
import mastrjimbo.itemsmith.registry.Registries;
import org.bukkit.Material;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The exact inverse of {@link ItemParser}: turns a {@link CustomItem} back into the per-item YAML
 * the parser reads. The structural framing (item / ability / gate / action nesting) is written by
 * hand to mirror the parser section-for-section; every component's parameters are delegated to the
 * shared {@link ParamCodec#write} so no per-component serialization is ever hand-coded. Output is
 * kept clean — default/empty sections are omitted (the parser re-fills them), so files read like the
 * hand-authored examples and {@code serialize → parse → serialize} is a fixed point.
 */
public final class ItemSerializer {

    private static final String DEFAULT_TARGETER = "target";

    private final Registries registries;
    private final ParamCodec codec;

    public ItemSerializer(Registries registries, ParamCodec codec) {
        this.registries = registries;
        this.codec = codec;
    }

    /** Serializes one item into a fresh {@link YamlConfiguration} ready to save (id lives in the filename). */
    public YamlConfiguration serialize(CustomItem item) {
        YamlConfiguration root = new YamlConfiguration();
        writeItemFields(root, item);

        List<Map<String, Object>> abilities = new ArrayList<>();
        for (Ability ability : item.abilities()) {
            abilities.add(writeAbility(ability));
        }
        if (!abilities.isEmpty()) root.set("abilities", abilities);

        writeRecipes(root, item.recipes());
        writeDrops(root, item.drops());
        writeLoot(root, item.loot());
        return root;
    }

    private void writeItemFields(YamlConfiguration root, CustomItem item) {
        root.set("material", item.material().getKey().getKey());
        if (item.itemModel() != null) root.set("item-model", item.itemModel().toString());
        if (item.customModelData() != null) root.set("custom-model-data", item.customModelData());
        if (item.name() != null && !item.name().isEmpty()) root.set("name", item.name());
        if (item.lore() != null && !item.lore().isEmpty()) root.set("lore", item.lore());

        if (item.charges() != null) {
            root.set("charges", item.charges());
            // Parser defaults max-charges to charges, so only write it when it actually differs.
            if (item.maxCharges() != null && !item.maxCharges().equals(item.charges())) {
                root.set("max-charges", item.maxCharges());
            }
        }
        if (item.onDepletion() != null && item.onDepletion() != DepletionPolicy.CONSUME) {
            root.set("on-depletion", item.onDepletion().name().toLowerCase(Locale.ROOT));
        }
        if (item.durabilityBar()) root.set("durability-bar", true);
        // Persistent stat declarations (initial values). These are only the seeds; the live per-item
        // values live in each stack's PDC, not here.
        if (item.stats() != null && !item.stats().isEmpty()) {
            item.stats().forEach((name, value) -> root.set("stats." + name, value));
        }
    }

    private Map<String, Object> writeAbility(Ability ability) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("activator", ability.activatorId());
        // Activator asymmetry: its params live flat on the same map. Resolve the schema from the
        // registry since Ability stores only the id. (All activators have an empty schema today.)
        Activator activator = registries.activator(ability.activatorId());
        if (activator != null) mergeParams(map, activator.schema(), ability.activatorParams());

        List<Map<String, Object>> conditions = writeConditions(ability.conditions());
        if (!conditions.isEmpty()) map.put("conditions", conditions);

        Object targeter = writeTargeter(ability.targeter());
        if (targeter != null) map.put("targeter", targeter);

        if (ability.cooldownSeconds() > 0) map.put("cooldown", ability.cooldownSeconds());

        List<Map<String, Object>> actions = writeActionList(ability.actions());
        if (!actions.isEmpty()) map.put("actions", actions);

        writeGate(map, ability.gate());
        return map;
    }

    private List<Map<String, Object>> writeConditions(List<Configured<Condition>> conditions) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (Configured<Condition> c : conditions) {
            Map<String, Object> cm = new LinkedHashMap<>();
            Condition def = c.definition();
            if (def instanceof InvertingCondition inv) {
                cm.put("type", inv.inner().id());
                mergeParams(cm, inv.inner().schema(), c.params());
                cm.put("invert", true);
            } else {
                cm.put("type", def.id());
                mergeParams(cm, def.schema(), c.params());
            }
            out.add(cm);
        }
        return out;
    }

    /** Bare string when the targeter has no params (omitted entirely for the default {@code target}); else a section. */
    private Object writeTargeter(Configured<Targeter> targeter) {
        if (targeter == null || targeter.definition() == null) return null;
        String id = targeter.definition().id();
        Map<String, Object> params = new LinkedHashMap<>();
        mergeParams(params, targeter.definition().schema(), targeter.params());
        if (params.isEmpty()) {
            return id.equals(DEFAULT_TARGETER) ? null : id;
        }
        Map<String, Object> section = new LinkedHashMap<>();
        section.put("type", id);
        section.putAll(params);
        return section;
    }

    private List<Map<String, Object>> writeActionList(List<ActionNode> nodes) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (ActionNode node : nodes) {
            out.add(writeActionNode(node));
        }
        return out;
    }

    private Map<String, Object> writeActionNode(ActionNode node) {
        Map<String, Object> map = new LinkedHashMap<>();
        Action def = node.definition();
        map.put("type", def.id());
        mergeParams(map, def.schema(), node.params());

        List<Map<String, Object>> conditions = writeConditions(node.conditions());
        if (!conditions.isEmpty()) map.put("conditions", conditions);

        if (def instanceof FlowAction flow) {
            for (String key : flow.bodyKeys()) {
                List<ActionNode> body = node.bodies().get(key);
                if (body != null && !body.isEmpty()) {
                    map.put(key, writeActionList(body));
                }
            }
            if (flow.usesBranches() && node.branches() != null && !node.branches().isEmpty()) {
                map.put("branches", writeBranches(node.branches()));
            }
        }
        return map;
    }

    private List<Map<String, Object>> writeBranches(List<ActionNode.Branch> branches) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (ActionNode.Branch branch : branches) {
            Map<String, Object> bm = new LinkedHashMap<>();
            bm.put("weight", branch.weight());
            bm.put("do", writeActionList(branch.body()));
            out.add(bm);
        }
        return out;
    }

    /** Writes the gate keys flat onto the ability map; nothing at all when the gate is a no-op. */
    private void writeGate(Map<String, Object> map, Gate gate) {
        if (gate == null || gate == Gate.NONE) return;
        boolean hasDeny = gate.denyMessage() != null && !gate.denyMessage().isEmpty();
        if (gate.isNoOp() && !hasDeny) return;

        if (gate.permission() != null && !gate.permission().isEmpty()) {
            map.put("permission", gate.permission());
        }
        if (gate.chargeCost() > 0) map.put("charge-cost", gate.chargeCost());
        if (hasDeny) map.put("deny-message", gate.denyMessage());

        if (gate.cooldownGroup() != null && !gate.cooldownGroup().isEmpty()) {
            Map<String, Object> cg = new LinkedHashMap<>();
            cg.put("key", gate.cooldownGroup());
            cg.put("seconds", gate.cooldownGroupSeconds());
            map.put("cooldown-group", cg);
        }

        RegionSpec region = gate.region();
        if (region != null && !region.isNoOp()) {
            Map<String, Object> rm = new LinkedHashMap<>();
            if (region.region() != null && !region.region().isEmpty()) rm.put("in-region", region.region());
            if (region.canBuild()) rm.put("can-build", true);
            if (region.respectClaims()) rm.put("respect-claims", true);
            map.put("region", rm);
        }

        CostSpec cost = gate.cost();
        if (cost != null && !cost.isNone()) {
            Map<String, Object> cm = new LinkedHashMap<>();
            if (cost.money() > 0) cm.put("money", cost.money());
            if (cost.xpLevels() > 0) cm.put("xp-levels", cost.xpLevels());
            if (cost.xpPoints() > 0) cm.put("xp", cost.xpPoints());   // field xpPoints -> yaml key 'xp'
            if (cost.hunger() > 0) cm.put("hunger", cost.hunger());
            if (cost.items() != null && !cost.items().isEmpty()) {
                List<Map<String, Object>> items = new ArrayList<>();
                for (CostSpec.ItemCost ic : cost.items()) {
                    Map<String, Object> im = new LinkedHashMap<>();
                    im.put("material", ic.material().getKey().getKey());
                    im.put("amount", ic.amount());
                    items.add(im);
                }
                cm.put("items", items);
            }
            map.put("cost", cm);
        }
    }

    /** Writes every recipe under a {@code recipes:} list (the modern form; legacy {@code recipe:} is read-only). */
    private void writeRecipes(YamlConfiguration root, List<RecipeSpec> recipes) {
        if (recipes == null || recipes.isEmpty()) return;
        List<Map<String, Object>> out = new ArrayList<>();
        for (RecipeSpec recipe : recipes) {
            if (recipe != null) out.add(writeOneRecipe(recipe));
        }
        if (!out.isEmpty()) root.set("recipes", out);
    }

    private Map<String, Object> writeOneRecipe(RecipeSpec recipe) {
        Map<String, Object> rm = new LinkedHashMap<>();
        rm.put("type", recipe.type());
        switch (recipe) {
            case RecipeSpec.Shaped s -> {
                rm.put("shape", s.shape());
                Map<String, Object> ingredients = new LinkedHashMap<>();
                for (Map.Entry<Character, Material> e : s.ingredients().entrySet()) {
                    ingredients.put(String.valueOf(e.getKey()), e.getValue().getKey().getKey());
                }
                rm.put("ingredients", ingredients);
            }
            case RecipeSpec.Shapeless s -> {
                List<String> materials = new ArrayList<>();
                for (Material m : s.materials()) {
                    materials.add(m.getKey().getKey());
                }
                rm.put("ingredients", materials);
            }
            case RecipeSpec.Cooking c -> {
                rm.put("input", c.input().getKey().getKey());
                rm.put("experience", (double) c.experience());
                rm.put("cook-time", c.cookTime());
            }
            case RecipeSpec.Smithing s -> {
                rm.put("template", s.template().getKey().getKey());
                rm.put("base", s.base().getKey().getKey());
                rm.put("addition", s.addition().getKey().getKey());
            }
            case RecipeSpec.Stonecutting s -> rm.put("input", s.input().getKey().getKey());
        }
        return rm;
    }

    /** Writes the {@code drops:} section (mob + block rules); nothing when the item has no drops. */
    private void writeDrops(YamlConfiguration root, DropSources drops) {
        if (drops == null || drops.isEmpty()) return;
        Map<String, Object> dm = new LinkedHashMap<>();

        if (!drops.mobDrops().isEmpty()) {
            List<Map<String, Object>> mobs = new ArrayList<>();
            for (MobDrop md : drops.mobDrops()) {
                Map<String, Object> m = new LinkedHashMap<>();
                if (!md.entities().isEmpty()) {
                    List<String> ents = new ArrayList<>();
                    for (var e : md.entities()) ents.add(e.getKey().getKey());
                    m.put("entities", ents);
                }
                m.put("chance", md.chance());
                if (md.min() != 1) m.put("min", md.min());
                if (md.max() != md.min()) m.put("max", md.max());
                if (!md.requirePlayerKill()) m.put("require-player-kill", false);
                mobs.add(m);
            }
            dm.put("mobs", mobs);
        }

        if (!drops.blockDrops().isEmpty()) {
            List<Map<String, Object>> blocks = new ArrayList<>();
            for (BlockDrop bd : drops.blockDrops()) {
                Map<String, Object> b = new LinkedHashMap<>();
                List<String> mats = new ArrayList<>();
                for (Material m : bd.blocks()) mats.add(m.getKey().getKey());
                b.put("blocks", mats);
                b.put("chance", bd.chance());
                if (bd.min() != 1) b.put("min", bd.min());
                if (bd.max() != bd.min()) b.put("max", bd.max());
                if (bd.silkTouch() != SilkTouchPolicy.ANY) {
                    b.put("silk-touch", bd.silkTouch().name().toLowerCase(Locale.ROOT));
                }
                blocks.add(b);
            }
            dm.put("blocks", blocks);
        }

        // createSection (vs set) builds a real nested section so getConfigurationSection("drops") works
        // both in-memory and after a save/load round-trip.
        root.createSection("drops", dm);
    }

    /** Writes the top-level {@code loot:} list (loot-table injection rules); nothing when there are none. */
    private void writeLoot(YamlConfiguration root, LootInjection loot) {
        if (loot == null || loot.isEmpty()) return;
        List<Map<String, Object>> out = new ArrayList<>();
        for (LootRule rule : loot.rules()) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("tables", new ArrayList<>(rule.tables()));
            m.put("chance", rule.chance());
            if (rule.min() != 1) m.put("min", rule.min());
            if (rule.max() != rule.min()) m.put("max", rule.max());
            out.add(m);
        }
        root.set("loot", out);
    }

    /**
     * Emits a component's parameters flat onto {@code target} via the shared {@link ParamCodec#write}
     * (Material/effect already normalized to lowercase keys). No-op for an empty schema.
     */
    private void mergeParams(Map<String, Object> target, ParamSchema schema, ParamValues values) {
        if (schema == null || schema.isEmpty() || values == null) return;
        MemoryConfiguration tmp = new MemoryConfiguration();
        codec.write(schema, values, tmp);
        target.putAll(tmp.getValues(false));
    }
}
