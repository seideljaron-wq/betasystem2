package de.betaplugin;

import de.betaplugin.commands.BetaAdminCommand;
import de.betaplugin.commands.BetaCommand;
import de.betaplugin.commands.BetaTesterCommand;
import de.betaplugin.commands.BugCommand;
import de.betaplugin.listeners.GUIListener;
import de.betaplugin.managers.BetaManager;
import de.betaplugin.managers.WebhookManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BetaPlugin extends JavaPlugin {

    private static BetaPlugin instance;
    private BetaManager betaManager;
    private WebhookManager webhookManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.webhookManager = new WebhookManager(this);
        this.betaManager    = new BetaManager(this);

        // Commands
        getCommand("beta-admin").setExecutor(new BetaAdminCommand(this));
        getCommand("beta-tester").setExecutor(new BetaTesterCommand(this));
        getCommand("beta").setExecutor(new BetaCommand(this));
        getCommand("bug").setExecutor(new BugCommand(this));

        // Listeners
        getServer().getPluginManager().registerEvents(new GUIListener(this), this);

        getLogger().info("BetaPlugin enabled!");
    }

    @Override
    public void onDisable() {
        if (betaManager != null) betaManager.save();
        getLogger().info("BetaPlugin disabled.");
    }

    public static BetaPlugin getInstance() { return instance; }
    public BetaManager getBetaManager()     { return betaManager; }
    public WebhookManager getWebhookManager() { return webhookManager; }
}
