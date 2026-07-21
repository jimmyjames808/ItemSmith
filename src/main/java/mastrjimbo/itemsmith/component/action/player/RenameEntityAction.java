package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.entity.Entity;

/** Sets a visible custom name on the target entity. No-op for non-entity targets. */
public final class RenameEntityAction implements Action {

    public static final String ID = "rename_entity";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("name", ParamType.MINIMESSAGE, "")
                    .label("Name").desc("The name to display above the target."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Rename Entity"; }
    @Override public String description() { return "Sets a visible custom name on the target."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Entity e = Targets.entity(target);
        if (e == null) return;
        String name = params.getString("name", "");
        e.customName(Text.item(name));
        e.setCustomNameVisible(true);
    }
}
