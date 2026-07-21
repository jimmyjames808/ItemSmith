package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gate.Gate;
import mastrjimbo.itemsmith.gui.EditSession;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.AbilityDraft;
import mastrjimbo.itemsmith.gui.draft.ConfiguredDraft;
import mastrjimbo.itemsmith.gui.draft.ParamBag;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.gui.pick.ComponentPickerScreen;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * The ability hub: pick its activator/targeter, edit its conditions, set its cooldown, and add / edit /
 * remove its top-level actions. Leaf-action params edit inline; flow (delay/repeat/if/random) nodes are
 * added here but their nested bodies are edited in the action-tree screen.
 */
public final class AbilityEditorScreen {

    private final GuiManager gui;
    private final EditSession session;
    private final AbilityDraft ability;

    public AbilityEditorScreen(GuiManager gui, EditSession session, AbilityDraft ability) {
        this.gui = gui;
        this.session = session;
        this.ability = ability;
    }

    public void open(Player player) {
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Edit ability</aqua> <dark_gray>·</dark_gray> <white>" + ability.activatorId()))
                .rows(6)
                .disableAllInteractions()
                .create();

        menu.setItem(1, 2, PaperItemBuilder.from(Material.LEVER)
                .name(Text.item("<aqua>Activator"))
                .lore(List.of(Text.item("<gray>" + ability.activatorId()),
                        Text.item("<green>Click to change")))
                .asGuiItem(event -> new ComponentPickerScreen<>(gui,
                        Text.chat("<white>Choose <yellow>activator"),
                        gui.registries().activators(), ability.activatorId(),
                        act -> ability.setActivatorId(act.id()),
                        () -> open(player)).open(player)));

        menu.setItem(1, 4, PaperItemBuilder.from(Material.COMPARATOR)
                .name(Text.item("<aqua>Conditions"))
                .lore(List.of(Text.item("<gray>" + ability.conditions().size() + " defined"),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> new ConditionListScreen(gui, session,
                        Text.chat("<aqua>Conditions</aqua> <dark_gray>·</dark_gray> <white>" + ability.activatorId()),
                        ability.conditions(), () -> open(player)).open(player)));

        boolean targeterHasParams = !ability.targeter().def().schema().defs().isEmpty();
        menu.setItem(1, 6, PaperItemBuilder.from(Material.ENDER_EYE)
                .name(Text.item("<aqua>Targeter"))
                .lore(List.of(Text.item("<gray>" + ability.targeter().def().id()),
                        Text.item("<green>Left-click: change"),
                        targeterHasParams ? Text.item("<yellow>Right-click: edit params")
                                : Text.item("<dark_gray>no params")))
                .asGuiItem(event -> {
                    if (event.isRightClick() && targeterHasParams) {
                        new ParamEditorScreen(gui, session,
                                Text.chat("<white>" + ability.targeter().def().displayName()),
                                ability.targeter().def().schema(), ability.targeter().params(),
                                () -> open(player)).open(player);
                        return;
                    }
                    new ComponentPickerScreen<>(gui, Text.chat("<white>Choose <yellow>targeter"),
                            gui.registries().targeters(), ability.targeter().def().id(),
                            tgt -> ability.setTargeter(new ConfiguredDraft<>(tgt, new ParamBag())),
                            () -> open(player)).open(player);
                }));

        menu.setItem(1, 8, PaperItemBuilder.from(Material.CLOCK)
                .name(Text.item("<aqua>Cooldown"))
                .lore(List.of(Text.item("<gray>" + ability.cooldownSeconds() + "s"),
                        Text.item("<dark_gray>reuse delay for this ability"),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> gui.forms().number(player,
                        new Forms.NumberPrompt(Text.chat("<white>Ability cooldown"),
                                Text.chat("<gray>Seconds"), ability.cooldownSeconds(), 0.0, 60.0, false),
                        () -> open(player),
                        ability::setCooldownSeconds)));

        menu.setItem(2, 5, PaperItemBuilder.from(Material.IRON_BARS)
                .name(Text.item("<aqua>Gate <dark_gray>· governance"))
                .lore(List.of(Text.item("<gray>" + gateSummary()),
                        Text.item("<dark_gray>permission · cost · region · cooldown group"),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> new GateEditorScreen(gui, ability, () -> open(player)).open(player)));

        menu.setItem(3, 5, PaperItemBuilder.from(Material.NETHER_STAR)
                .name(Text.item("<aqua>Actions <dark_gray>(" + ability.actions().size() + ")"))
                .lore(List.of(Text.item("<gray>the effects this ability runs, in order"),
                        Text.item("<green>Click to edit")))
                .asGuiItem(event -> new ActionTreeScreen(gui, session, "Actions",
                        ability.actions(), () -> open(player)).open(player)));

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> new AbilityListScreen(gui, session).open(player)));

        menu.open(player);
    }

    private String gateSummary() {
        Gate g = ability.gate();
        if (g == null || (g.isNoOp() && g.denyMessage().isEmpty())) return "none";
        List<String> parts = new ArrayList<>();
        if (!g.permission().isEmpty()) parts.add("permission");
        if (g.chargeCost() > 0) parts.add("charge-cost");
        if (g.cost() != null && !g.cost().isNone()) parts.add("cost");
        if (g.region() != null && !g.region().isNoOp()) parts.add("region");
        if (!g.cooldownGroup().isEmpty()) parts.add("cooldown-group");
        return parts.isEmpty() ? "deny message only" : String.join(", ", parts);
    }
}
