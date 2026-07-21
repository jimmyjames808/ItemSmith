package mastrjimbo.itemsmith.component.condition.identity;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes while the caster has the given permission node. */
public final class HasPermissionCondition implements Condition {

    public static final String ID = "has_permission";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("permission", ParamType.STRING, "")
                    .label("Permission").desc("Permission node the caster must hold, e.g. myplugin.vip."))
            .build();

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
        return "Has Permission";
    }

    @Override
    public String description() {
        return "True while the caster has a permission node.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        return ctx.player().hasPermission(params.getString("permission", ""));
    }
}
