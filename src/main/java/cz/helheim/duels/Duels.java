package cz.helheim.duels;

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

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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

