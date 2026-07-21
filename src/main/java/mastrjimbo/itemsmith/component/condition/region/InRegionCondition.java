package mastrjimbo.itemsmith.component.condition.region;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes when the caster stands inside the named WorldGuard region. Fails closed without WorldGuard. */
public final class InRegionCondition implements Condition {

    public static final String ID = "in_region";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("region", ParamType.STRING, "")
                    .label("Region").desc("WorldGuard region name the caster must be inside."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.CONDITION; }
    @Override public String displayName() { return "In Region"; }
    @Override public String description() { return "True when the caster is inside a WorldGuard region."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.plugin() instanceof ItemSmith plugin)) return false;
        return plugin.protection().isInRegion(ctx.player(), params.getString("region", ""));
    }
}
