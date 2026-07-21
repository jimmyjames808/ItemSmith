package mastrjimbo.itemsmith.loot;

import java.util.List;

/**
 * An item's loot-table injection rules — the parsed {@code loot:} list. Applied on
 * {@code LootGenerateEvent} (chest, fishing and mob-loot tables) by {@link LootInjector}.
 * {@link #NONE} is the shared empty instance.
 */
public record LootInjection(List<LootRule> rules) {

    public static final LootInjection NONE = new LootInjection(List.of());

    public boolean isEmpty() {
        return rules.isEmpty();
    }
}
