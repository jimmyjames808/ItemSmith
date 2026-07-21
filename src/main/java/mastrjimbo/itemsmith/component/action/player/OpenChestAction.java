package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Text;

/** Opens a fresh, empty 27-slot chest inventory for the caster with a custom title. */
public final class OpenChestAction implements Action {

    public static final String ID = "open_chest";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("title", ParamType.STRING, "Chest")
                    .label("Title").desc("The inventory's display title."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Open Chest"; }
    @Override public String description() { return "Opens a fresh 27-slot chest inventory for the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        String title = params.getString("title", "Chest");
        ctx.player().openInventory(ctx.plugin().getServer().createInventory(null, 27, Text.item(title)));
    }
}
