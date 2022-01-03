package cz.helheim.duels.maps;

import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaType;
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
                ArenaMode mode = ArenaMode.valueOf(section.getString("mode"));
                String builder = section.getString("builder");
                LocalGameMap map = new LocalGameMap(FileUtil.getGameMapsFolder(ArenaType.BUILD_UHC), false, name, ArenaType.BUILD_UHC, mode, builder, section);
                buildUHCMaps.add(map);
            }
        }

        for(String key : classicSection.getKeys(false)){
            ConfigurationSection section = classicSection.getConfigurationSection(key);
            if(section != null) {
                String name = section.getString("name");
                ArenaMode mode = ArenaMode.valueOf(section.getString("mode"));
                String builder = section.getString("builder");
                LocalGameMap map = new LocalGameMap(FileUtil.getGameMapsFolder(ArenaType.CLASSIC_DUELS), false, name, ArenaType.CLASSIC_DUELS, mode, builder, section);
                getClassicDuelsMaps().add(map);
            }
        }

        for(String key : theBridgeSection.getKeys(false)){
            ConfigurationSection section = theBridgeSection.getConfigurationSection(key);
            if(section != null) {
                String name = section.getString("name");
                ArenaMode mode = ArenaMode.valueOf(section.getString("mode"));
                String builder = section.getString("builder");
                LocalGameMap map = new LocalGameMap(FileUtil.getGameMapsFolder(ArenaType.THE_BRIDGE), false, name, ArenaType.THE_BRIDGE, mode, builder, section);
                getBridgeMaps().add(map);
            }
        }
    }

    public static LocalGameMap getRandomMap(ArenaType type, ArenaMode mode){
        Random random = new Random();
        LocalGameMap map;
        List<LocalGameMap> maps = new ArrayList<>();
        switch (type){
            case BUILD_UHC:
                for(LocalGameMap localGameMap : getBuildUHCMaps()){
                    if(localGameMap.getArenaMode().equals(mode)){
                        maps.add(localGameMap);
                    }
                }
                System.out.println(maps.size());
                map = maps.get(random.nextInt(maps.size()));
                break;
            case CLASSIC_DUELS:
                for(LocalGameMap localGameMap : getClassicDuelsMaps()){
                    if(localGameMap.getArenaMode().equals(mode)){
                        maps.add(localGameMap);
                    }
                }
                map = maps.get(random.nextInt(maps.size() + 1));
                break;
            case THE_BRIDGE:
                for(LocalGameMap localGameMap : getBridgeMaps()){
                    if(localGameMap.getArenaMode().equals(mode)){
                        maps.add(localGameMap);
                    }
                }
                map = maps.get(random.nextInt(maps.size() + 1));
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
