package cz.helheim.duels.maps;

import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.utils.Cuboid;
import cz.helheim.duels.utils.FileUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;

public class LocalGameMap {

    private final String name;
    private final ArenaType arenaType;
    private final ArenaMode arenaMode;
    private final String builder;
    private Cuboid SPAWN_ONE;
    private Cuboid SPAWN_TWO;
    private Location specSpawn;
    private Cuboid PORTAL_ONE;
    private Cuboid PORTAL_TWO;
    private final ConfigurationSection section;
    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private World bukkitWorld;

    public LocalGameMap(File worldFolder, boolean loadOnInit, String name, ArenaType type, ArenaMode mode, String builder, ConfigurationSection section){
        this.name = name;
        this.builder = builder;
        this.arenaType = type;
        this.arenaMode = mode;
        this.sourceWorldFolder = new File(
                worldFolder,
                name
        );
        if(loadOnInit) load();

        this.section = section;
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
        if(bukkitWorld == null){
            System.out.println("DEBUG: Error world is null");
        }
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

    public Cuboid getSPAWN_ONE() {
        if(!isLoaded()) load();
        this.SPAWN_ONE = new Cuboid(MapManager.locationFromString(section.getStringList("SPAWN_ONE").get(0), getBukkitWorld()), MapManager.locationFromString(section.getStringList("SPAWN_ONE").get(1), getBukkitWorld()));
        return SPAWN_ONE;
    }

    public Cuboid getSPAWN_TWO() {
        if(!isLoaded()) load();
        this.SPAWN_TWO = new Cuboid(MapManager.locationFromString(section.getStringList("SPAWN_TWO").get(0), getBukkitWorld()), MapManager.locationFromString(section.getStringList("SPAWN_TWO").get(1), getBukkitWorld()));
        return SPAWN_TWO;
    }

    public Cuboid getPORTAL_ONE(){
        if(!getArenaType().equals(ArenaType.THE_BRIDGE)) return new Cuboid(new Location(bukkitWorld, 0,0,0), new Location(bukkitWorld, 2,2,2));

        if(!isLoaded()) load();
        this.PORTAL_ONE = new Cuboid(MapManager.locationFromString(section.getStringList("PORTAL_ONE").get(0), getBukkitWorld()), MapManager.locationFromString(section.getStringList("PORTAL_ONE").get(1), getBukkitWorld()));
        return PORTAL_ONE;
    }

    public Cuboid getPORTAL_TWO(){
        if(!getArenaType().equals(ArenaType.THE_BRIDGE)) return new Cuboid(new Location(bukkitWorld, 0,0,0), new Location(bukkitWorld, 2,2,2));

        if(!isLoaded()) load();
        this.PORTAL_TWO = new Cuboid(MapManager.locationFromString(section.getStringList("PORTAL_TWO").get(0), getBukkitWorld()), MapManager.locationFromString(section.getStringList("PORTAL_TWO").get(1), getBukkitWorld()));
        return PORTAL_TWO;
    }

    public ArenaType getArenaType() {
        return arenaType;
    }

    public Location getSpecSpawn() {
        this.specSpawn = MapManager.locationFromString(section.getString("SPECTATOR_SPAWN"), bukkitWorld);
        return specSpawn;
    }

    public ArenaMode getArenaMode() {
        return arenaMode;
    }
}
