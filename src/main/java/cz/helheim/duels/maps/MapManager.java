package cz.helheim.duels.maps;

import cz.helheim.duels.Duels;
import cz.helheim.duels.modes.ArenaGameMode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
    private static List<LocalGameMap> buildUHCMaps = new ArrayList<>();

    public MapManager(FileConfiguration mapConfig){
        ConfigurationSection mapSection = mapConfig.getConfigurationSection("BuildUHC.maps");

        if(mapSection == null){
            Bukkit.getLogger().severe("Setup 'BuildUHC.maps' in maps.yml!");
        }

        for(String key : mapSection.getKeys(false)){
            ConfigurationSection section = mapSection.getConfigurationSection(key);
            if(section != null) {
                String name = section.getString("name");
                String mode = section.getString("mode");
                String builder = section.getString("builder");
                Location SPAWN_ONE = locationFromString(section.getString("SPAWN_ONE"), name);
                Location SPAWN_TWO = locationFromString(section.getString("SPAWN_TWO"), name);
                Location specSpawn = locationFromString(section.getString("SPECTATOR_SPAWN"), name);
                LocalGameMap map = new LocalGameMap(Duels.getInstance().getGameMapsFolder(), false, name, ArenaGameMode.valueOf(mode), builder, SPAWN_ONE, SPAWN_TWO, specSpawn);
                buildUHCMaps.add(map);
            }
        }
    }

    public static List<LocalGameMap> getBuildUHCMaps(){
        return buildUHCMaps;
    }

    private static Location locationFromString(String string, String world){
        String[] loc = string.split(",");
        double x = Double.parseDouble(loc[0]);
        double y = Double.parseDouble(loc[1]);
        double z = Double.parseDouble(loc[2]);
        Location location = new Location(Bukkit.getWorld(world), x, y, z);
        return location;
    }

    public static LocalGameMap getRandomBuildUHCMap(){
        Random random = new Random();
        LocalGameMap map = getBuildUHCMaps().get(random.nextInt(getBuildUHCMaps().size()));
        if(!map.isLoaded()) map.load();
        return map;
    }

}
