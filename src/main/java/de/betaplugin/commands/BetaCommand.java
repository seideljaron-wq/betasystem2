package de.betaplugin.commands;

import de.betaplugin.BetaPlugin;
import de.betaplugin.gui.BetaGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /beta — opens the Beta GUI for Beta Testers.
 */
public class BetaCommand implements CommandExecutor {

    private final BetaPlugin plugin;

    public BetaCommand(BetaPlugin plugin) {
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
            player.sendMessage(ChatColor.RED + "✗ You are not a Beta Tester!");
            return true;
        }

        BetaGUI.open(player, plugin);
        plugin.getWebhookManager().logGUIOpen(player.getName());
        return true;
    }
}
