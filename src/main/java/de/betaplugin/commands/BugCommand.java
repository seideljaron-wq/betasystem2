package de.betaplugin.commands;

import de.betaplugin.BetaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /bug <reason> — reports a bug via Discord webhook.
 * Only Beta Testers, Admins, and javakuba can use this.
 */
public class BugCommand implements CommandExecutor {

    private final BetaPlugin plugin;

    public BugCommand(BetaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        boolean isTester     = plugin.getBetaManager().isTester(player.getUniqueId());
        boolean isAdmin      = plugin.getBetaManager().isAdmin(player.getUniqueId());
        boolean isSuperAdmin = player.getName().equalsIgnoreCase("javakuba");

        if (!isTester && !isAdmin && !isSuperAdmin) {
            player.sendMessage(ChatColor.RED + "✗ Only Beta Testers can report bugs!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /bug <description>");
            return true;
        }

        String reason = String.join(" ", args);

        if (reason.length() > 500) {
            player.sendMessage(ChatColor.RED + "✗ Bug description is too long (max 500 characters).");
            return true;
        }

        plugin.getWebhookManager().logBugReport(player.getName(), reason);
        player.sendMessage(ChatColor.GREEN + "✔ Bug report submitted! Thank you for your feedback.");
        return true;
    }
}
