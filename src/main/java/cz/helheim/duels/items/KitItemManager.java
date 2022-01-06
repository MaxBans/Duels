package cz.helheim.duels.items;

import cz.helheim.duels.arena.ArenaType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class KitItemManager {

    private static final List<KitItem> kitItems = new ArrayList<>();
    private static final List<ArmorItem> armorItems = new ArrayList<>();

    public KitItemManager(FileConfiguration kitConfig, ArenaType type){
        ConfigurationSection kitSection = kitConfig.getConfigurationSection(type.getFormattedName());
        ConfigurationSection armorSection = kitConfig.getConfigurationSection(type.getFormattedName() + ".armorItems");
        if (kitSection == null) {
            Bukkit.getLogger().severe("Please setup 'kitItems' in config.yml");
        }
        if (armorSection == null) {
            Bukkit.getLogger().severe("Please setup 'armorItems' in config.yml");
        }

        for (String key : kitSection.getKeys(false)) {
            ConfigurationSection section = kitSection.getConfigurationSection(key);
            kitItems.add(new KitItem(section));
        }
        for (String key : armorSection.getKeys(false)) {
            ConfigurationSection section = armorSection.getConfigurationSection(key);
            armorItems.add(new ArmorItem(section));
        }
    }

    public void addKitItems(Inventory inventory){
        for(KitItem kitItem : kitItems){
            inventory.setItem(kitItem.getSlot(), kitItem.make());
        }
    }

    public void suit(Player player){
        for(ArmorItem armorItem : armorItems){
            armorItem.suitPlayer(player);
        }
    }

}
