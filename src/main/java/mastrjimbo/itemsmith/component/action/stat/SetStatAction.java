package mastrjimbo.itemsmith.component.action.stat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;

/**
 * Sets a persistent stat on the trigger item to a fixed value. The value can be a number or text;
 * a {@code <stat:name>} lore token updates to match. No-op if the ability has no item stack (e.g.
 * a projectile-hit ability).
 */
public final class SetStatAction implements Action {

    public static final String ID = "set_stat";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("name", ParamType.STRING, "stat")
                    .label("Stat").desc("Which stat to write (a-z, 0-9, _)."))
            .add(ParamDef.of("value", ParamType.STRING, "")
                    .label("Value").desc("Value to store — a number or text."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ITEM; }
    @Override public String displayName() { return "Set Stat"; }
    @Override public String description() { return "Sets a persistent stat on the trigger item."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack stack = ctx.itemStack();
        if (stack == null) return;
        String name = params.getString("name", "stat").trim();
        if (name.isEmpty()) return;
        StatHooks.mutate(ctx, stack, name, () -> ctx.registry().setStat(stack, name, params.getString("value", "")));
    }
}
