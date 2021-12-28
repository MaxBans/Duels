package cz.helheim.duels.maps;

import cz.helheim.duels.Duels;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
    private static final List<LocalGameMap> buildUHCMaps = new ArrayList<>();
    private static final List<LocalGameMap> classicDuelsMaps = new ArrayList<>();
    private static final List<LocalGameMap> bridgeMaps = new ArrayList<>();

    public MapManager(FileConfiguration mapConfig){
        ConfigurationSection buhcSection = mapConfig.getConfigurationSection("BuildUHC.maps");
        ConfigurationSection classicSection = mapConfig.getConfigurationSection("ClassicDuels.maps");
        ConfigurationSection theBridgeSection = mapConfig.getConfigurationSection("TheBridge.maps");

        if(buhcSection == null){
            Bukkit.getLogger().severe("Setup 'BuildUHC.maps' in maps.yml!");
        }
        if(classicSection == null){
            Bukkit.getLogger().severe("Setup 'ClassicDuels.maps' in maps.yml!");
        }
        if(theBridgeSection == null){
            Bukkit.getLogger().severe("Setup 'TheBridge.maps' in maps.yml!");
        }

        for(String key : buhcSection.getKeys(false)){
            ConfigurationSection section = buhcSection.getConfigurationSection(key);
            if(section != null) {
                String name = section.getString("name");
                System.out.println(name);
                String builder = section.getString("builder");
                LocalGameMap map = new LocalGameMap(FileUtil.getGameMapsFolder(ArenaGameMode.BUILD_UHC), false, name, ArenaGameMode.BUILD_UHC, builder, section);
                buildUHCMaps.add(map);
            }
        }

        for(String key : classicSection.getKeys(false)){
            ConfigurationSection section = buhcSection.getConfigurationSection(key);
            if(section != null) {
                String name = section.getString("name");
                String builder = section.getString("builder");
                LocalGameMap map = new LocalGameMap(FileUtil.getGameMapsFolder(ArenaGameMode.CLASSIC_DUELS), false, name, ArenaGameMode.CLASSIC_DUELS, builder, section);
                getClassicDuelsMaps().add(map);
            }
        }

        for(String key : theBridgeSection.getKeys(false)){
            ConfigurationSection section = buhcSection.getConfigurationSection(key);
            if(section != null) {
                String name = section.getString("name");
                String builder = section.getString("builder");
                LocalGameMap map = new LocalGameMap(FileUtil.getGameMapsFolder(ArenaGameMode.THE_BRIDGE), false, name, ArenaGameMode.THE_BRIDGE, builder, section);
                getBridgeMaps().add(map);
            }
        }
    }

    public static LocalGameMap getRandomMap(ArenaGameMode mode){
        Random random = new Random();
        LocalGameMap map;
        switch (mode){
            case BUILD_UHC:
                System.out.println("DEBUG: " + getBuildUHCMaps().size());
                map = getBuildUHCMaps().get(random.nextInt(getBuildUHCMaps().size() + 1));
                break;
            case CLASSIC_DUELS:
                System.out.println("DEBUG: " + getClassicDuelsMaps().size());
                map = getClassicDuelsMaps().get(random.nextInt(getClassicDuelsMaps().size() + 1));
                break;
            case THE_BRIDGE:
                System.out.println("DEBUG: " + getBridgeMaps().size());
                map = getBridgeMaps().get(random.nextInt(getBridgeMaps().size() + 1));
                break;
            default:
                map = getClassicDuelsMaps().get(0);
                break;
        }
        if(!map.isLoaded()) map.load();
        return map;
    }

    public static Location locationFromString(String string, World world){
        String[] loc = string.split(",");
        double x = Double.parseDouble(loc[0]);
        double y = Double.parseDouble(loc[1]);
        double z = Double.parseDouble(loc[2]);
        return new Location(world, x, y, z);
    }

    public static List<LocalGameMap> getClassicDuelsMaps() {
        return classicDuelsMaps;
    }

    public static List<LocalGameMap> getBridgeMaps() {
        return bridgeMaps;
    }
    public static List<LocalGameMap> getBuildUHCMaps(){
        return buildUHCMaps;
    }

}
