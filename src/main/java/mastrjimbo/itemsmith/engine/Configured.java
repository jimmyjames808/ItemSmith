package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.param.ParamValues;

/**
 * A component definition paired with the concrete values it was configured with.
 * One registered {@link Action}/{@link Condition}/{@link Targeter} class is
 * reused across every item; this record is the per-use binding of that shared
 * definition to a specific item's parameters.
 */
public record Configured<T extends Component>(T definition, ParamValues params) {
}
