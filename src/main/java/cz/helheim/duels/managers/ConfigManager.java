package cz.helheim.duels.managers;

import cz.helheim.duels.Duels;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class ConfigManager {

    public static Location getLobbySpawn(){
        World world = Bukkit.getWorld(Duels.getInstance().getConfig().getString("lobby.world"));
        int x = Duels.getInstance().getConfig().getInt("lobby.x");
        int y = Duels.getInstance().getConfig().getInt("lobby.y");
        int z = Duels.getInstance().getConfig().getInt("lobby.z");

        return new Location(world, x,y,z);

    }
}
