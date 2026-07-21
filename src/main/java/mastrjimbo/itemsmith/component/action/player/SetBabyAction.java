package mastrjimbo.itemsmith.component.action.player;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Zombie;

/** Turns the target ageable entity into a baby. No-op for non-ageable targets. */
public final class SetBabyAction implements Action {

    public static final String ID = "set_baby";

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.PLAYER; }
    @Override public String displayName() { return "Set Baby"; }
    @Override public String description() { return "Turns the target into a baby."; }
    @Override public ParamSchema schema() { return ParamSchema.EMPTY; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (target instanceof Ageable a) {
            a.setBaby();
        } else if (target instanceof Zombie z) {
            z.setBaby(true); // zombies/husks/drowned aren't Ageable — separate baby flag
        }
    }
}
