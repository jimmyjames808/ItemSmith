package mastrjimbo.itemsmith.component.condition.playerstate;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.param.ParamValues;

/** Passes while the caster is standing in lava. */
public final class IsInLavaCondition implements Condition {

    public static final String ID = "is_in_lava";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.CONDITION;
    }

    @Override
    public String displayName() {
        return "Is In Lava";
    }

    @Override
    public String description() {
        return "True while the caster is standing in lava.";
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().getLocation().getBlock().getType() == org.bukkit.Material.LAVA;
    }
}
