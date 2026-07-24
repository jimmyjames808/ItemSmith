package mastrjimbo.itemsmith.component.action.command;

import mastrjimbo.itemsmith.engine.AbilityContext;

/** Shared helpers for the {@code run_command_*} actions. */
final class CommandSupport {

    private CommandSupport() {
    }

    /**
     * Builds the command string from a template, substituting {@code {player}} and
     * {@code {item}}. Returns {@code null} (the caller should abort) when the template is
     * blank, or when it uses {@code {player}} but the caster's name isn't a safe command
     * token.
     *
     * <p>On offline-mode or Bedrock-via-Floodgate servers a player controls their own name,
     * so an unsanitised name spliced into a dispatched command could inject extra arguments.
     * Online-mode names are always {@code [A-Za-z0-9_]}, so they pass through unchanged.
     * {@code {item}} is always a validated item id, so it needs no check.
     */
    static String resolve(AbilityContext ctx, String template) {
        if (template == null || template.isBlank()) return null;
        String out = template.replace("{item}", ctx.itemId());
        if (out.contains("{player}")) {
            String name = ctx.player().getName();
            if (!name.matches("[A-Za-z0-9_]+")) return null; // unsafe name — refuse rather than inject
            out = out.replace("{player}", name);
        }
        return out;
    }
}
