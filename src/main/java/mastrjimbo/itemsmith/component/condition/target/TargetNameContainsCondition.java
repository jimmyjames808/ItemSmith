package mastrjimbo.itemsmith.component.condition.target;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;

import org.bukkit.entity.Entity;

import java.util.Locale;

/** Passes when the target's name contains the given text. Fail-closed when there is no entity target. */
public final class TargetNameContainsCondition implements Condition {

    public static final String ID = "target_name_contains";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("text", ParamType.STRING, "")
                    .label("Text").desc("Substring the target's name must contain (case-insensitive)."))
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
        return "Target Name Contains";
    }

    @Override
    public String description() {
        return "True when the target's name contains the text.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        Entity e = Targets.entity(target);
        if (e == null) return false;
        String n = e.getName();
        return n != null && n.toLowerCase(Locale.ROOT).contains(params.getString("text", "").toLowerCase(Locale.ROOT));
    }
}
