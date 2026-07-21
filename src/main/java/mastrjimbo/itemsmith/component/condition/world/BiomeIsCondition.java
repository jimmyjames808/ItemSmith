package mastrjimbo.itemsmith.component.condition.world;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Condition;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;

/** Passes when the biome at the caster's location matches the given biome key. */
public final class BiomeIsCondition implements Condition {

    public static final String ID = "biome_is";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("biome", ParamType.BIOME, "plains")
                    .label("Biome").desc("Biome key to match (e.g. plains, desert, taiga; no namespace)."))
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
        return "Biome Is";
    }

    @Override
    public String description() {
        return "True when the caster stands in the given biome.";
    }

    @Override
    public ParamSchema schema() {
        return SCHEMA;
    }

    @Override
    public boolean test(AbilityContext ctx, Object target, ParamValues params) {
        String key = ctx.player().getWorld().getBiome(ctx.player().getLocation()).getKey().getKey();
        return key.equalsIgnoreCase(params.getString("biome", "plains"));
    }
}
