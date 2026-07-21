package mastrjimbo.itemsmith.component.action;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/**
 * Logs a line to the server console. Handy for server-side auditing and, during
 * development, for objectively verifying that a trigger fired (the line lands in
 * the server log with the plugin prefix). Supports the {@code {player}} and
 * {@code {item}} placeholders.
 */
public final class ConsoleLogAction implements Action {

    public static final String ID = "console_log";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("text", ParamType.STRING, "")
                    .label("Log text").desc("Line to log. {player} and {item} are substituted."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.COMMAND;
    }

    @Override
    public String displayName() {
        return "Console Log";
    }

    @Override
    public String description() {
        return "Writes a line to the server console.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String text = params.getString("text", "")
                .replace("{player}", ctx.player().getName())
                .replace("{item}", ctx.itemId());
        if (text.isBlank()) return;
        ctx.plugin().getLogger().info(text);
    }
}
