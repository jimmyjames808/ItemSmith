package mastrjimbo.itemsmith.drops;

import org.bukkit.Material;

import java.util.Set;

/**
 * One "breaking this block drops this custom item" rule. {@code blocks} is the set of matching materials;
 * {@code chance} is 0-1 rolled per break; {@code min}/{@code max} is the stack count; {@code silkTouch}
 * gates on the breaking tool (see {@link SilkTouchPolicy}). Only fires when the break actually drops items
 * (creative breaks and {@code setDropItems(false)} are respected).
 */
public record BlockDrop(Set<Material> blocks, double chance, int min, int max, SilkTouchPolicy silkTouch) {
}
