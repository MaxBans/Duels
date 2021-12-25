package cz.helheim.duels;

import cz.helheim.duels.Listeners.GameListener;
import cz.helheim.duels.Listeners.JoinListener;
import cz.helheim.duels.commands.DuelCommand;
import cz.helheim.duels.commands.DuelsCommand;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.maps.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Duels extends JavaPlugin {
    private static Duels instance;

    File gameMapsFolder;
    private static MapManager mapManager;

    private FileConfiguration mapsYAML;

    public Duels(){
        instance = this;
    }

    public static Duels getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().severe("=-=-=-=-=--=-=-=-=");
        Bukkit.getLogger().severe("Duels | Developed by: Maxbans9");
        Bukkit.getLogger().severe("=-=-=-=-=--=-=-=-=");
        getConfig();
        saveDefaultConfig();
        setupFiles();
        mapManager = new MapManager(Duels.getInstance().getMapsYAML());
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
    }

    public void setupFiles(){
        getDataFolder().mkdirs();
        gameMapsFolder = new File(getDataFolder(), "gameMaps");
        if(!gameMapsFolder.exists()){
            gameMapsFolder.mkdirs();
        }
    }

    public File getGameMapsFolder(){
        return gameMapsFolder;
    }

    public FileConfiguration getMapsYAML(){
        File file = new File(this.getDataFolder(), "maps.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapsYAML = YamlConfiguration.loadConfiguration(file);
        return mapsYAML;
    }

    public MapManager getMapManager(){
        return mapManager;
    }
}

