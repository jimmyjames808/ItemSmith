package mastrjimbo.itemsmith.drops;

import java.util.List;

/**
 * An item's direct-drop rules — the parsed {@code drops:} section. Mob drops fire on {@code EntityDeathEvent}
 * and block drops on {@code BlockBreakEvent} (see {@link DropManager}). Loot-table injection is a separate
 * concern (M6 O2). {@link #NONE} is the shared empty instance for items that aren't dropped by anything.
 */
public record DropSources(List<MobDrop> mobDrops, List<BlockDrop> blockDrops) {

    public static final DropSources NONE = new DropSources(List.of(), List.of());

    public boolean isEmpty() {
        return mobDrops.isEmpty() && blockDrops.isEmpty();
    }
}
