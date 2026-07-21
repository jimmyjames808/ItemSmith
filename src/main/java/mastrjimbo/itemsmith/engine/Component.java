package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.param.ParamSchema;

/**
 * Common metadata shared by every registerable engine component (activator,
 * condition, targeter, action). The registry keys on {@link #id()}; the GUI
 * groups by {@link #category()} and renders an editor from {@link #schema()}.
 */
public interface Component {

    /** Stable, lowercase, config-facing id (e.g. {@code potion_effect}). */
    String id();

    /** GUI folder this component appears under (e.g. {@code Effects}, {@code Combat}). */
    String category();

    /** Short human label for menus; defaults to the id. */
    default String displayName() {
        return id();
    }

    /** One-line help shown in the picker/editor. */
    default String description() {
        return "";
    }

    /** The parameters this component accepts; empty if it needs none. */
    default ParamSchema schema() {
        return ParamSchema.EMPTY;
    }
}
