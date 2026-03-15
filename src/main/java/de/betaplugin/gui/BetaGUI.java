package de.betaplugin.gui;

import de.betaplugin.BetaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class BetaGUI {

    public static final String GUI_TITLE = ChatColor.DARK_AQUA + "✦ " + ChatColor.AQUA + "Beta Panel" + ChatColor.DARK_AQUA + " ✦";

    // Slot positions
    public static final int SLOT_BUG        = 11;
    public static final int SLOT_FEATURE    = 13;
    public static final int SLOT_STATUS     = 15;
    public static final int SLOT_PLAYERLIST = 29;
    public static final int SLOT_FEEDBACK   = 33;

    public static void open(Player player, BetaPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 45, GUI_TITLE);

        // ── Fill borders with glass ────────────────────────────────
        ItemStack glass = makeItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + " ", null);
        for (int i = 0; i < 45; i++) inv.setItem(i, glass);

        // ── Bug Report ─────────────────────────────────────────────
        inv.setItem(SLOT_BUG, makeItem(
            Material.RED_DYE,
            ChatColor.RED + "🐛 Report a Bug",
            Arrays.asList(
                ChatColor.GRAY + "Found something broken?",
                ChatColor.GRAY + "Click to open bug report chat.",
                "",
                ChatColor.YELLOW + "➜ Click to report"
            )
        ));

        // ── Feature Suggestion ─────────────────────────────────────
        inv.setItem(SLOT_FEATURE, makeItem(
            Material.LIME_DYE,
            ChatColor.GREEN + "💡 Suggest a Feature",
            Arrays.asList(
                ChatColor.GRAY + "Have a great idea?",
                ChatColor.GRAY + "Click to suggest a new feature.",
                "",
                ChatColor.YELLOW + "➜ Click to suggest"
            )
        ));

        // ── Beta Status ────────────────────────────────────────────
        boolean isAdmin = plugin.getBetaManager().isAdmin(player.getUniqueId());
        String role     = player.getName().equalsIgnoreCase("javakuba") ? "Super Admin"
                        : isAdmin ? "Beta Admin" : "Beta Tester";

        int testerCount = plugin.getBetaManager().getTesters().size();
        int adminCount  = plugin.getBetaManager().getAdmins().size();

        inv.setItem(SLOT_STATUS, makeItem(
            Material.NETHER_STAR,
            ChatColor.AQUA + "⭐ Your Beta Status",
            Arrays.asList(
                ChatColor.GRAY + "Role: " + ChatColor.YELLOW + role,
                ChatColor.GRAY + "Beta Testers: " + ChatColor.WHITE + testerCount,
                ChatColor.GRAY + "Beta Admins: " + ChatColor.WHITE + adminCount,
                "",
                ChatColor.GREEN + "✔ You are part of the Beta Team!"
            )
        ));

        // ── Player List ────────────────────────────────────────────
        inv.setItem(SLOT_PLAYERLIST, makeItem(
            Material.PLAYER_HEAD,
            ChatColor.GOLD + "👥 Beta Player List",
            Arrays.asList(
                ChatColor.GRAY + "View all active Beta Testers",
                ChatColor.GRAY + "and their online status.",
                "",
                ChatColor.YELLOW + "➜ Click to view"
            )
        ));

        // ── Feedback ───────────────────────────────────────────────
        inv.setItem(SLOT_FEEDBACK, makeItem(
            Material.BOOK,
            ChatColor.LIGHT_PURPLE + "📝 Submit Feedback",
            Arrays.asList(
                ChatColor.GRAY + "General feedback about",
                ChatColor.GRAY + "the beta experience.",
                "",
                ChatColor.YELLOW + "➜ Click to write"
            )
        ));

        player.openInventory(inv);
    }

    // ── Player List Sub-GUI ────────────────────────────────────────
    public static void openPlayerList(Player player, BetaPlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 54,
            ChatColor.DARK_AQUA + "✦ " + ChatColor.AQUA + "Beta Testers" + ChatColor.DARK_AQUA + " ✦");

        ItemStack glass = makeItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + " ", null);
        for (int i = 0; i < 54; i++) inv.setItem(i, glass);

        List<java.util.UUID> testers = plugin.getBetaManager().getTesters();
        int slot = 10;

        for (java.util.UUID uuid : testers) {
            if (slot > 43) break;
            org.bukkit.OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            boolean online = op.isOnline();

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta  = head.getItemMeta();
            if (meta != null) {
                meta.setDisplayName((online ? ChatColor.GREEN : ChatColor.RED) + op.getName());
                meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Status: " + (online ? ChatColor.GREEN + "Online" : ChatColor.RED + "Offline"),
                    ChatColor.GRAY + "UUID: " + ChatColor.DARK_GRAY + uuid.toString().substring(0, 8) + "..."
                ));
                head.setItemMeta(meta);
            }
            inv.setItem(slot, head);
            slot++;
            if ((slot + 1) % 9 == 8) slot += 2; // skip border columns
        }

        // Back button
        inv.setItem(49, makeItem(
            Material.ARROW,
            ChatColor.YELLOW + "← Back",
            List.of(ChatColor.GRAY + "Return to Beta Panel")
        ));

        player.openInventory(inv);
    }

    // ── Helper ─────────────────────────────────────────────────────
    public static ItemStack makeItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta  meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
