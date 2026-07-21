package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.param.ParamValues;

/**
 * A single effect performed against one resolved target. Actions run once per
 * target the {@link Targeter} produced, in declared order. An action that does
 * not apply to a given target kind (e.g. a heal against a Block) should simply
 * do nothing.
 */
public interface Action extends Component {

    /**
     * @param ctx    the fire context (caster, item, triggering event, variables)
     * @param target one resolved target (Entity, Block, Location) — may be null
     *               for self/global actions
     * @param params this action's configured values
     */
    void run(AbilityContext ctx, Object target, ParamValues params);
}
