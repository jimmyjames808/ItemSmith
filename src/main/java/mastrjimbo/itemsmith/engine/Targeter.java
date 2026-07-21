package mastrjimbo.itemsmith.engine;

import mastrjimbo.itemsmith.param.ParamValues;

import java.util.List;

/**
 * Resolves the list of targets an ability's actions run against. A target may be
 * an {@link org.bukkit.entity.Entity}, a {@link org.bukkit.block.Block}, or a
 * {@link org.bukkit.Location}; each action decides which target kinds it can act
 * on. An empty list means "no targets" and the actions are skipped.
 */
public interface Targeter extends Component {

    List<Object> resolve(AbilityContext ctx, ParamValues params);
}
