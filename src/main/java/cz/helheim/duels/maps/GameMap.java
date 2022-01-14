package cz.helheim.duels.maps;

import org.bukkit.World;

public interface GameMap {
    boolean load();
    void unload();
    boolean restoreFromSource();

    boolean isLoaded();
    World getBukkitWorld();
}
