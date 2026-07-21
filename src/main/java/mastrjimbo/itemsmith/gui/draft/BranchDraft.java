package mastrjimbo.itemsmith.gui.draft;

import java.util.List;

/** One weighted branch of a {@code random} node in the editor: a relative weight and a nested body. */
public final class BranchDraft {

    private double weight;
    private final List<ActionNodeDraft> body;

    public BranchDraft(double weight, List<ActionNodeDraft> body) {
        this.weight = weight;
        this.body = body;
    }

    public double weight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<ActionNodeDraft> body() {
        return body;
    }
}
