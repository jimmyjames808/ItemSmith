package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.World;

import java.util.Locale;

/** Passes when the caster's world is the selected dimension (overworld, nether or the end). */
public final class DimensionIsCondition implements Condition {

    public static final String ID = "dimension_is";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("dimension", ParamType.ENUM, "overworld")
                    .label("Dimension").options("overworld", "nether", "the_end")
                    .desc("Which dimension the caster's world must be."))
            .build();

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String category() {
        return Categories.CONDITION;
    }

    @Override
    public String displayName() {
        return "Dimension Is";
    }

    @Override
    public String description() {
        return "True when the caster is in the chosen dimension.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        World.Environment env;
        switch (params.getString("dimension", "overworld").toLowerCase(Locale.ROOT)) {
            case "overworld":
                env = World.Environment.NORMAL;
                break;
            case "nether":
                env = World.Environment.NETHER;
                break;
            case "the_end":
                env = World.Environment.THE_END;
                break;
            default:
                return false;
        }
        return ctx.player().getWorld().getEnvironment() == env;
    }
}
