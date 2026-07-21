package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.title.Title;

/** Shows a MiniMessage-formatted title/subtitle pair to the caster. */
public final class SendTitleAction implements Action {

    public static final String ID = "send_title";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("title", ParamType.MINIMESSAGE, "")
                    .label("Title").desc("MiniMessage text for the main title line."))
            .add(ParamDef.of("subtitle", ParamType.MINIMESSAGE, "")
                    .label("Subtitle").desc("MiniMessage text for the subtitle line."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Send Title"; }
    @Override public String description() { return "Shows a title and subtitle to the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String title = params.getString("title", "");
        String subtitle = params.getString("subtitle", "");
        ctx.player().showTitle(Title.title(Text.chat(title), Text.chat(subtitle)));
    }
}
