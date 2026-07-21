package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Material;

/** Puts a chosen material on the caster's native item-cooldown, exactly like eating a golden apple. */
public final class SetItemCooldownAction implements Action {

    public static final String ID = "set_item_cooldown";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("material", ParamType.MATERIAL, "DIAMOND")
                    .label("Material").desc("Item type the cooldown applies to."))
            .add(ParamDef.of("ticks", ParamType.INT, 100)
                    .label("Cooldown (ticks)").min(0).desc("Cooldown length in ticks (20 = 1 second)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Set Item Cooldown"; }
    @Override public String description() { return "Puts a material on the caster's native item cooldown."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Material material = params.getMaterial("material");
        if (material == null) return;
        int ticks = Math.max(0, params.getInt("ticks", 100));
        ctx.player().setCooldown(material, ticks);
    }
}
