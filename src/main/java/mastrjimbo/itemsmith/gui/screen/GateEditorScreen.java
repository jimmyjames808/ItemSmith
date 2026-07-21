package mastrjimbo.itemsmith.gui.screen;

import dev.triumphteam.gui.builder.item.PaperItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import mastrjimbo.itemsmith.gate.CostSpec;
import mastrjimbo.itemsmith.gate.Gate;
import mastrjimbo.itemsmith.gate.RegionSpec;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.gui.draft.AbilityDraft;
import mastrjimbo.itemsmith.gui.form.Forms;
import mastrjimbo.itemsmith.util.Text;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Edits an ability's {@link Gate}: who may use it (permission), what it costs (money / xp / hunger /
 * charge-cost), where (WorldGuard region + build/claim rules), a shared cooldown group, and the message
 * shown when it's denied. The immutable {@link Gate} is rebuilt on each change and normalised back to
 * {@link Gate#NONE} when nothing meaningful is set. (Item-ingredient costs are edited in the YAML for
 * now.)
 */
public final class GateEditorScreen {

    private final GuiManager gui;
    private final AbilityDraft ability;
    private final Runnable back;

    public GateEditorScreen(GuiManager gui, AbilityDraft ability, Runnable back) {
        this.gui = gui;
        this.ability = ability;
        this.back = back;
    }

    public void open(Player player) {
        Gate g = gate();
        RegionSpec r = region();
        CostSpec c = cost();
        Gui menu = Gui.gui()
                .title(Text.chat("<aqua>Gate <dark_gray>· who / cost / where"))
                .rows(6)
                .disableAllInteractions()
                .create();

        // Access
        menu.setItem(1, 2, tile(Material.NAME_TAG, "Permission",
                g.permission().isEmpty() ? "<gray>anyone" : "<gray>" + g.permission(),
                event -> gui.forms().text(player, Forms.TextPrompt.of(Text.chat("<white>Permission node"),
                        Text.chat("<gray>blank = anyone"), g.permission()), () -> open(player), this::setPermission)));
        menu.setItem(1, 4, tile(Material.AMETHYST_SHARD, "Charge cost",
                "<gray>" + g.chargeCost() + " per use",
                event -> gui.forms().number(player, num("Charge cost", g.chargeCost(), true),
                        () -> open(player), v -> setChargeCost((int) Math.round(v)))));
        menu.setItem(1, 6, tile(Material.BARRIER, "Deny message",
                g.denyMessage().isEmpty() ? "<gray>silent" : "<gray>" + g.denyMessage(),
                event -> gui.forms().text(player, Forms.TextPrompt.of(Text.chat("<white>Deny message"),
                        Text.chat("<gray>shown when blocked; blank = silent"), g.denyMessage()),
                        () -> open(player), this::setDeny)));

        // Cooldown group
        menu.setItem(2, 3, tile(Material.CLOCK, "Cooldown group",
                g.cooldownGroup().isEmpty() ? "<gray>none" : "<gray>" + g.cooldownGroup(),
                event -> gui.forms().text(player, Forms.TextPrompt.of(Text.chat("<white>Cooldown group key"),
                        Text.chat("<gray>shared across items; blank = none"), g.cooldownGroup()),
                        () -> open(player), this::setCooldownGroup)));
        menu.setItem(2, 5, tile(Material.CLOCK, "Group seconds",
                "<gray>" + g.cooldownGroupSeconds() + "s",
                event -> gui.forms().number(player, num("Cooldown group seconds", g.cooldownGroupSeconds(), false),
                        () -> open(player), this::setCooldownSeconds)));

        // Region
        menu.setItem(3, 2, tile(Material.GRASS_BLOCK, "In region",
                r.region().isEmpty() ? "<gray>anywhere" : "<gray>" + r.region(),
                event -> gui.forms().text(player, Forms.TextPrompt.of(Text.chat("<white>Required WorldGuard region"),
                        Text.chat("<gray>blank = anywhere"), r.region()), () -> open(player), this::setInRegion)));
        menu.setItem(3, 4, toggle("Must be able to build", r.canBuild(),
                event -> setCanBuild(!region().canBuild()), player));
        menu.setItem(3, 6, toggle("Respect claims (block actions)", r.respectClaims(),
                event -> setRespectClaims(!region().respectClaims()), player));

        // Cost
        menu.setItem(4, 2, tile(Material.GOLD_INGOT, "Money cost", "<gray>" + c.money(),
                event -> gui.forms().number(player, num("Money cost", c.money(), false),
                        () -> open(player), this::setMoney)));
        menu.setItem(4, 4, tile(Material.EXPERIENCE_BOTTLE, "XP levels", "<gray>" + c.xpLevels(),
                event -> gui.forms().number(player, num("XP level cost", c.xpLevels(), true),
                        () -> open(player), v -> setXpLevels((int) Math.round(v)))));
        menu.setItem(4, 6, tile(Material.EXPERIENCE_BOTTLE, "XP points", "<gray>" + c.xpPoints(),
                event -> gui.forms().number(player, num("XP point cost", c.xpPoints(), true),
                        () -> open(player), v -> setXpPoints((int) Math.round(v)))));
        menu.setItem(4, 8, tile(Material.COOKED_BEEF, "Hunger cost", "<gray>" + c.hunger(),
                event -> gui.forms().number(player, num("Hunger cost", c.hunger(), false),
                        () -> open(player), this::setHunger)));

        menu.setItem(6, 1, PaperItemBuilder.from(Material.ARROW)
                .name(Text.item("<white>◀ Back"))
                .asGuiItem(event -> back.run()));

        menu.open(player);
    }

    private dev.triumphteam.gui.guis.GuiItem tile(Material icon, String name, String value,
                                                  dev.triumphteam.gui.components.GuiAction<org.bukkit.event.inventory.InventoryClickEvent> action) {
        return PaperItemBuilder.from(icon)
                .name(Text.item("<aqua>" + name))
                .lore(List.of(Text.item(value), Text.item("<green>Click to edit")))
                .asGuiItem(action);
    }

    private dev.triumphteam.gui.guis.GuiItem toggle(String name, boolean on, java.util.function.Consumer<Object> onClick, Player player) {
        return PaperItemBuilder.from(on ? Material.LIME_DYE : Material.GRAY_DYE)
                .name(Text.item((on ? "<green>" : "<gray>") + name))
                .lore(List.of(Text.item("<gray>" + (on ? "yes" : "no")), Text.item("<green>Click to toggle")))
                .asGuiItem(event -> {
                    onClick.accept(null);
                    open(player);
                });
    }

    private Forms.NumberPrompt num(String label, double current, boolean integer) {
        return new Forms.NumberPrompt(Text.chat("<white>" + label), Text.chat("<gray>value"), current, null, null, integer);
    }

    // --- gate rebuild helpers (Gate/CostSpec/RegionSpec are immutable) ---

    private Gate gate() {
        return ability.gate() == null ? Gate.NONE : ability.gate();
    }

    private RegionSpec region() {
        RegionSpec r = gate().region();
        return r == null ? new RegionSpec("", false, false) : r;
    }

    private CostSpec cost() {
        CostSpec c = gate().cost();
        return c == null ? CostSpec.NONE : c;
    }

    private void apply(Gate g) {
        ability.setGate(g.isNoOp() && g.denyMessage().isEmpty() ? Gate.NONE : g);
    }

    private void setPermission(String v) {
        Gate g = gate();
        apply(new Gate(v, g.region(), g.cost(), g.chargeCost(), g.cooldownGroup(), g.cooldownGroupSeconds(), g.denyMessage()));
    }

    private void setChargeCost(int v) {
        Gate g = gate();
        apply(new Gate(g.permission(), g.region(), g.cost(), Math.max(0, v), g.cooldownGroup(), g.cooldownGroupSeconds(), g.denyMessage()));
    }

    private void setDeny(String v) {
        Gate g = gate();
        apply(new Gate(g.permission(), g.region(), g.cost(), g.chargeCost(), g.cooldownGroup(), g.cooldownGroupSeconds(), v));
    }

    private void setCooldownGroup(String v) {
        Gate g = gate();
        apply(new Gate(g.permission(), g.region(), g.cost(), g.chargeCost(), v, g.cooldownGroupSeconds(), g.denyMessage()));
    }

    private void setCooldownSeconds(double v) {
        Gate g = gate();
        apply(new Gate(g.permission(), g.region(), g.cost(), g.chargeCost(), g.cooldownGroup(), Math.max(0, v), g.denyMessage()));
    }

    private void setRegionSpec(RegionSpec rs) {
        Gate g = gate();
        apply(new Gate(g.permission(), rs.isNoOp() ? null : rs, g.cost(), g.chargeCost(), g.cooldownGroup(), g.cooldownGroupSeconds(), g.denyMessage()));
    }

    private void setInRegion(String v) {
        RegionSpec r = region();
        setRegionSpec(new RegionSpec(v, r.canBuild(), r.respectClaims()));
    }

    private void setCanBuild(boolean b) {
        RegionSpec r = region();
        setRegionSpec(new RegionSpec(r.region(), b, r.respectClaims()));
    }

    private void setRespectClaims(boolean b) {
        RegionSpec r = region();
        setRegionSpec(new RegionSpec(r.region(), r.canBuild(), b));
    }

    private void setCostSpec(CostSpec cs) {
        Gate g = gate();
        apply(new Gate(g.permission(), g.region(), cs.isNone() ? CostSpec.NONE : cs, g.chargeCost(), g.cooldownGroup(), g.cooldownGroupSeconds(), g.denyMessage()));
    }

    private void setMoney(double v) {
        CostSpec c = cost();
        setCostSpec(new CostSpec(Math.max(0, v), c.xpLevels(), c.xpPoints(), c.hunger(), c.items()));
    }

    private void setXpLevels(int v) {
        CostSpec c = cost();
        setCostSpec(new CostSpec(c.money(), Math.max(0, v), c.xpPoints(), c.hunger(), c.items()));
    }

    private void setXpPoints(int v) {
        CostSpec c = cost();
        setCostSpec(new CostSpec(c.money(), c.xpLevels(), Math.max(0, v), c.hunger(), c.items()));
    }

    private void setHunger(double v) {
        CostSpec c = cost();
        setCostSpec(new CostSpec(c.money(), c.xpLevels(), c.xpPoints(), Math.max(0, v), c.items()));
    }
}
