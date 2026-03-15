package de.betaplugin.managers;

import de.betaplugin.BetaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BetaManager {

    private final BetaPlugin plugin;
    private final File dataFile;
    private FileConfiguration data;

    private List<UUID> betaAdmins  = new ArrayList<>();
    private List<UUID> betaTesters = new ArrayList<>();

    public BetaManager(BetaPlugin plugin) {
        this.plugin   = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        load();
    }

    private void load() {
        if (!dataFile.exists()) {
            try { dataFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);

        List<String> admins  = data.getStringList("beta-admins");
        List<String> testers = data.getStringList("beta-testers");

        admins.forEach(s  -> betaAdmins.add(UUID.fromString(s)));
        testers.forEach(s -> betaTesters.add(UUID.fromString(s)));
    }

    public void save() {
        List<String> admins  = new ArrayList<>();
        List<String> testers = new ArrayList<>();
        betaAdmins.forEach(u  -> admins.add(u.toString()));
        betaTesters.forEach(u -> testers.add(u.toString()));
        data.set("beta-admins",   admins);
        data.set("beta-testers",  testers);
        try { data.save(dataFile); } catch (IOException e) { e.printStackTrace(); }
    }

    // ── Beta Admins ────────────────────────────────────────────────
    public boolean isAdmin(UUID uuid)  { return betaAdmins.contains(uuid); }
    public void addAdmin(UUID uuid)    { if (!betaAdmins.contains(uuid))  { betaAdmins.add(uuid);   save(); } }
    public void removeAdmin(UUID uuid) { betaAdmins.remove(uuid); save(); }
    public List<UUID> getAdmins()      { return new ArrayList<>(betaAdmins); }

    // ── Beta Testers ───────────────────────────────────────────────
    public boolean isTester(UUID uuid)  { return betaTesters.contains(uuid); }
    public void addTester(UUID uuid)    { if (!betaTesters.contains(uuid))  { betaTesters.add(uuid);   save(); } }
    public void removeTester(UUID uuid) { betaTesters.remove(uuid); save(); }
    public List<UUID> getTesters()      { return new ArrayList<>(betaTesters); }
}
