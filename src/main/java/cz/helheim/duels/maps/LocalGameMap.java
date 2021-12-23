package cz.helheim.duels.maps;

import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.utils.FileUtil;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;

public class LocalGameMap {

    private String name;
    private ArenaGameMode mode;
    private String builder;
    private Location SPAWN_ONE;
    private Location SPAWN_TWO;
    private Location specSpawn;

    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private World bukkitWorld;

    public LocalGameMap(File worldFolder, boolean loadOnInit, String name, ArenaGameMode mode, String builder, Location spawnOne, Location spawnTwo, Location specSpawn){
        this.name = name;
        this.builder = builder;
        this.SPAWN_ONE = spawnOne;
        this.SPAWN_TWO = spawnTwo;
        this.mode = mode;
        this.specSpawn = specSpawn;
        this.sourceWorldFolder = new File(
                worldFolder,
                name
        );

        if(loadOnInit) load();
    }

    public boolean load(){
        if(isLoaded()) return true;

        this.activeWorldFolder = new File(Bukkit.getWorldContainer().getParentFile(), sourceWorldFolder.getName() + "_active_" + System.currentTimeMillis());

        try{
            FileUtil.copy(sourceWorldFolder, activeWorldFolder);
        }catch (IOException e){
            Bukkit.getLogger().severe("Failed to laod Map from source folder " + sourceWorldFolder.getName());
            e.printStackTrace();
            return false;
        }

        this.bukkitWorld = Bukkit.createWorld(new WorldCreator(activeWorldFolder.getName()));

        if(bukkitWorld != null) {
            this.bukkitWorld.setAutoSave(false);
            this.bukkitWorld.setDifficulty(Difficulty.PEACEFUL);
            this.bukkitWorld.setAnimalSpawnLimit(0);
        }
        return isLoaded();
    }

    public void unload(){
        if(bukkitWorld != null) Bukkit.unloadWorld(bukkitWorld, false);
        if(activeWorldFolder != null){
            try {
                FileUtil.delete(activeWorldFolder);
                System.out.println(activeWorldFolder.getName() + " Was succesfully reset!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        bukkitWorld = null;
        activeWorldFolder = null;
    }

    public boolean restoreFromSource(){
        unload();
        return load();
    }
    public boolean isLoaded(){
        return this.bukkitWorld != null;
    }

    public String getName() {
        return name;
    }

    public String getBuilder() {
        return builder;
    }

    public World getBukkitWorld(){
        return bukkitWorld;
    }

    public Location getSPAWN_ONE() {
        return SPAWN_ONE;
    }

    public Location getSPAWN_TWO() {
        return SPAWN_TWO;
    }

    public ArenaGameMode getMode() {
        return mode;
    }

    public Location getSpecSpawn() {
        return specSpawn;
    }
}
