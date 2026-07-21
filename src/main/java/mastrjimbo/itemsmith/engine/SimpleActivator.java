package mastrjimbo.itemsmith.engine;

/**
 * A metadata-only activator. Because event wiring lives in the per-family
 * listeners, most activators carry no behaviour of their own — just an id,
 * category and label for the registry, YAML validation and the GUI. This one
 * class covers all of them; a trigger only needs a dedicated class if it takes
 * configurable parameters.
 */
public record SimpleActivator(String id, String category, String displayName, String description)
        implements Activator {
}
