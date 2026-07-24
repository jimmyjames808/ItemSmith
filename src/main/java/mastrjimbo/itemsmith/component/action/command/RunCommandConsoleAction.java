package mastrjimbo.itemsmith.component.action.command;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/**
 * Dispatches a command as the console (full permissions, no player attached).
 * Supports the {@code {player}} and {@code {item}} placeholders. No leading
 * slash.
 */
public final class RunCommandConsoleAction implements Action {

    public static final String ID = "run_command_console";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("command", ParamType.STRING, "")
                    .label("Command").desc("Command to run as console, no leading slash. {player} and {item} are substituted."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMMAND; }
    @Override public String displayName() { return "Run Command (Console)"; }
    @Override public String description() { return "Runs a command as the server console."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String cmd = CommandSupport.resolve(ctx, params.getString("command", ""));
        if (cmd == null) return;
        ctx.plugin().getServer().dispatchCommand(ctx.plugin().getServer().getConsoleSender(), cmd);
    }
}
