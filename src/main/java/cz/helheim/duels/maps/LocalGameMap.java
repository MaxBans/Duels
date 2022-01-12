package cz.helheim.duels.maps;

import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.managers.LocationManager;
import cz.helheim.duels.utils.Cuboid;
import cz.helheim.duels.utils.FileUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.io.IOException;

public class LocalGameMap {

    private final String name;
    private final String id;
    private final ArenaType arenaType;
    private final ArenaMode arenaMode;
    private final String builder;
    private final ConfigurationSection section;
    private final File sourceWorldFolder;
    private File activeWorldFolder;

    private World bukkitWorld;

    public LocalGameMap(File worldFolder, boolean loadOnInit, String name, ArenaType type, ArenaMode mode, String builder, ConfigurationSection section, String id){
        this.name = name;
        this.builder = builder;
        this.arenaType = type;
        this.arenaMode = mode;
        this.id = id;
        this.sourceWorldFolder = new File(
                worldFolder,
                name
        );
        if(loadOnInit) load();

        this.section = section;
    }

    public boolean load(){
        if(isLoaded()) return true;

        this.activeWorldFolder = new File(Bukkit.getWorldContainer().getParentFile(), sourceWorldFolder.getName() + "__" + arenaType.getFormattedName() + "__" + id);

        try{
            FileUtil.copy(sourceWorldFolder, activeWorldFolder);
        }catch (IOException e){
            Bukkit.getLogger().severe("Failed to load Map from source folder " + sourceWorldFolder.getName());
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

    public Cuboid getBlueSpawnPoint() {
        if(!isLoaded()) load();
        return LocationManager.getBlue("spawn", bukkitWorld, section);
    }

    public Cuboid getRedSpawnPoint() {
        if(!isLoaded()) load();
        return LocationManager.getRed("spawn", bukkitWorld, section);
    }

    public Cuboid getBlueCage(){
        if(!isLoaded()) load();
        return LocationManager.getBlue("cage", bukkitWorld, section);
    }

    public Cuboid getRedCage(){
        if(!isLoaded()) load();
        return LocationManager.getRed("cage", bukkitWorld, section);
    }

    public Cuboid getBlueCageFloor(){
        if(!isLoaded()) load();
        return LocationManager.getBlue("floor", bukkitWorld, section);
    }

    public Cuboid getRedCageFloor(){
        if(!isLoaded()) load();
        return LocationManager.getRed("floor", bukkitWorld, section);
    }

    public Cuboid getBlueRespawnPoint(){
        if(!isLoaded()) load();
        return LocationManager.getBlue("respawn", bukkitWorld, section);
    }

    public Cuboid getRedRespawnPoint(){
        if(!isLoaded()) load();
        return LocationManager.getRed("respawn", bukkitWorld, section);
    }

    public Cuboid getBluePortal(){
        if(!isLoaded()) load();
        return LocationManager.getBlue("portal", bukkitWorld, section);
    }

    public Cuboid getRedPortal(){
        if(!isLoaded()) load();
        return LocationManager.getRed("portal", bukkitWorld, section);
    }

    public Cuboid getBlueBase(){
        if(!isLoaded()) load();
        return LocationManager.getBlue("base", bukkitWorld, section);
    }

    public Cuboid getRedBase(){
        if(!isLoaded()) load();
        return LocationManager.getRed("base", bukkitWorld, section);
    }


    public ArenaType getArenaType() {
        return arenaType;
    }

    public Location getSpecSpawn() {
        return MapManager.locationFromString(section.getString("spectator_location"), bukkitWorld);
    }

    public ArenaMode getArenaMode() {
        return arenaMode;
    }

    public String getId(){
        return id;
    }
}
