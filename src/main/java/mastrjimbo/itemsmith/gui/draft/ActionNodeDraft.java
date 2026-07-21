package mastrjimbo.itemsmith.gui.draft;

import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.engine.ActionNode;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.engine.Configured;
import mastrjimbo.itemsmith.engine.FlowAction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The mutable counterpart of {@link ActionNode}: one node in the editable action tree. A leaf is just
 * a definition + params; a {@link FlowAction} node additionally carries inline conditions (for
 * {@code if}), named nested {@code bodies} (e.g. {@code do}/{@code then}/{@code else}), and weighted
 * {@code branches} (for {@code random}). Mutable so a deep edit is O(1) instead of rebuilding the
 * whole immutable spine.
 */
public final class ActionNodeDraft {

    private Action def;
    private ParamBag params;
    private final List<ConditionEntry> conditions;
    private final Map<String, List<ActionNodeDraft>> bodies;
    private final List<BranchDraft> branches;

    public ActionNodeDraft(Action def, ParamBag params, List<ConditionEntry> conditions,
                           Map<String, List<ActionNodeDraft>> bodies, List<BranchDraft> branches) {
        this.def = def;
        this.params = params;
        this.conditions = conditions;
        this.bodies = bodies;
        this.branches = branches;
    }

    /** A fresh node for a newly-added action: empty params/conditions, and (for a flow action) empty bodies. */
    public static ActionNodeDraft create(Action def) {
        Map<String, List<ActionNodeDraft>> bodies = new LinkedHashMap<>();
        if (def instanceof FlowAction flow) {
            for (String key : flow.bodyKeys()) {
                bodies.put(key, new ArrayList<>());
            }
        }
        return new ActionNodeDraft(def, new ParamBag(), new ArrayList<>(), bodies, new ArrayList<>());
    }

    public static ActionNodeDraft hydrate(ActionNode node) {
        List<ConditionEntry> conditions = new ArrayList<>();
        for (Configured<Condition> c : node.conditions()) {
            conditions.add(ConditionEntry.hydrate(c));
        }
        Map<String, List<ActionNodeDraft>> bodies = new LinkedHashMap<>();
        for (Map.Entry<String, List<ActionNode>> e : node.bodies().entrySet()) {
            bodies.put(e.getKey(), hydrateList(e.getValue()));
        }
        List<BranchDraft> branches = new ArrayList<>();
        for (ActionNode.Branch b : node.branches()) {
            branches.add(new BranchDraft(b.weight(), hydrateList(b.body())));
        }
        return new ActionNodeDraft(node.definition(), new ParamBag(node.params()), conditions, bodies, branches);
    }

    public static List<ActionNodeDraft> hydrateList(List<ActionNode> nodes) {
        List<ActionNodeDraft> out = new ArrayList<>();
        for (ActionNode n : nodes) {
            out.add(hydrate(n));
        }
        return out;
    }

    public ActionNode toActionNode() {
        List<Configured<Condition>> conds = new ArrayList<>();
        for (ConditionEntry c : conditions) {
            conds.add(c.toConfigured());
        }
        Map<String, List<ActionNode>> bodyMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<ActionNodeDraft>> e : bodies.entrySet()) {
            bodyMap.put(e.getKey(), lowerList(e.getValue()));
        }
        List<ActionNode.Branch> branchList = new ArrayList<>();
        for (BranchDraft b : branches) {
            branchList.add(new ActionNode.Branch(b.weight(), lowerList(b.body())));
        }
        return new ActionNode(def, params.toValues(), conds, bodyMap, branchList);
    }

    public static List<ActionNode> lowerList(List<ActionNodeDraft> drafts) {
        List<ActionNode> out = new ArrayList<>();
        for (ActionNodeDraft d : drafts) {
            out.add(d.toActionNode());
        }
        return out;
    }

    public boolean isFlow() {
        return def instanceof FlowAction;
    }

    public Action def() {
        return def;
    }

    public void setDef(Action def) {
        this.def = def;
    }

    public ParamBag params() {
        return params;
    }

    public void setParams(ParamBag params) {
        this.params = params;
    }

    public List<ConditionEntry> conditions() {
        return conditions;
    }

    public Map<String, List<ActionNodeDraft>> bodies() {
        return bodies;
    }

    public List<BranchDraft> branches() {
        return branches;
    }
}
