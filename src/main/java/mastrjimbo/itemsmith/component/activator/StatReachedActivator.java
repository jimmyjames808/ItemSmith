package mastrjimbo.itemsmith.component.activator;

import mastrjimbo.itemsmith.engine.Activator;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.registry.Activators;
import mastrjimbo.itemsmith.registry.Categories;

/**
 * Fires the moment a persistent stat on the trigger item rises across a threshold — the "level up /
 * evolution" hook, so authors write that logic once instead of duplicating a threshold-gated ability.
 *
 * <p>Unlike most activators this one is not backed by a listener: it carries configurable params
 * ({@code stat} + {@code value}), so it needs a dedicated class rather than a {@code SimpleActivator}.
 * The {@code set_stat}/{@code add_stat} actions detect a rising crossing (previous value {@code <}
 * {@code value}, new value {@code >=} {@code value}) after they mutate a stat and ask the engine to
 * fire this activator; the engine only runs abilities whose {@code stat}+{@code value} match the
 * crossing, so it fires once per crossing rather than on every subsequent stat change.
 */
public final class StatReachedActivator implements Activator {

    public static final String ID = Activators.STAT_REACHED;

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("stat", ParamType.STRING, "stat")
                    .label("Stat").desc("Which stat's rising crossing triggers this (a-z, 0-9, _)."))
            .add(ParamDef.of("value", ParamType.DOUBLE, 1.0)
                    .label("Threshold").desc("Fires once when the stat rises from below this to at or above it."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.ITEM; }
    @Override public String displayName() { return "On Stat Reached"; }
    @Override public String description() { return "A stat rises across a threshold value (once per crossing)."; }
    @Override public ParamSchema schema() { return SCHEMA; }
}
