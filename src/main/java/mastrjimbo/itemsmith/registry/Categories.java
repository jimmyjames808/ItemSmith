package mastrjimbo.itemsmith.registry;

/**
 * Canonical category labels used as GUI folder names. Centralised so the growing
 * component library stays consistently grouped instead of drifting into
 * near-duplicate folders ("Effect" vs "Effects").
 */
public final class Categories {

    public static final String COMBAT = "Combat";
    public static final String MOVEMENT = "Movement";
    public static final String EFFECTS = "Effects";
    public static final String WORLD = "World";
    public static final String PLAYER = "Player";
    public static final String ECONOMY = "Economy";
    public static final String COMMAND = "Command";
    public static final String VISUAL = "Visual";
    public static final String META = "Meta";
    public static final String TARGETER = "Targeter";
    public static final String CONDITION = "Condition";

    // Activator-oriented groupings (trigger picker folders)
    public static final String INTERACT = "Interact";
    public static final String BLOCK = "Block";
    public static final String ITEM = "Item";
    public static final String PROJECTILE = "Projectile";
    public static final String LIFECYCLE = "Lifecycle";

    private Categories() {
    }
}
