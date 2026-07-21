package mastrjimbo.itemsmith.drops;

import org.bukkit.entity.EntityType;

import java.util.Set;

/**
 * One "this mob drops this custom item" rule. {@code entities} empty means <i>any</i> mob; {@code chance}
 * is 0-1 rolled per death; {@code min}/{@code max} is the stack count; {@code requirePlayerKill} restricts
 * the drop to kills credited to a player (the common case — prevents mob-vs-mob farm exploits).
 */
public record MobDrop(Set<EntityType> entities, double chance, int min, int max, boolean requirePlayerKill) {

    /** True if this rule fires for the given mob type ({@code entities} empty = matches all). */
    public boolean matches(EntityType type) {
        return entities.isEmpty() || entities.contains(type);
    }
}
