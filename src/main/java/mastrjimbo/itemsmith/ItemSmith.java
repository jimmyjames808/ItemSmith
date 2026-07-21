package mastrjimbo.itemsmith;

import mastrjimbo.itemsmith.command.ItemCommand;
import mastrjimbo.itemsmith.drops.DropManager;
import mastrjimbo.itemsmith.loot.LootInjector;
import mastrjimbo.itemsmith.engine.AbilityEngine;
import mastrjimbo.itemsmith.engine.CooldownManager;
import mastrjimbo.itemsmith.engine.ItemBuilder;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.gate.GateEvaluator;
import mastrjimbo.itemsmith.gate.NamedCooldownStore;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.integration.ProtectionHook;
import mastrjimbo.itemsmith.integration.VaultHook;
import mastrjimbo.itemsmith.listener.BlockListener;
import mastrjimbo.itemsmith.listener.CombatListener;
import mastrjimbo.itemsmith.listener.DurabilityListener;
import mastrjimbo.itemsmith.listener.EffectListener;
import mastrjimbo.itemsmith.listener.EntityInteractListener;
import mastrjimbo.itemsmith.listener.EquipListener;
import mastrjimbo.itemsmith.listener.FireworkListener;
import mastrjimbo.itemsmith.listener.FishingListener;
import mastrjimbo.itemsmith.listener.HealthListener;
import mastrjimbo.itemsmith.listener.InteractListener;
import mastrjimbo.itemsmith.listener.InventoryListener;
import mastrjimbo.itemsmith.listener.ItemUseListener;
import mastrjimbo.itemsmith.listener.MovementListener;
import mastrjimbo.itemsmith.listener.PassiveTask;
import mastrjimbo.itemsmith.listener.ProjectileListener;
import mastrjimbo.itemsmith.listener.ProjectileTracker;
import mastrjimbo.itemsmith.listener.SessionListener;
import mastrjimbo.itemsmith.listener.StationListener;
import mastrjimbo.itemsmith.param.ParamCodec;
import mastrjimbo.itemsmith.recipe.RecipeManager;
import mastrjimbo.itemsmith.registry.BuiltinComponents;
import mastrjimbo.itemsmith.registry.Registries;
import mastrjimbo.itemsmith.store.ItemParser;
import mastrjimbo.itemsmith.store.ItemSerializer;
import mastrjimbo.itemsmith.store.ItemStore;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin entry point. Wires the schema-driven engine: the component registries,
 * the per-item YAML store, the ability engine and its event-family listeners.
 * All behaviour flows through registered components, so future milestones extend
 * ItemSmith by adding components — not by touching this class.
 */
public final class ItemSmith extends JavaPlugin {

    private Registries registries;
    private ItemRegistry registry;
    private AbilityEngine engine;
    private ItemStore store;
    private RecipeManager recipes;
    private DropManager drops;
    private LootInjector loot;
    private VaultHook vault;
    private ProtectionHook protection;
    private NamedCooldownStore cooldownGroups;

    @Override
    public void onEnable() {
        registries = new Registries();
        BuiltinComponents.registerAll(registries);

        ParamCodec codec = new ParamCodec(getLogger()::warning);
        ItemBuilder builder = new ItemBuilder(this);
        registry = new ItemRegistry(builder);

        ItemParser parser = new ItemParser(registries, codec, getLogger()::warning);
        ItemSerializer serializer = new ItemSerializer(registries, codec);
        store = new ItemStore(this, parser, serializer);
        store.ensureDefaults();

        // M4 integrations (null-object soft-depend) + gate layer.
        vault = VaultHook.detect(this);
        protection = ProtectionHook.detect(this);
        cooldownGroups = new NamedCooldownStore();
        getLogger().info("Integrations: Vault=" + vault.available()
                + " WorldGuard=" + protection.hasWorldGuard()
                + " GriefPrevention=" + protection.hasGriefPrevention());

        CooldownManager cooldowns = new CooldownManager();
        GateEvaluator gates = new GateEvaluator(vault, protection, cooldownGroups, getLogger());
        engine = new AbilityEngine(this, registry, cooldowns, gates, getLogger());
        recipes = new RecipeManager(this, registry);
        drops = new DropManager(registry);
        loot = new LootInjector(registry);

        loadItems();

        registerListeners(engine);

        // M5 creator: constructed after items load so it can enumerate them, before the command.
        GuiManager guiManager = new GuiManager(this, registries, store, registry);

        ItemCommand command = new ItemCommand(this, registry, guiManager);
        PluginCommand pluginCommand = getCommand("itemsmith");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        getLogger().info("ItemSmith enabled: " + registry.ids().size() + " item(s), "
                + registries.total() + " engine component(s).");
    }

    @Override
    public void onDisable() {
        if (recipes != null) {
            recipes.unregisterAll();
        }
    }

    /** Reloads every item file from disk and re-registers recipes. @return item count. */
    public int reload() {
        loadItems();
        return registry.ids().size();
    }

    /** Vault economy adapter (null-object; {@code available()} is false when Vault is absent). */
    public VaultHook vault() {
        return vault;
    }

    /** Land-protection adapter (WorldGuard/GriefPrevention; graceful when absent). */
    public ProtectionHook protection() {
        return protection;
    }

    /** Shared named-cooldown store backing cooldown groups, {@code set_cooldown} and {@code cooldown_ready}. */
    public NamedCooldownStore cooldownGroups() {
        return cooldownGroups;
    }

    /** The per-item YAML store (load + save). The GUI creator writes edited items through this. */
    public ItemStore store() {
        return store;
    }

    /** The live registry of loaded item definitions (hydrated by the GUI creator to edit an item). */
    public ItemRegistry registry() {
        return registry;
    }

    /** Recipe registrar — the catalog uses it to unlock an item's recipes in a player's recipe book. */
    public RecipeManager recipes() {
        return recipes;
    }

    /** The ability engine — used by custom projectiles to fire hit activators back onto the pipeline. */
    public AbilityEngine engine() {
        return engine;
    }

    private void loadItems() {
        registry.replaceAll(store.loadAll());
        recipes.registerAll();
        drops.reindex(registry.all());
        loot.reindex(registry.all());
    }

    private void registerListeners(AbilityEngine engine) {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InteractListener(engine), this);
        pm.registerEvents(new InventoryListener(engine), this);
        pm.registerEvents(new CombatListener(engine), this);
        pm.registerEvents(new EffectListener(engine), this);
        pm.registerEvents(new HealthListener(engine), this);
        pm.registerEvents(new DurabilityListener(engine), this);
        ProjectileTracker projectileTracker = new ProjectileTracker(this, registry, engine);
        pm.registerEvents(new ProjectileListener(this, registry, engine, projectileTracker), this);
        getServer().getScheduler().runTaskTimer(this, projectileTracker, 1L, 1L);
        pm.registerEvents(new BlockListener(engine, registry), this);
        pm.registerEvents(new MovementListener(engine), this);
        pm.registerEvents(new SessionListener(this, registry, engine), this);
        pm.registerEvents(new ItemUseListener(engine), this);
        pm.registerEvents(new EquipListener(registry, engine), this);
        pm.registerEvents(new EntityInteractListener(this, registry, engine), this);
        pm.registerEvents(new StationListener(engine), this);
        pm.registerEvents(new FishingListener(engine), this);
        pm.registerEvents(new FireworkListener(this), this);
        pm.registerEvents(drops, this);
        pm.registerEvents(loot, this);

        getServer().getScheduler().runTaskTimer(this, new PassiveTask(registry, engine),
                PassiveTask.INTERVAL_TICKS, PassiveTask.INTERVAL_TICKS);
    }
}
