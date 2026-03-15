package de.betaplugin.commands;

import de.betaplugin.BetaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /beta-tester add/remove <player>
 * Usable by javakuba and Beta Admins.
 */
public class BetaTesterCommand implements CommandExecutor {

    private static final String SUPER_ADMIN = "javakuba";
    private final BetaPlugin plugin;

    public BetaTesterCommand(BetaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        boolean isSuperAdmin = player.getName().equalsIgnoreCase(SUPER_ADMIN);
        boolean isAdmin      = plugin.getBetaManager().isAdmin(player.getUniqueId());

        if (!isSuperAdmin && !isAdmin) {
            player.sendMessage(ChatColor.RED + "✗ You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /beta-tester <add|remove> <player>");
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "✗ Player not found or offline.");
            return true;
        }

        switch (action) {
            case "add" -> {
                if (plugin.getBetaManager().isTester(target.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "⚠ " + target.getName() + " is already a Beta Tester.");
                    return true;
                }
                plugin.getBetaManager().addTester(target.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "✔ " + target.getName() + " is now a Beta Tester.");
                target.sendMessage(ChatColor.GREEN + "✔ Welcome to the Beta Team! Use /beta to open the Beta GUI.");
                plugin.getWebhookManager().logBetaTesterAction(player.getName(), "ADD", target.getName());
            }
            case "remove" -> {
                if (!plugin.getBetaManager().isTester(target.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "⚠ " + target.getName() + " is not a Beta Tester.");
                    return true;
                }
                plugin.getBetaManager().removeTester(target.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "✔ " + target.getName() + " is no longer a Beta Tester.");
                target.sendMessage(ChatColor.RED + "✗ Your Beta Tester role has been removed.");
                plugin.getWebhookManager().logBetaTesterAction(player.getName(), "REMOVE", target.getName());
            }
            default -> player.sendMessage(ChatColor.YELLOW + "Usage: /beta-tester <add|remove> <player>");
        }

        return true;
    }
}
