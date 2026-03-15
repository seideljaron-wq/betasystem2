package de.betaplugin.listeners;

import de.betaplugin.BetaPlugin;
import de.betaplugin.gui.BetaGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {

    private final BetaPlugin plugin;

    // Tracks players waiting for chat input
    // Value: "bug", "feature", "feedback"
    private final Map<UUID, String> awaitingInput = new HashMap<>();

    public GUIListener(BetaPlugin plugin) {
        this.plugin = plugin;
    }

    // ══════════════════════════════════════════════════════════════
    //  INVENTORY CLICK
    // ══════════════════════════════════════════════════════════════

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;

        String title = e.getView().getTitle();

        // ── Main Beta GUI ────────────────────────────────────────
        if (title.equals(BetaGUI.GUI_TITLE)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;

            switch (e.getSlot()) {
                case BetaGUI.SLOT_BUG -> {
                    player.closeInventory();
                    awaitingInput.put(player.getUniqueId(), "bug");
                    player.sendMessage("");
                    player.sendMessage(ChatColor.RED + "🐛 " + ChatColor.BOLD + "Bug Report");
                    player.sendMessage(ChatColor.GRAY + "Type your bug description in chat.");
                    player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to abort.");
                    player.sendMessage("");
                }
                case BetaGUI.SLOT_FEATURE -> {
                    player.closeInventory();
                    awaitingInput.put(player.getUniqueId(), "feature");
                    player.sendMessage("");
                    player.sendMessage(ChatColor.GREEN + "💡 " + ChatColor.BOLD + "Feature Suggestion");
                    player.sendMessage(ChatColor.GRAY + "Type your feature suggestion in chat.");
                    player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to abort.");
                    player.sendMessage("");
                }
                case BetaGUI.SLOT_STATUS -> {
                    // Already shown in GUI, no action needed
                    player.sendMessage(ChatColor.AQUA + "ℹ Your status is shown in the GUI.");
                }
                case BetaGUI.SLOT_PLAYERLIST -> {
                    player.closeInventory();
                    BetaGUI.openPlayerList(player, plugin);
                }
                case BetaGUI.SLOT_FEEDBACK -> {
                    player.closeInventory();
                    awaitingInput.put(player.getUniqueId(), "feedback");
                    player.sendMessage("");
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "📝 " + ChatColor.BOLD + "Submit Feedback");
                    player.sendMessage(ChatColor.GRAY + "Type your feedback in chat.");
                    player.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to abort.");
                    player.sendMessage("");
                }
            }
        }

        // ── Player List Sub-GUI ──────────────────────────────────
        if (title.contains("Beta Testers")) {
            e.setCancelled(true);
            if (e.getSlot() == 49) { // Back button
                player.closeInventory();
                BetaGUI.open(player, plugin);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    //  CHAT INPUT
    // ══════════════════════════════════════════════════════════════

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID   uuid   = player.getUniqueId();

        if (!awaitingInput.containsKey(uuid)) return;

        e.setCancelled(true); // Don't broadcast to chat
        String type  = awaitingInput.remove(uuid);
        String input = e.getMessage().trim();

        if (input.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.YELLOW + "✗ Cancelled.");
            return;
        }

        if (input.length() > 500) {
            player.sendMessage(ChatColor.RED + "✗ Too long! Maximum 500 characters.");
            awaitingInput.put(uuid, type); // re-prompt
            return;
        }

        switch (type) {
            case "bug" -> {
                plugin.getWebhookManager().logBugReport(player.getName(), input);
                player.sendMessage(ChatColor.GREEN + "✔ Bug report submitted! Thank you.");
            }
            case "feature" -> {
                plugin.getWebhookManager().logFeatureSuggestion(player.getName(), input);
                player.sendMessage(ChatColor.GREEN + "✔ Feature suggestion submitted! Thank you.");
            }
            case "feedback" -> {
                plugin.getWebhookManager().logFeedback(player.getName(), input);
                player.sendMessage(ChatColor.GREEN + "✔ Feedback submitted! Thank you.");
            }
        }
    }
}
