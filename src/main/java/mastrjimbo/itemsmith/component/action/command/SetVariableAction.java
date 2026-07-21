package mastrjimbo.itemsmith.component.action.command;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/**
 * Writes a value into the ability's scratch variable map, so later actions in
 * the same chain (or conditions re-reading {@link AbilityContext#variables()})
 * can see it. The map does not persist beyond a single activation.
 */
public final class SetVariableAction implements Action {

    public static final String ID = "set_variable";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("key", ParamType.STRING, "var")
                    .label("Key").desc("Variable name to write."))
            .add(ParamDef.of("value", ParamType.STRING, "")
                    .label("Value").desc("Value to store."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMMAND; }
    @Override public String displayName() { return "Set Variable"; }
    @Override public String description() { return "Stores a value in the ability's scratch variables."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String key = params.getString("key", "var");
        if (key.isBlank()) return;
        String value = params.getString("value", "");
        ctx.variables().put(key, value);
    }
}
