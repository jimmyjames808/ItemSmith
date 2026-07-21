package mastrjimbo.itemsmith.engine;

/**
 * A trigger that starts an ability pipeline. Activators are registered by id and
 * are metadata + validation targets; the actual event wiring lives in the
 * per-family listeners, which call
 * {@link AbilityEngine#fire(String, AbilityContext)} with this activator's id.
 * Keeping the "what event" (listener) and the "how it's configured" (this
 * descriptor) separate is what lets one listener serve many activators.
 */
public interface Activator extends Component {
}
