package mastrjimbo.itemsmith.loot;

import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

/**
 * Injects custom items into loot by matching each item's {@link LootInjection} rules (table-key patterns)
 * against the source that produced the loot. {@code LootGenerateEvent} covers <b>containers</b> only
 * (chests/barrels/minecarts), so mob and fishing loot are routed through their own events under synthetic
 * keys: a mob death is treated as {@code entities/<type>} and a fishing catch as {@code gameplay/fishing}.
 * A single {@code loot:} section therefore reaches all three sources with the same pattern syntax.
 * Registered alongside the activator listeners; rebuilt from the registry on every reload.
 */
public final class LootInjector implements Listener {

    /** Synthetic key a fishing catch is matched against ({@code PlayerFishEvent} exposes no table key). */
    private static final NamespacedKey FISHING_KEY = NamespacedKey.minecraft("gameplay/fishing");

    private final ItemRegistry registry;

    private record Entry(String itemId, LootRule rule) {
    }

    private final List<Entry> rules = new ArrayList<>();

    public LootInjector(ItemRegistry registry) {
        this.registry = registry;
    }

    /** Rebuilds the flat rule list from the loaded items. Call after every (re)load. */
    public void reindex(Collection<CustomItem> items) {
        rules.clear();
        for (CustomItem item : items) {
            LootInjection loot = item.loot();
            if (loot == null || loot.isEmpty()) continue;
            for (LootRule rule : loot.rules()) {
                rules.add(new Entry(item.id(), rule));
            }
        }
    }

    /** Container loot (chests, barrels, minecart chests, structure loot) — the only source this event covers. */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onLootGenerate(LootGenerateEvent event) {
        if (rules.isEmpty()) return;
        LootTable table = event.getLootTable();
        if (table == null) return;
        List<ItemStack> loot = event.getLoot();
        inject(table.getKey(), loot::add);
    }

    /** Mob loot: matched as {@code entities/<type>} and appended to the death drops. */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (rules.isEmpty()) return;
        NamespacedKey key = NamespacedKey.minecraft("entities/" + event.getEntityType().getKey().getKey());
        List<ItemStack> drops = event.getDrops();
        inject(key, drops::add);
    }

    /**
     * Fishing loot: matched as {@code gameplay/fishing}. The item is spawned at the bobber and flung toward
     * the angler with vanilla's reel-in velocity, so it arcs out of the water and is picked up like a real
     * catch rather than silently appearing in the inventory.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (rules.isEmpty() || event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        Player player = event.getPlayer();
        Location hook = event.getHook().getLocation();
        inject(FISHING_KEY, stack -> {
            Item drop = player.getWorld().dropItem(hook, stack);
            drop.setPickupDelay(0);
            // Vanilla FishingHook reel-in velocity: pull toward the player with a gentle upward arc.
            double dx = player.getX() - hook.getX();
            double dy = player.getY() - hook.getY();
            double dz = player.getZ() - hook.getZ();
            double f = 0.1;
            drop.setVelocity(new Vector(dx * f,
                    dy * f + Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz)) * 0.08, dz * f));
        });
    }

    /** Rolls every rule matching {@code key} and hands each built stack to {@code sink}. */
    private void inject(NamespacedKey key, Consumer<ItemStack> sink) {
        if (rules.isEmpty()) return;
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (Entry e : rules) {
            LootRule rule = e.rule();
            if (!rule.matches(key)) continue;
            if (rng.nextDouble() >= rule.chance()) continue;
            ItemStack stack = registry.build(e.itemId());
            if (stack == null) continue;
            stack.setAmount(count(rule.min(), rule.max(), rng));
            sink.accept(stack);
        }
    }

    private static int count(int min, int max, ThreadLocalRandom rng) {
        int lo = Math.max(1, min);
        int hi = Math.max(lo, max);
        return lo == hi ? lo : lo + rng.nextInt(hi - lo + 1);
    }
}
