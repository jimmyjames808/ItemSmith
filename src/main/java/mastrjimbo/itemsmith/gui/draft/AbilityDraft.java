package mastrjimbo.itemsmith.gui.draft;

import mastrjimbo.itemsmith.engine.Ability;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.engine.Configured;
import mastrjimbo.itemsmith.engine.Targeter;
import mastrjimbo.itemsmith.gate.Gate;

import java.util.ArrayList;
import java.util.List;

/**
 * The mutable counterpart of {@link Ability}. The activator is held loose (id + params) mirroring the
 * engine's asymmetry; conditions carry their inverted flag via {@link ConditionEntry}; the targeter is
 * a {@link ConfiguredDraft}; actions are the editable tree. The {@link Gate} is held immutably for now
 * (gate editing lands in a later checkpoint) but is preserved faithfully across hydrate/lower.
 */
public final class AbilityDraft {

    private String activatorId;
    private ParamBag activatorParams;
    private final List<ConditionEntry> conditions;
    private ConfiguredDraft<Targeter> targeter;
    private final List<ActionNodeDraft> actions;
    private Gate gate;
    private double cooldownSeconds;

    public AbilityDraft(String activatorId, ParamBag activatorParams, List<ConditionEntry> conditions,
                        ConfiguredDraft<Targeter> targeter, List<ActionNodeDraft> actions, Gate gate,
                        double cooldownSeconds) {
        this.activatorId = activatorId;
        this.activatorParams = activatorParams;
        this.conditions = conditions;
        this.targeter = targeter;
        this.actions = actions;
        this.gate = gate;
        this.cooldownSeconds = cooldownSeconds;
    }

    public static AbilityDraft hydrate(Ability ability) {
        List<ConditionEntry> conditions = new ArrayList<>();
        for (Configured<Condition> c : ability.conditions()) {
            conditions.add(ConditionEntry.hydrate(c));
        }
        return new AbilityDraft(
                ability.activatorId(),
                new ParamBag(ability.activatorParams()),
                conditions,
                ConfiguredDraft.hydrate(ability.targeter()),
                ActionNodeDraft.hydrateList(ability.actions()),
                ability.gate(),
                ability.cooldownSeconds());
    }

    public Ability toAbility() {
        List<Configured<Condition>> conds = new ArrayList<>();
        for (ConditionEntry c : conditions) {
            conds.add(c.toConfigured());
        }
        return new Ability(
                activatorId,
                activatorParams.toValues(),
                conds,
                targeter.toConfigured(),
                ActionNodeDraft.lowerList(actions),
                gate,
                cooldownSeconds);
    }

    public String activatorId() {
        return activatorId;
    }

    public void setActivatorId(String activatorId) {
        this.activatorId = activatorId;
    }

    public ParamBag activatorParams() {
        return activatorParams;
    }

    public void setActivatorParams(ParamBag activatorParams) {
        this.activatorParams = activatorParams;
    }

    public List<ConditionEntry> conditions() {
        return conditions;
    }

    public ConfiguredDraft<Targeter> targeter() {
        return targeter;
    }

    public void setTargeter(ConfiguredDraft<Targeter> targeter) {
        this.targeter = targeter;
    }

    public List<ActionNodeDraft> actions() {
        return actions;
    }

    public Gate gate() {
        return gate;
    }

    public void setGate(Gate gate) {
        this.gate = gate;
    }

    public double cooldownSeconds() {
        return cooldownSeconds;
    }

    public void setCooldownSeconds(double cooldownSeconds) {
        this.cooldownSeconds = cooldownSeconds;
    }
}
