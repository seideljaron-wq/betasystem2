package de.betaplugin.commands;

import de.betaplugin.BetaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /beta-admin add/remove <player>
 * Only "javakuba" can use this.
 */
public class BetaAdminCommand implements CommandExecutor {

    private static final String SUPER_ADMIN = "javakuba";
    private final BetaPlugin plugin;

    public BetaAdminCommand(BetaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (!player.getName().equalsIgnoreCase(SUPER_ADMIN)) {
            player.sendMessage(ChatColor.RED + "✗ You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /beta-admin <add|remove> <player>");
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
                if (plugin.getBetaManager().isAdmin(target.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "⚠ " + target.getName() + " is already a Beta Admin.");
                    return true;
                }
                plugin.getBetaManager().addAdmin(target.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "✔ " + target.getName() + " is now a Beta Admin.");
                target.sendMessage(ChatColor.GREEN + "✔ You have been promoted to Beta Admin!");
                plugin.getWebhookManager().logBetaAdminAction(player.getName(), "ADD Admin", target.getName());
            }
            case "remove" -> {
                if (!plugin.getBetaManager().isAdmin(target.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "⚠ " + target.getName() + " is not a Beta Admin.");
                    return true;
                }
                plugin.getBetaManager().removeAdmin(target.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "✔ " + target.getName() + " is no longer a Beta Admin.");
                target.sendMessage(ChatColor.RED + "✗ Your Beta Admin role has been removed.");
                plugin.getWebhookManager().logBetaAdminAction(player.getName(), "REMOVE Admin", target.getName());
            }
            default -> player.sendMessage(ChatColor.YELLOW + "Usage: /beta-admin <add|remove> <player>");
        }

        return true;
    }
}
