package cz.helheim.duels;

import cz.helheim.duels.Listeners.GameListener;
import cz.helheim.duels.Listeners.JoinListener;
import cz.helheim.duels.Listeners.PlayerDeathListener;
import cz.helheim.duels.commands.DuelCommand;
import cz.helheim.duels.commands.DuelsCommand;
import cz.helheim.duels.items.KitItemManager;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Duels extends JavaPlugin {
    private static Duels instance;

    private static MapManager mapManager;
    private static KitItemManager kitItemManager;
    private FileUtil fileUtil;

    public Duels(){
        instance = this;
    }

    public static Duels getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().severe("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        Bukkit.getLogger().severe("Duels | Developed by: Maxbans9");
        Bukkit.getLogger().severe("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        getConfig();
        saveDefaultConfig();
        fileUtil = new FileUtil(this);
        fileUtil.setupFiles();
        mapManager = new MapManager(fileUtil.getMapsYAML());
        kitItemManager = new KitItemManager(fileUtil.getBuildUHCKitYAML());
        register();

    }

    @Override
    public void onDisable() {
        for(LocalGameMap map : MapManager.getBuildUHCMaps()){
            map.unload();
        }
    }

    public void register(){
        getCommand("autojoin").setExecutor(new DuelsCommand());
        getCommand("duel").setExecutor(new DuelCommand());
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    public MapManager getMapManager(){
        return mapManager;
    }
    public static KitItemManager getKitItemManager() { return kitItemManager; }

}

