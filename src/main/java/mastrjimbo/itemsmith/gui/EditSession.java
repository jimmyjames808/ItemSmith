package mastrjimbo.itemsmith.gui;

import mastrjimbo.itemsmith.gui.draft.ItemDraft;

import java.util.UUID;

/**
 * The per-player editing state for one open creator session: the in-progress {@link ItemDraft}, a
 * {@code transitioning} flag (set around a chest→dialog handoff so the intervening
 * {@code InventoryCloseEvent} doesn't tear the session down), and the Simple/Advanced disclosure
 * toggle. The action-tree cursor and nav stack are added as those screens land.
 */
public final class EditSession {

    private final UUID playerId;
    private final ItemDraft draft;
    private boolean transitioning;
    private boolean advanced;

    public EditSession(UUID playerId, ItemDraft draft) {
        this.playerId = playerId;
        this.draft = draft;
    }

    public UUID playerId() {
        return playerId;
    }

    public ItemDraft draft() {
        return draft;
    }

    public boolean transitioning() {
        return transitioning;
    }

    public void setTransitioning(boolean transitioning) {
        this.transitioning = transitioning;
    }

    public boolean advanced() {
        return advanced;
    }

    public void toggleAdvanced() {
        this.advanced = !this.advanced;
    }
}
