package cz.helheim.duels.maps;

import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.utils.FileUtil;
import cz.helheim.duels.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MapManager {
    private final List<LocalGameMap> maps = new ArrayList<>();

    public MapManager(FileConfiguration mapConfig, ArenaType type){
        ConfigurationSection mapSection = mapConfig.getConfigurationSection(type.getFormattedName() + ".maps");
        if(mapSection == null){
            Bukkit.getLogger().severe("Setup " + type.getFormattedName() + "'.maps' in maps.yml!");
        }

        for(String key : mapSection.getKeys(false)){
            ConfigurationSection section = mapSection.getConfigurationSection(key);
            if(section != null) {
                UUID uuid = UUID.randomUUID();
                String[] ids = uuid.toString().split("-");
                String id = ids[0];
                String name = section.getString("name");
                ArenaMode mode = ArenaMode.valueOf(section.getString("mode"));
                String builder = section.getString("builder");
                LocalGameMap map = new LocalGameMap(FileUtil.getGameMapsFolder(type), false, name, type, mode, builder, section, id);
                System.out.println(id);
                maps.add(map);
            }
        }
    }

    public LocalGameMap getRandomMap(ArenaMode mode){
        Random random = new Random();
        List<LocalGameMap> modeMaps = new ArrayList<>();
        for(LocalGameMap localGameMap : maps) {
            if(localGameMap.getArenaMode().equals(mode))
            modeMaps.add(localGameMap);
        }
        return modeMaps.get(random.nextInt(modeMaps.size()));
    }

    public static Location locationFromString(String string, World world){
        String[] loc = string.split(",");
        double x = Double.parseDouble(loc[0]);
        double y = Double.parseDouble(loc[1]);
        double z = Double.parseDouble(loc[2]);
        return new Location(world, x, y, z);
    }


}
