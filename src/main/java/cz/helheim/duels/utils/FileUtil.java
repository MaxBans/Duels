package cz.helheim.duels.utils;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.ArenaType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class FileUtil {

    private static File gameMapsFolder;
    private static File classicMapsFolder;
    private static File buildUHCFolder;
    private static File theBridgeFolder;
    private static File menuFolder;

    private static FileConfiguration mapsYAML;
    private static FileConfiguration kitYAML;
    private static Duels duels;

    public FileUtil(Duels duels){
        this.duels = duels;
    }

    public static void copy(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String[] files = source.list();
            if (files == null) return;
            for (String file : files) {
                File newSource = new File(source, file);
                File newDestination = new File(destination, file);
                copy(newSource, newDestination);
            }
        } else {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);

            byte[] buffer = new byte[1024];
            int length;

            //copy the file content in bytes

            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }

    public static void delete(File file) throws IOException {
        if (file.isDirectory()) {
            File[] entries = file.listFiles();
            if (entries != null) {
                for (File entry : entries) {
                    delete(entry);
                }
            }
        }
        if (!file.delete()) {
            throw new IOException("Failed to delete " + file);
        }
    }

    public void setupFiles(){
        duels.getDataFolder().mkdirs();
        gameMapsFolder = new File(duels.getDataFolder(), "gameMaps");
        classicMapsFolder = new File(gameMapsFolder, "ClassicDuels");
        buildUHCFolder = new File(gameMapsFolder, "BuildUHC");
        theBridgeFolder = new File(gameMapsFolder, "TheBridge");
        menuFolder = new File(Duels.getInstance().getDataFolder(), "Menus");

        if(!gameMapsFolder.exists()){
            gameMapsFolder.mkdirs();
        }

        if(!classicMapsFolder.exists()){
            classicMapsFolder.mkdirs();
        }

        if(!buildUHCFolder.exists()){
            buildUHCFolder.mkdirs();
        }

        if(!theBridgeFolder.exists()){
            theBridgeFolder.mkdirs();
        }

        if(!menuFolder.exists()){
            menuFolder.mkdirs();
        }

        getMapsYAML();
        getKitYAML();
    }

    public static File getMenuFolder(){
        return menuFolder;
    }

    public static File getGameMapsFolder(ArenaType mode){
        switch (mode){
            case BUILD_UHC:
                return buildUHCFolder;
            case CLASSIC_DUELS:
                return classicMapsFolder;
            case THE_BRIDGE:
                return theBridgeFolder;
        }
        return theBridgeFolder;
    }

    public static FileConfiguration getMapsYAML(){
        File file = new File(duels.getDataFolder(), "maps.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapsYAML = YamlConfiguration.loadConfiguration(file);
        return mapsYAML;
    }

    public static FileConfiguration getKitYAML(){
        File file = new File(duels.getDataFolder(), "kit.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        kitYAML = YamlConfiguration.loadConfiguration(file);
        return kitYAML;
    }


}
