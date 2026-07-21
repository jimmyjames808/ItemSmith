package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Locale;

/** Puts the triggering item into one of the caster's armor slots. No-op if there is no triggering item. */
public final class EquipSlotAction implements Action {

    public static final String ID = "equip_slot";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("slot", ParamType.ENUM, "head")
                    .label("Slot").options("head", "chest", "legs", "feet")
                    .desc("Which armor slot to fill with the triggering item."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Equip Slot"; }
    @Override public String description() { return "Puts the triggering item into an armor slot."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        ItemStack s = ctx.itemStack();
        if (s == null) return;
        PlayerInventory inv = ctx.player().getInventory();
        String slot = params.getString("slot", "head").trim().toLowerCase(Locale.ROOT);
        switch (slot) {
            case "chest" -> inv.setChestplate(s);
            case "legs" -> inv.setLeggings(s);
            case "feet" -> inv.setBoots(s);
            default -> inv.setHelmet(s);
        }
    }
}
