package mastrjimbo.itemsmith.gui.draft;

import mastrjimbo.itemsmith.engine.Component;
import mastrjimbo.itemsmith.engine.Configured;

/**
 * The mutable counterpart of {@link Configured}: a swappable component definition plus its editable
 * parameters. Used for the ability's targeter (the activator is held loose as id+params on
 * {@link AbilityDraft}, and conditions use {@link ConditionEntry} to carry their inverted flag).
 */
public final class ConfiguredDraft<T extends Component> {

    private T def;
    private ParamBag params;

    public ConfiguredDraft(T def, ParamBag params) {
        this.def = def;
        this.params = params;
    }

    public static <T extends Component> ConfiguredDraft<T> hydrate(Configured<T> configured) {
        return new ConfiguredDraft<>(configured.definition(), new ParamBag(configured.params()));
    }

    public Configured<T> toConfigured() {
        return new Configured<>(def, params.toValues());
    }

    public T def() {
        return def;
    }

    public void setDef(T def) {
        this.def = def;
    }

    public ParamBag params() {
        return params;
    }

    public void setParams(ParamBag params) {
        this.params = params;
    }
}
