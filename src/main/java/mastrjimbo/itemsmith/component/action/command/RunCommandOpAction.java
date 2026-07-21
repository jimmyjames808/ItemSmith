package mastrjimbo.itemsmith.component.action.command;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Player;

/**
 * Runs a command as the caster while temporarily granting them operator
 * rights, restoring their prior op state afterward even if the command
 * throws. Supports the {@code {player}} and {@code {item}} placeholders. No
 * leading slash.
 *
 * <p><b>Warning:</b> this action grants the caster temporary operator
 * privileges for the duration of the command. Use with care — a malicious or
 * exploitable command string run while op'd can escalate to full server
 * access.
 */
public final class RunCommandOpAction implements Action {

    public static final String ID = "run_command_op";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("command", ParamType.STRING, "")
                    .label("Command").desc("Command to run with temporary op, no leading slash. {player} and {item} are substituted."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMMAND; }
    @Override public String displayName() { return "Run Command (Temp Op)"; }
    @Override public String description() { return "Runs a command as the caster, temporarily op'd."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String cmd = params.getString("command", "")
                .replace("{player}", ctx.player().getName())
                .replace("{item}", ctx.itemId());
        if (cmd.isBlank()) return;
        Player p = ctx.player();
        boolean wasOp = p.isOp();
        try {
            p.setOp(true);
            p.performCommand(cmd);
        } finally {
            p.setOp(wasOp);
        }
    }
}
