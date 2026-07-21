package mastrjimbo.itemsmith.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared visual emitter so any particle effect can be swapped for a floating mob/player head. Actions pass
 * their resolved {@link Particle} plus an optional {@code head} material (and, for {@code player_head}, an
 * owner name): if {@code head} names a head/skull, a short-lived head is shown at the point instead of the
 * particle, so a burst becomes one head and a per-point shape (ring, line, helix…) becomes a shape of heads.
 *
 * <p>Heads are cosmetic entities (an invisible small armour stand wearing the head as a helmet — NOT a
 * marker stand, which would not render equipment). They self-remove after a few ticks and are capped
 * server-wide by {@link #MAX_LIVE_HEADS} so a large shape can't spawn hundreds of entities at once.
 */
public final class Visuals {

    /** A small armour stand's helmet renders this far above its base — drop the base so the head sits on the point. */
    private static final double HEAD_OFFSET = 0.75;
    /** Transient heads self-remove after this many ticks. */
    private static final long HEAD_LIFESPAN = 12L;
    /** Hard cap on concurrently-alive cosmetic heads across the server (protects against huge shapes). */
    private static final int MAX_LIVE_HEADS = 96;

    private static final AtomicInteger LIVE_HEADS = new AtomicInteger();

    private Visuals() {
    }

    /** True if the material is a wearable head/skull (zombie_head, player_head, wither_skeleton_skull…). */
    public static boolean isHead(Material m) {
        if (m == null) return false;
        String key = m.getKey().getKey();
        return key.endsWith("_head") || key.endsWith("_skull");
    }

    /**
     * Emits {@code count} of the particle at {@code loc} with the given spread — unless {@code head} is a
     * head/skull material, in which case a single floating head is shown at the point (spread jitters it).
     */
    public static void emit(Plugin plugin, Location loc, Particle particle, int count,
                            double sx, double sy, double sz, double extra, Material head, String owner) {
        World w = loc.getWorld();
        if (w == null) return;
        if (isHead(head)) {
            Location at = loc.clone();
            if (sx > 0 || sy > 0 || sz > 0) {
                at.add(jitter(sx), jitter(sy), jitter(sz));
            }
            spawnHead(plugin, w, at, head, owner);
        } else if (particle != null) {
            w.spawnParticle(particle, loc, count, sx, sy, sz, extra);
        }
    }

    /** Convenience for callers that have raw x/y/z (e.g. line points). */
    public static void emit(Plugin plugin, World w, double x, double y, double z, Particle particle,
                            Material head, String owner) {
        emit(plugin, new Location(w, x, y, z), particle, 1, 0, 0, 0, 0, head, owner);
    }

    private static void spawnHead(Plugin plugin, World w, Location point, Material headMat, String owner) {
        if (LIVE_HEADS.get() >= MAX_LIVE_HEADS) return; // don't flood the world with entities
        ItemStack helmet = headItem(headMat, owner);
        Location base = point.clone().subtract(0, HEAD_OFFSET, 0);
        // A NON-marker small stand: invisible body, no hitbox interaction, but the helmet still renders
        // (marker stands do not render equipment, which is the whole point of this effect).
        ArmorStand stand = w.spawn(base, ArmorStand.class, s -> {
            s.setVisible(false);
            s.setSmall(true);
            s.setGravity(false);
            s.setBasePlate(false);
            s.setArms(false);
            s.setInvulnerable(true);
            s.setCollidable(false);
            s.setPersistent(false);
            if (s.getEquipment() != null) s.getEquipment().setHelmet(helmet);
        });
        LIVE_HEADS.incrementAndGet();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            stand.remove();
            LIVE_HEADS.decrementAndGet();
        }, HEAD_LIFESPAN);
    }

    private static ItemStack headItem(Material mat, String owner) {
        ItemStack item = new ItemStack(mat);
        if (mat == Material.PLAYER_HEAD && owner != null && !owner.isBlank()
                && item.getItemMeta() instanceof SkullMeta skull) {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(owner));
            item.setItemMeta(skull);
        }
        return item;
    }

    private static double jitter(double spread) {
        return (Math.random() - 0.5) * 2 * spread;
    }
}
