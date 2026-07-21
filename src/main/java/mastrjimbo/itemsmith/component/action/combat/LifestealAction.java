package mastrjimbo.itemsmith.component.action.combat;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Heals the caster for a fraction of the damage from the triggering hit. Only meaningful on a
 * damage-dealing trigger (e.g. on-hit-entity), where the event carries the damage figure.
 */
public final class LifestealAction implements Action {

    public static final String ID = "lifesteal";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("fraction", ParamType.DOUBLE, 0.5)
                    .label("Fraction").range(0, 10).desc("Portion of the hit's damage healed back (0.5 = half)."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.COMBAT; }
    @Override public String displayName() { return "Lifesteal"; }
    @Override public String description() { return "Heals the caster for a share of damage dealt."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        if (!(ctx.event() instanceof EntityDamageByEntityEvent event)) return;
        Player player = ctx.player();
        if (player == null) return;
        double heal = event.getFinalDamage() * params.getDouble("fraction", 0.5);
        if (heal <= 0) return;
        AttributeInstance maxAttr = player.getAttribute(Attribute.MAX_HEALTH);
        double max = maxAttr != null ? maxAttr.getValue() : 20.0;
        player.setHealth(Math.min(max, player.getHealth() + heal));
    }
}
