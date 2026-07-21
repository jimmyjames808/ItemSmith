package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.param.ParamValues;

/** A gate evaluated before an ability's actions run. All conditions must pass. */
public interface Condition extends Component {

    /**
     * @param ctx    the fire context (caster, item, event, ...).
     * @param target the object this check is evaluated against: at the ability gate it is the
     *               trigger's natural target ({@code ctx.eventTarget()}, possibly null); inside an
     *               {@code if} it is the resolved per-target element the executor is iterating
     *               (an {@code Entity}/{@code Block}/{@code Location}). Self/world conditions ignore
     *               it; target-relative conditions narrow it via {@code util/Targets} and fail-closed
     *               (return false) when it is null or the wrong kind.
     * @return true if this condition is satisfied.
     */
    boolean test(AbilityContext ctx, Object target, ParamValues params);
}
