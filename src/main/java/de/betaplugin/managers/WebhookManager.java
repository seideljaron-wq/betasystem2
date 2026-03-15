package de.betaplugin.managers;

import de.betaplugin.BetaPlugin;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class WebhookManager {

    private final BetaPlugin plugin;

    // ── Webhook URLs ───────────────────────────────────────────────
    private static final String WEBHOOK_URL =
        "https://discord.com/api/webhooks/1482801301506232442/sXixUCWxiRY8veXOXU7fED7oHul6DNZoQvonBKDD6bDjoA9RISKgkkNFfrH5jrdsBFZ2";
    // ──────────────────────────────────────────────────────────────

    public WebhookManager(BetaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Sends a Discord embed via webhook asynchronously.
     *
     * @param title       Embed title
     * @param description Embed description
     * @param color       Decimal color (e.g. 5793266 = #5865F2)
     * @param footer      Footer text
     */
    public void sendEmbed(String title, String description, int color, String footer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String timestamp = Instant.now().toString();
                String json = "{"
                    + "\"embeds\": [{"
                    + "\"title\": \"" + escape(title) + "\","
                    + "\"description\": \"" + escape(description) + "\","
                    + "\"color\": " + color + ","
                    + "\"footer\": {\"text\": \"" + escape(footer) + "\"},"
                    + "\"timestamp\": \"" + timestamp + "\""
                    + "}]"
                    + "}";

                URL url = new URL(WEBHOOK_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int code = conn.getResponseCode();
                if (code != 204 && code != 200) {
                    plugin.getLogger().warning("[Webhook] Response code: " + code);
                }
                conn.disconnect();

            } catch (Exception e) {
                plugin.getLogger().warning("[Webhook] Failed to send: " + e.getMessage());
            }
        });
    }

    // ── Pre-built embed senders ────────────────────────────────────

    public void logBetaAdminAction(String actor, String action, String target) {
        sendEmbed(
            "🛡️ Beta Admin Action",
            "**Actor:** " + actor + "\n**Action:** " + action + "\n**Target:** " + target,
            0xE67E22,  // orange
            "Beta Admin Log"
        );
    }

    public void logBetaTesterAction(String actor, String action, String target) {
        sendEmbed(
            "🧪 Beta Tester " + action,
            "**By:** " + actor + "\n**Player:** " + target,
            0x5865F2,  // blurple
            "Beta Tester Log"
        );
    }

    public void logBugReport(String player, String reason) {
        sendEmbed(
            "🐛 Bug Report",
            "**Reporter:** " + player + "\n**Description:**\n" + reason,
            0xE74C3C,  // red
            "Bug Report"
        );
    }

    public void logFeatureSuggestion(String player, String suggestion) {
        sendEmbed(
            "💡 Feature Suggestion",
            "**Player:** " + player + "\n**Suggestion:**\n" + suggestion,
            0x2ECC71,  // green
            "Feature Suggestion"
        );
    }

    public void logFeedback(String player, String feedback) {
        sendEmbed(
            "📝 Beta Feedback",
            "**Player:** " + player + "\n**Feedback:**\n" + feedback,
            0x3498DB,  // blue
            "Beta Feedback"
        );
    }

    public void logGUIOpen(String player) {
        sendEmbed(
            "📂 Beta GUI Opened",
            "**Player:** " + player + " opened the Beta GUI.",
            0x95A5A6,  // grey
            "GUI Log"
        );
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
