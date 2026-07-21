package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gate.DepletionPolicy;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.ItemDraft;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

/**
 * Edits an item's charge counter and depletion settings: toggle charges on/off, set the starting and
 * maximum counts, choose what happens at zero ({@code consume} / {@code break} / {@code keep_inert}),
 * and whether the counter mirrors onto the vanilla durability bar.
 */
public final class ItemSettingsScreen {

    private static final List<String> POLICIES = List.of("consume", "break", "keep_inert");

    private final GuiManager gui;
    private final EditSession session;
    private final Runnable back;

    public ItemSettingsScreen(GuiManager gui, EditSession session, Runnable back) {
        this.gui = gui;
        this.session = session;
        this.back = back;
    }

    public void open(Player player) {
        ItemDraft draft = session.draft();
        boolean on = draft.charges() != null;

        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Charges & settings"))
                .rows(3)
                .disableAllInteractions()
                .create();

        menu.setItem(10, PaperItemBuilder.from(on ? Material.AMETHYST_SHARD : Material.GRAY_DYE)
                .name(Text.item(on ? "<green>Charges: ON" : "<gray>Charges: OFF"))
                .lore(List.of(Text.item("<gray>limited uses per item"),
                        Text.item(on ? "<red>Click to disable" : "<green>Click to enable")))
                .asGuiItem(event -> {
                    if (on) {
                        draft.setCharges(null);
                        draft.setMaxCharges(null);
                    } else {
                        draft.setCharges(1);
                        draft.setMaxCharges(1);
                    }
                    open(player);
                }));

        if (on) {
            menu.setItem(12, PaperItemBuilder.from(Material.REPEATER)
                    .name(Text.item("<aqua>Starting charges"))
                    .lore(List.of(Text.item("<gray>" + draft.charges()), Text.item("<green>Click to edit")))
                    .asGuiItem(event -> gui.forms().number(player,
                            new Forms.NumberPrompt(Text.chat("<white>Starting charges"), Text.chat("<gray>count"),
                                    draft.charges(), null, null, true),
                            () -> open(player),
                            v -> draft.setCharges(Math.max(1, (int) Math.round(v))))));
            menu.setItem(13, PaperItemBuilder.from(Material.REPEATER)
                    .name(Text.item("<aqua>Max charges"))
                    .lore(List.of(Text.item("<gray>" + draft.maxCharges()), Text.item("<green>Click to edit")))
                    .asGuiItem(event -> gui.forms().number(player,
                            new Forms.NumberPrompt(Text.chat("<white>Max charges"), Text.chat("<gray>count"),
                                    draft.maxCharges(), null, null, true),
                            () -> open(player),
                            v -> draft.setMaxCharges(Math.max(1, (int) Math.round(v))))));
            menu.setItem(14, PaperItemBuilder.from(Material.LAVA_BUCKET)
                    .name(Text.item("<aqua>On depletion"))
                    .lore(List.of(Text.item("<gray>" + draft.onDepletion().name().toLowerCase(Locale.ROOT)),
                            Text.item("<dark_gray>consume: use up · break: shatter · keep_inert: stay at 0"),
                            Text.item("<green>Click to change")))
                    .asGuiItem(event -> gui.forms().option(player,
                            new Forms.OptionPrompt(Text.chat("<white>On depletion"), Text.chat("<gray>at zero charges"),
                                    POLICIES, draft.onDepletion().name().toLowerCase(Locale.ROOT)),
                            () -> open(player),
                            v -> draft.setOnDepletion(DepletionPolicy.from(v)))));
            menu.setItem(15, PaperItemBuilder.from(Material.DIAMOND_PICKAXE)
                    .name(Text.item("<aqua>Durability bar"))
                    .lore(List.of(Text.item("<gray>" + (draft.durabilityBar() ? "shown" : "hidden")),
                            Text.item("<dark_gray>mirror charges onto the vanilla bar"),
                            Text.item("<green>Click to toggle")))
                    .asGuiItem(event -> gui.forms().bool(player,
                            new Forms.BoolPrompt(Text.chat("<white>Durability bar"),
                                    Text.chat("<gray>mirror charges onto the bar"), draft.durabilityBar()),
                            () -> open(player),
                            draft::setDurabilityBar)));
        }

        menu.setItem(3, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }
}
