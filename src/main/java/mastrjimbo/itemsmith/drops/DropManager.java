package mastrjimbo.itemsmith.drops;

import mastrjimbo.itemsmith.engine.CustomItem;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Injects custom-item drops on mob death and block break, driven by each item's {@link DropSources}. It
 * registers its own listeners <i>alongside</i> the activator listeners (Bukkit allows many handlers per
 * event) and drops items — it never fires abilities, so no re-entrancy guard is needed. The lookup indexes
 * are rebuilt from the registry on every load/reload via {@link #reindex(Collection)}.
 */
public final class DropManager implements Listener {

    private final ItemRegistry registry;

    /** (itemId, rule) so a drop knows which custom item to build. */
    private record Entry<T>(String itemId, T rule) {
    }

    private final Map<EntityType, List<Entry<MobDrop>>> mobIndex = new EnumMap<>(EntityType.class);
    private final List<Entry<MobDrop>> anyMob = new ArrayList<>();
    private final Map<Material, List<Entry<BlockDrop>>> blockIndex = new EnumMap<>(Material.class);

    public DropManager(ItemRegistry registry) {
        this.registry = registry;
    }

    /** Rebuilds the mob/block lookup tables from the loaded items. Call after every (re)load. */
    public void reindex(Collection<CustomItem> items) {
        mobIndex.clear();
        anyMob.clear();
        blockIndex.clear();
        for (CustomItem item : items) {
            DropSources drops = item.drops();
            if (drops == null || drops.isEmpty()) continue;
            for (MobDrop md : drops.mobDrops()) {
                Entry<MobDrop> e = new Entry<>(item.id(), md);
                if (md.entities().isEmpty()) {
                    anyMob.add(e);
                } else {
                    for (EntityType t : md.entities()) {
                        mobIndex.computeIfAbsent(t, k -> new ArrayList<>()).add(e);
                    }
                }
            }
            for (BlockDrop bd : drops.blockDrops()) {
                Entry<BlockDrop> e = new Entry<>(item.id(), bd);
                for (Material m : bd.blocks()) {
                    blockIndex.computeIfAbsent(m, k -> new ArrayList<>()).add(e);
                }
            }
        }
    }

    // HIGH (not MONITOR) so we add to the drop list before other plugins read the final drops; ignore
    // cancelled so a cancelled death doesn't drop.
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        EntityType type = event.getEntityType();
        List<Entry<MobDrop>> exact = mobIndex.get(type);
        if (exact == null && anyMob.isEmpty()) return;

        boolean playerKill = event.getEntity().getKiller() != null;
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        rollMob(exact, playerKill, rng, event);
        rollMob(anyMob, playerKill, rng, event);
    }

    private void rollMob(List<Entry<MobDrop>> list, boolean playerKill, ThreadLocalRandom rng, EntityDeathEvent event) {
        if (list == null) return;
        for (Entry<MobDrop> e : list) {
            MobDrop md = e.rule();
            if (md.requirePlayerKill() && !playerKill) continue;
            if (rng.nextDouble() >= md.chance()) continue;
            ItemStack stack = registry.build(e.itemId());
            if (stack == null) continue;
            stack.setAmount(count(md.min(), md.max(), rng));
            event.getDrops().add(stack);
        }
    }

    // MONITOR + ignoreCancelled: a protected/cancelled break never fires; we only add a natural drop,
    // which doesn't change the event outcome. Respect setDropItems(false) (creative / auto-smelt).
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        List<Entry<BlockDrop>> list = blockIndex.get(event.getBlock().getType());
        if (list == null || !event.isDropItems()) return;

        boolean silk = hasSilkTouch(event.getPlayer());
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        for (Entry<BlockDrop> e : list) {
            BlockDrop bd = e.rule();
            if (!bd.silkTouch().allows(silk)) continue;
            if (rng.nextDouble() >= bd.chance()) continue;
            ItemStack stack = registry.build(e.itemId());
            if (stack == null) continue;
            stack.setAmount(count(bd.min(), bd.max(), rng));
            event.getBlock().getWorld().dropItemNaturally(
                    event.getBlock().getLocation().add(0.5, 0.5, 0.5), stack);
        }
    }

    private static final Enchantment SILK_TOUCH =
            Registry.ENCHANTMENT.get(NamespacedKey.minecraft("silk_touch"));

    private boolean hasSilkTouch(Player player) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        return SILK_TOUCH != null && tool != null && tool.containsEnchantment(SILK_TOUCH);
    }

    private static int count(int min, int max, ThreadLocalRandom rng) {
        int lo = Math.max(1, min);
        int hi = Math.max(lo, max);
        return lo == hi ? lo : lo + rng.nextInt(hi - lo + 1);
    }
}
