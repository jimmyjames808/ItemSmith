package mastrjimbo.itemsmith.command;

import mastrjimbo.itemsmith.ItemSmith;
import mastrjimbo.itemsmith.engine.ItemRegistry;
import mastrjimbo.itemsmith.gui.GuiManager;
import mastrjimbo.itemsmith.util.Text;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Handles {@code /itemsmith catalog|open|create|get|give|list|reload} plus tab completion. */
public final class ItemCommand implements CommandExecutor, TabCompleter {

    private static final Component PREFIX =
            Text.chat("<gray>[</gray><gradient:#4fc3f7:#b388ff>ItemSmith</gradient><gray>]</gray> ");
    private static final String PERM = "itemsmith.admin";
    private static final String CATALOG_PERM = "itemsmith.catalog";
    private static final List<String> SUBS =
            List.of("catalog", "open", "create", "get", "give", "list", "reload");

    private final ItemSmith plugin;
    private final ItemRegistry registry;
    private final GuiManager gui;

    public ItemCommand(ItemSmith plugin, ItemRegistry registry, GuiManager gui) {
        this.plugin = plugin;
        this.registry = registry;
        this.gui = gui;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            usage(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        // The catalog is the one player-facing subcommand — its own permission (default granted), checked
        // before the admin gate so non-admins can browse but reach nothing else.
        if (sub.equals("catalog")) {
            if (!sender.hasPermission(CATALOG_PERM)) {
                send(sender, "<red>You don't have permission to open the catalog.");
                return true;
            }
            if (!(sender instanceof Player player)) {
                send(sender, "<red>Only players can open the catalog.");
                return true;
            }
            gui.openCatalog(player);
            return true;
        }

        if (!sender.hasPermission(PERM)) {
            send(sender, "<red>You don't have permission to use ItemSmith.");
            return true;
        }

        switch (sub) {
            case "reload" -> {
                int count = plugin.reload();
                send(sender, "<green>Reloaded <white>" + count + "<green> item(s).");
            }
            case "list" -> {
                if (registry.ids().isEmpty()) {
                    send(sender, "<yellow>No items are defined. Add files under plugins/ItemSmith/items/.");
                } else {
                    send(sender, "<gray>Items: <white>" + String.join("<gray>, <white>", registry.ids()));
                }
            }
            case "get" -> {
                if (!(sender instanceof Player player)) {
                    send(sender, "<red>Only players can use /itemsmith get.");
                    return true;
                }
                if (args.length < 2) {
                    send(sender, "<red>Usage: /itemsmith get <id>");
                    return true;
                }
                giveItem(sender, player, args[1]);
            }
            case "give" -> {
                if (args.length < 2) {
                    send(sender, "<red>Usage: /itemsmith give <id> [player]");
                    return true;
                }
                Player target;
                if (args.length >= 3) {
                    target = Bukkit.getPlayerExact(args[2]);
                    if (target == null) {
                        send(sender, "<red>Player '" + args[2] + "' is not online.");
                        return true;
                    }
                } else if (sender instanceof Player self) {
                    target = self;
                } else {
                    send(sender, "<red>Specify a player: /itemsmith give <id> <player>");
                    return true;
                }
                giveItem(sender, target, args[1]);
            }
            case "open" -> {
                if (!(sender instanceof Player player)) {
                    send(sender, "<red>Only players can open the creator.");
                    return true;
                }
                gui.openList(player);
            }
            case "create" -> {
                if (!(sender instanceof Player player)) {
                    send(sender, "<red>Only players can create items.");
                    return true;
                }
                gui.openNew(player);
            }
            default -> usage(sender);
        }
        return true;
    }

    private void giveItem(CommandSender sender, Player target, String id) {
        ItemStack item = registry.build(id);
        if (item == null) {
            send(sender, "<red>Unknown item id '" + id + "'. Try /itemsmith list.");
            return;
        }
        target.getInventory().addItem(item);
        if (sender.equals(target)) {
            send(sender, "<green>You received <white>" + id + "<green>.");
        } else {
            send(sender, "<green>Gave <white>" + id + "<green> to <white>" + target.getName() + "<green>.");
            send(target, "<green>You received <white>" + id + "<green>.");
        }
    }

    private void usage(CommandSender sender) {
        if (sender.hasPermission(PERM)) {
            send(sender, "<gray>/itemsmith <white>open <gray>· <white>catalog <gray>· <white>get <id> <gray>· <white>give <id> [player] <gray>· <white>list <gray>· <white>reload");
        } else {
            send(sender, "<gray>/itemsmith <white>catalog");
        }
    }

    private void send(CommandSender sender, String miniMessage) {
        sender.sendMessage(PREFIX.append(Text.chat(miniMessage)));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            String prefix = args[0].toLowerCase(Locale.ROOT);
            boolean admin = sender.hasPermission(PERM);
            for (String sub : SUBS) {
                if (!sub.startsWith(prefix)) continue;
                if (sub.equals("catalog")) {
                    if (sender.hasPermission(CATALOG_PERM)) out.add(sub);
                } else if (admin) {
                    out.add(sub);
                }
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("get") || args[0].equalsIgnoreCase("give"))) {
            String prefix = args[1].toLowerCase(Locale.ROOT);
            for (String id : registry.ids()) {
                if (id.startsWith(prefix)) out.add(id);
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            String prefix = args[2].toLowerCase(Locale.ROOT);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) out.add(p.getName());
            }
        }
        return out;
    }
}
