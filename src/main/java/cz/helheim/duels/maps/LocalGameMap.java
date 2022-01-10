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
    private final String id;
    private final ArenaType arenaType;
    private final ArenaMode arenaMode;
    private final String builder;
    private Cuboid BLUE_SPAWN;
    private Cuboid RED_SPAWN;
    private Location specSpawn;
    private Cuboid BLUE_PORTAL;
    private Cuboid RED_PORTAL;
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

    public Cuboid getBLUE_SPAWN() {
        if(!isLoaded()) load();
        this.BLUE_SPAWN = new Cuboid(MapManager.locationFromString(section.getStringList("BLUE_SPAWN").get(0), getBukkitWorld()), MapManager.locationFromString(section.getStringList("BLUE_SPAWN").get(1), getBukkitWorld()));
        return BLUE_SPAWN;
    }

    public Cuboid getRED_SPAWN() {
        if(!isLoaded()) load();
        this.RED_SPAWN = new Cuboid(MapManager.locationFromString(section.getStringList("RED_SPAWN").get(0), getBukkitWorld()), MapManager.locationFromString(section.getStringList("RED_SPAWN").get(1), getBukkitWorld()));
        return RED_SPAWN;
    }

    public Cuboid getBLUE_PORTAL(){
        if(!getArenaType().equals(ArenaType.THE_BRIDGE)) return new Cuboid(new Location(bukkitWorld, 0,0,0), new Location(bukkitWorld, 2,2,2));

        if(!isLoaded()) load();
        this.BLUE_PORTAL = new Cuboid(MapManager.locationFromString(section.getStringList("BLUE_PORTAL").get(0), getBukkitWorld()), MapManager.locationFromString(section.getStringList("BLUE_PORTAL").get(1), getBukkitWorld()));
        return BLUE_PORTAL;
    }

    public Cuboid getRED_PORTAL(){
        if(!getArenaType().equals(ArenaType.THE_BRIDGE)) return new Cuboid(new Location(bukkitWorld, 0,0,0), new Location(bukkitWorld, 2,2,2));

        if(!isLoaded()) load();
        this.RED_PORTAL = new Cuboid(MapManager.locationFromString(section.getStringList("RED_PORTAL").get(0), getBukkitWorld()), MapManager.locationFromString(section.getStringList("RED_PORTAL").get(1), getBukkitWorld()));
        return RED_PORTAL;
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

    public String getId(){
        return id;
    }
}
