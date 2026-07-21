package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;

/** Clears one of the caster's armor slots. */
public final class UnequipSlotAction implements Action {

    public static final String ID = "unequip_slot";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("slot", ParamType.ENUM, "head")
                    .label("Slot").options("head", "chest", "legs", "feet")
                    .desc("Which armor slot to clear."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Unequip Slot"; }
    @Override public String description() { return "Clears an armor slot on the caster."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        PlayerInventory inv = ctx.player().getInventory();
        String slot = params.getString("slot", "head").trim().toLowerCase(Locale.ROOT);
        switch (slot) {
            case "chest" -> inv.setChestplate(null);
            case "legs" -> inv.setLeggings(null);
            case "feet" -> inv.setBoots(null);
            default -> inv.setHelmet(null);
        }
    }
}
