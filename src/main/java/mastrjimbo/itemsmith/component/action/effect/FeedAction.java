package mastrjimbo.itemsmith.component.action.effect;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.entity.Player;

/**
 * Restores hunger to the target player, capped at the vanilla max of 20. Falls
 * back to the caster when the target isn't a player. No-op if neither resolves.
 */
public final class FeedAction implements Action {

    public static final String ID = "feed";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("amount", ParamType.INT, 20)
                    .label("Amount").min(0).desc("Food level points to restore (max 20)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.EFFECTS; }
    @Override public String displayName() { return "Feed"; }
    @Override public String description() { return "Restores hunger to the target (or the caster)."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Player p = Targets.player(target);
        if (p == null) p = ctx.player();
        if (p == null) return;
        int amount = params.getInt("amount", 20);
        p.setFoodLevel(Math.min(20, p.getFoodLevel() + amount));
    }
}
