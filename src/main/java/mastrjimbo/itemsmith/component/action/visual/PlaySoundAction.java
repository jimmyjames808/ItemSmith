package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import mastrjimbo.itemsmith.util.Targets;
import org.bukkit.Location;

/** Plays a sound at the target's location (or the caster's, if the target has no location). */
public final class PlaySoundAction implements Action {

    public static final String ID = "play_sound";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("sound", ParamType.SOUND, "entity.experience_orb.pickup")
                    .label("Sound").desc("Namespaced sound key to play."))
            .add(ParamDef.of("volume", ParamType.DOUBLE, 1.0)
                    .label("Volume").min(0).desc("Playback volume."))
            .add(ParamDef.of("pitch", ParamType.DOUBLE, 1.0)
                    .label("Pitch").min(0).max(2).desc("Playback pitch."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Play Sound"; }
    @Override public String description() { return "Plays a sound at the target's location."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location l = Targets.location(target);
        if (l == null) l = ctx.player().getLocation();
        if (l.getWorld() == null) return;
        String sound = params.getString("sound", "entity.experience_orb.pickup");
        double volume = params.getDouble("volume", 1.0);
        double pitch = params.getDouble("pitch", 1.0);
        l.getWorld().playSound(l, sound, (float) volume, (float) pitch);
    }
}
