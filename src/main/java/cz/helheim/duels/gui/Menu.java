package cz.helheim.duels.gui;

import cz.helheim.duels.Duels;
import cz.helheim.duels.items.KitItem;
import cz.helheim.duels.utils.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Menu {

    private static final List<Menu> MENUS = new ArrayList<>();

    private final String name;
    private File file;
    private FileConfiguration configuration;
    private final String title;
    private final int size;
    private final String command;
    private final List<MenuItem> items;
    private final int updateInterval;
    private final Inventory gui;
    private final boolean designed;


    public Menu(String name){
        this.name = name;
        this.items = new ArrayList<>();
        if(FileUtil.getMenuFolder().listFiles() != null) {
            for (File file : FileUtil.getMenuFolder().listFiles()) {
                if(file.getName().equals(name + ".yml")) {
                    this.file = file;
                    configuration = YamlConfiguration.loadConfiguration(file);
                }
            }
        }
       // this.isDesigned = configuration.getBoolean("designed");
        this.title = ChatColor.translateAlternateColorCodes('&', configuration.getString("title"));
        this.size = configuration.getInt("size");
        this.command = configuration.getString("command");
        this.updateInterval = configuration.getInt("updateInterval");
        this.designed = configuration.getBoolean("designed");

        for (String key : configuration.getConfigurationSection("items").getKeys(false)) {
            ConfigurationSection section = configuration.getConfigurationSection("items." + key);
            items.add(new MenuItem(section));
        }



        this.gui = Bukkit.createInventory(null, size, title);
        if(designed){
            for(int i = 0; i < 9; i++){
                gui.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1,DyeColor.BLACK.getData()));
            }

            for(int i = size - 9; i < size; i++){
                gui.setItem(i, new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData()));
            }
        }

        for(MenuItem item : items){
            gui.setItem(item.getSlot(), item.make());
        }

        MENUS.add(this);
    }

    public static List<Menu> getMENUS() {
        return MENUS;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    private List<MenuItem> getItems(){
        return items;
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return size;
    }

    public String getCommand() {
        return command;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public Inventory getGui() {
        return gui;
    }

    public boolean isDesigned() {
        return designed;
    }
}
