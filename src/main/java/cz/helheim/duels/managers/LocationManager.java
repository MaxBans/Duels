package cz.helheim.duels.managers;

import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public class LocationManager {

    public static Cuboid getBlue(String type, World world, ConfigurationSection section){
        return new Cuboid(MapManager.locationFromString(section.getString("blue." + type + ".loc1"), world), MapManager.locationFromString(section.getString("blue." + type + ".loc2"), world));
    }

    public static Cuboid getRed(String type, World world, ConfigurationSection section){
        return new Cuboid(MapManager.locationFromString(section.getString("red." + type + ".loc1"), world), MapManager.locationFromString(section.getString("red." + type + ".loc2"), world));
    }
}
