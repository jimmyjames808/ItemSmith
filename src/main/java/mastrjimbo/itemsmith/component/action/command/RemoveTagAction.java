package mastrjimbo.itemsmith.component.action.command;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Entity;

/**
 * Removes a scoreboard tag from the target entity, defaulting to the caster
 * when the target isn't an entity.
 */
public final class RemoveTagAction implements Action {

    public static final String ID = "remove_tag";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("tag", ParamType.STRING, "")
                    .label("Tag").desc("Scoreboard tag to remove."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMMAND; }
    @Override public String displayName() { return "Remove Tag"; }
    @Override public String description() { return "Removes a scoreboard tag from the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String tag = params.getString("tag", "");
        if (tag.isBlank()) return;
        Entity e = Targets.entity(target);
        if (e == null) e = ctx.player();
        if (e == null) return;
        e.removeScoreboardTag(tag);
    }
}
