package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.TempTasks;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;

import java.util.Locale;

/** Shows a temporary boss bar to the caster, which is automatically hidden after a number of seconds. */
public final class SendBossbarAction implements Action {

    public static final String ID = "send_bossbar";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("text", ParamType.MINIMESSAGE, "")
                    .label("Text").desc("MiniMessage text shown on the boss bar."))
            .add(ParamDef.of("color", ParamType.ENUM, "PURPLE")
                    .label("Color").options("PINK", "BLUE", "RED", "GREEN", "YELLOW", "PURPLE", "WHITE")
                    .desc("Boss bar color."))
            .add(ParamDef.of("seconds", ParamType.INT, 5)
                    .label("Duration (seconds)").min(0).desc("How long the boss bar stays visible."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Send Boss Bar"; }
    @Override public String description() { return "Shows a temporary boss bar to the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String text = params.getString("text", "");
        String color = params.getString("color", "PURPLE");
        int seconds = params.getInt("seconds", 5);

        BossBar.Color barColor;
        try {
            barColor = BossBar.Color.valueOf(color.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            barColor = BossBar.Color.RED;
        }

        BossBar bar = BossBar.bossBar(Text.chat(text), 1.0f, barColor, BossBar.Overlay.PROGRESS);
        ctx.player().showBossBar(bar);
        final Player p = ctx.player();
        TempTasks.later(ctx.plugin(), seconds * 20L, () -> p.hideBossBar(bar));
    }
}
