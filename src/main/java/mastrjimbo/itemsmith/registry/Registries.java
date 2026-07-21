package mastrjimbo.itemsmith.registry;

import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.engine.Activator;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.engine.Targeter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The four component registries (activators, conditions, targeters, actions),
 * each keyed by id. Components self-register at enable via
 * {@link BuiltinComponents}. Adding a feature is one new class plus one register
 * call — the YAML parser and the GUI both discover it here automatically, which
 * is the mechanism behind "100+ of everything".
 */
public final class Registries {

    private final Map<String, Activator> activators = new LinkedHashMap<>();
    private final Map<String, Condition> conditions = new LinkedHashMap<>();
    private final Map<String, Targeter> targeters = new LinkedHashMap<>();
    private final Map<String, Action> actions = new LinkedHashMap<>();

    public void register(Activator a) {
        activators.put(a.id(), a);
    }

    public void register(Condition c) {
        conditions.put(c.id(), c);
    }

    public void register(Targeter t) {
        targeters.put(t.id(), t);
    }

    public void register(Action a) {
        actions.put(a.id(), a);
    }

    public Activator activator(String id) {
        return activators.get(id);
    }

    public Condition condition(String id) {
        return conditions.get(id);
    }

    public Targeter targeter(String id) {
        return targeters.get(id);
    }

    public Action action(String id) {
        return actions.get(id);
    }

    public Collection<Activator> activators() {
        return activators.values();
    }

    public Collection<Condition> conditions() {
        return conditions.values();
    }

    public Collection<Targeter> targeters() {
        return targeters.values();
    }

    public Collection<Action> actions() {
        return actions.values();
    }

    public int total() {
        return activators.size() + conditions.size() + targeters.size() + actions.size();
    }
}
