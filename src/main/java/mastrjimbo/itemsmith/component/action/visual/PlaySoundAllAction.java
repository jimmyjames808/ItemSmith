package mastrjimbo.itemsmith.component.action.visual;

import mastrjimbo.itemsmith.engine.AbilityContext;
import mastrjimbo.itemsmith.engine.Action;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.param.ParamValues;
import mastrjimbo.itemsmith.registry.Categories;
import org.bukkit.Location;

/** Plays a sound at the caster's location, audible to everyone nearby (a world-level sound, not player-scoped). */
public final class PlaySoundAllAction implements Action {

    public static final String ID = "play_sound_all";

    private static final ParamSchema SCHEMA = ParamSchema.builder()
            .add(ParamDef.of("sound", ParamType.SOUND, "ui.toast.challenge_complete")
                    .label("Sound").desc("Namespaced sound key to play."))
            .add(ParamDef.of("volume", ParamType.DOUBLE, 1.0)
                    .label("Volume").min(0).desc("Playback volume."))
            .add(ParamDef.of("pitch", ParamType.DOUBLE, 1.0)
                    .label("Pitch").min(0).max(2).desc("Playback pitch."))
            .build();

    @Override public String id() { return ID; }
    @Override public String category() { return Categories.VISUAL; }
    @Override public String displayName() { return "Play Sound (Everyone Nearby)"; }
    @Override public String description() { return "Plays a sound at the caster's location, heard by everyone nearby."; }
    @Override public ParamSchema schema() { return SCHEMA; }

    @Override
    public void run(AbilityContext ctx, Object target, ParamValues params) {
        Location l = ctx.player().getLocation();
        if (l.getWorld() == null) return;
        String sound = params.getString("sound", "ui.toast.challenge_complete");
        double volume = params.getDouble("volume", 1.0);
        double pitch = params.getDouble("pitch", 1.0);
        l.getWorld().playSound(l, sound, (float) volume, (float) pitch);
    }
}
