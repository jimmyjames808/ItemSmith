package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ParamBag;
import mastrjimbo.itemsmith.gui.param.ParamEditor;
import mastrjimbo.itemsmith.param.ParamDef;
import mastrjimbo.itemsmith.param.ParamSchema;
import mastrjimbo.itemsmith.param.ParamType;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * The generic, schema-driven parameter editor reused by every component (activator/condition/targeter/
 * action). Renders one tile per {@link ParamDef} (filtered by the session's Simple/Advanced toggle);
 * clicking a tile opens the matching native dialog via {@link ParamEditor} and re-renders with the new
 * value. {@code back} re-opens whatever screen owns this component.
 */
public final class ParamEditorScreen {

    private final GuiManager gui;
    private final EditSession session;
    private final Component title;
    private final ParamSchema schema;
    private final ParamBag bag;
    private final Runnable back;

    public ParamEditorScreen(GuiManager gui, EditSession session, Component title,
                             ParamSchema schema, ParamBag bag, Runnable back) {
        this.gui = gui;
        this.session = session;
        this.title = title;
        this.schema = schema;
        this.bag = bag;
        this.back = back;
    }

    public void open(Player player) {
        Gui menu = Gui.gui().title(title).rows(6).disableAllInteractions().create();

        int slot = 0;
        boolean hasAdvanced = false;
        for (ParamDef def : schema.defs()) {
            if (def.advanced()) {
                hasAdvanced = true;
                if (!session.advanced()) continue;
            }
            if (slot >= 45) break;
            menu.setItem(slot++, paramItem(player, def));
        }
        if (slot == 0) {
            menu.setItem(22, PaperItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .name(Text.item("<gray>No parameters to edit"))
                    .asGuiItem(event -> {
                    }));
        }

        if (hasAdvanced) {
            menu.setItem(6, 5, PaperItemBuilder.from(session.advanced() ? Material.REDSTONE_TORCH : Material.LEVER)
                    .name(Text.item(session.advanced() ? "<yellow>Advanced: ON" : "<gray>Advanced: OFF"))
                    .lore(List.of(Text.item("<dark_gray>Toggle advanced parameters")))
                    .asGuiItem(event -> {
                        session.toggleAdvanced();
                        open(player);
                    }));
        }
        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> {
                    if (back != null) back.run();
                }));

        menu.open(player);
    }

    private GuiItem paramItem(Player player, ParamDef def) {
        return PaperItemBuilder.from(iconFor(def.type()))
                .name(Text.item("<yellow>" + def.label()))
                .lore(loreFor(def))
                .asGuiItem(event -> new ParamEditor(gui).edit(player, def, bag, () -> open(player)));
    }

    private List<Component> loreFor(ParamDef def) {
        List<Component> lore = new ArrayList<>();
        if (def.description() != null && !def.description().isEmpty()) {
            lore.add(Text.item("<gray>" + def.description()));
        }
        lore.add(Text.item("<dark_gray>type: " + def.type().name().toLowerCase()));
        lore.add(Text.item("<white>= <yellow>" + display(def)));
        lore.add(Component.empty());
        lore.add(Text.item("<green>Click to edit"));
        return lore;
    }

    private String display(ParamDef def) {
        Object v = bag.get(def.key());
        if (v == null) v = def.defaultValue();
        if (v instanceof Material m) return m.getKey().getKey();
        if (v instanceof PotionEffectType t) return t.getKey().getKey();
        if (v instanceof List<?> list) return list.isEmpty() ? "(empty)" : String.join(", ",
                list.stream().map(String::valueOf).toList());
        return v == null ? "(none)" : String.valueOf(v);
    }

    private Material iconFor(ParamType type) {
        return switch (type) {
            case INT, DOUBLE -> Material.REPEATER;
            case BOOLEAN -> Material.LEVER;
            case ENUM -> Material.COMPARATOR;
            case MATERIAL -> Material.GRASS_BLOCK;
            case EFFECT -> Material.POTION;
            case SOUND -> Material.NOTE_BLOCK;
            case PARTICLE -> Material.FIREWORK_STAR;
            case ITEM_REF -> Material.CHEST;
            case ENTITY_TYPE -> Material.ZOMBIE_HEAD;
            case ENCHANTMENT -> Material.ENCHANTED_BOOK;
            case BIOME -> Material.GRASS_BLOCK;
            case WORLD -> Material.GRASS_BLOCK;
            case STRING_LIST -> Material.BOOKSHELF;
            default -> Material.NAME_TAG; // STRING, MINIMESSAGE
        };
    }
}
