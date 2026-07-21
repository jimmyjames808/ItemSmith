package mastrjimbo.itemsmith.gui.param;

import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ParamBag;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.pick.RegistryValuePickerScreen;
import mastrjimbo.itemsmith.gui.pick.ValueProviders;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.util.Effects;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Dispatches a single parameter to the right editor based on its {@link ParamDef} type: a native dialog
 * for scalars (text / number / boolean / option) and a chest {@link RegistryValuePickerScreen} for the
 * registry-backed types (effect / material / particle / item-ref). The entered value is written back
 * into the {@link ParamBag}, then {@code reopen} re-renders the parameter screen with the new value.
 */
public final class ParamEditor {

    private final GuiManager gui;

    public ParamEditor(GuiManager gui) {
        this.gui = gui;
    }

    public void edit(Player player, ParamDef def, ParamBag bag, Runnable reopen) {
        Forms forms = gui.forms();
        Component title = Text.chat("<gradient:#4fc3f7:#b388ff>Edit</gradient> <white>" + def.label());
        Component label = Text.chat("<gray>" + def.label());

        switch (def.type()) {
            case BOOLEAN -> forms.bool(player, new Forms.BoolPrompt(title, label, currentBool(def, bag)),
                    reopen, v -> bag.set(def.key(), v));

            case INT -> forms.number(player, numberPrompt(title, label, def, bag, true),
                    reopen, v -> bag.set(def.key(), (int) Math.round(v)));

            case DOUBLE -> forms.number(player, numberPrompt(title, label, def, bag, false),
                    reopen, v -> bag.set(def.key(), v));

            case ENUM -> {
                if (def.enumOptions() == null || def.enumOptions().isEmpty()) {
                    forms.text(player, Forms.TextPrompt.of(title, label, currentString(def, bag)),
                            reopen, v -> bag.set(def.key(), v));
                } else {
                    forms.option(player, new Forms.OptionPrompt(title, label, def.enumOptions(), currentString(def, bag)),
                            reopen, v -> bag.set(def.key(), v));
                }
            }

            case EFFECT -> pick(player, def, bag, reopen, key -> {
                PotionEffectType type = Effects.type(key);
                if (type != null) bag.set(def.key(), type);
            });

            case MATERIAL, HEAD -> pick(player, def, bag, reopen, key -> {
                // Heads are optional: the "none" choice (or a blank) clears back to the particle.
                if (def.type() == ParamType.HEAD && (key == null || key.isBlank() || key.equalsIgnoreCase("none"))) {
                    bag.set(def.key(), "");
                    return;
                }
                Material material = Material.matchMaterial(key);
                if (material != null) bag.set(def.key(), material);
            });

            case PARTICLE, ITEM_REF, SOUND, ENTITY_TYPE, ENCHANTMENT, BIOME, WORLD ->
                    pick(player, def, bag, reopen, key -> bag.set(def.key(), key));

            case STRING_LIST -> forms.text(player,
                    new Forms.TextPrompt(title, label, String.join("\n", currentList(def, bag)), 512, true),
                    reopen, v -> bag.set(def.key(), splitLines(v)));

            // STRING, MINIMESSAGE — plain text
            default -> forms.text(player, Forms.TextPrompt.of(title, label, currentString(def, bag)),
                    reopen, v -> bag.set(def.key(), v));
        }
    }

    /** Opens the chest picker for a registry-backed type; the picker applies {@code setter} then reopens. */
    private void pick(Player player, ParamDef def, ParamBag bag, Runnable reopen, Consumer<String> setter) {
        List<ValueProviders.Option> options = ValueProviders.options(def.type(), gui.registry());
        new RegistryValuePickerScreen(gui,
                Text.chat("<white>Choose <yellow>" + def.label()),
                options, currentString(def, bag), setter, reopen).open(player);
    }

    private Forms.NumberPrompt numberPrompt(Component title, Component label, ParamDef def, ParamBag bag, boolean integer) {
        return new Forms.NumberPrompt(title, label, currentDouble(def, bag), def.min(), def.max(), integer);
    }

    private double currentDouble(ParamDef def, ParamBag bag) {
        Object v = bag.get(def.key());
        if (v instanceof Number n) return n.doubleValue();
        if (def.defaultValue() instanceof Number n) return n.doubleValue();
        return 0;
    }

    private boolean currentBool(ParamDef def, ParamBag bag) {
        Object v = bag.get(def.key());
        if (v instanceof Boolean b) return b;
        return def.defaultValue() instanceof Boolean b && b;
    }

    private String currentString(ParamDef def, ParamBag bag) {
        Object v = bag.get(def.key());
        if (v == null) v = def.defaultValue();
        if (v instanceof Material m) return m.getKey().getKey();
        if (v instanceof PotionEffectType t) return t.getKey().getKey();
        return v == null ? "" : String.valueOf(v);
    }

    @SuppressWarnings("unchecked")
    private List<String> currentList(ParamDef def, ParamBag bag) {
        Object v = bag.get(def.key());
        return v instanceof List<?> list ? (List<String>) list : List.of();
    }

    private List<String> splitLines(String v) {
        if (v == null || v.isEmpty()) return List.of();
        List<String> out = new ArrayList<>();
        for (String line : v.split("\n")) {
            out.add(line);
        }
        return out;
    }
}
