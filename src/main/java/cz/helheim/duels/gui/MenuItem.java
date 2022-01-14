package cz.helheim.duels.gui;

import cz.helheim.duels.utils.PlaceholderUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuItem {

    private final String name;
    private final Material material;
    private final String command;
    private final List<String> lore;
    private final int amount;
    private final int slot;

    public MenuItem(ConfigurationSection section){
        this.name = ChatColor.translateAlternateColorCodes('&', section.getString("name"));
        this.material =  Material.valueOf(section.getString("material"));
        List<String> translatedLore = new ArrayList<>();
        for(String key : PlaceholderUtil.replacePlaceholders(section.getStringList("lore"), PlaceholderUtil.getGlobalPlaceholders())){
           translatedLore.add(ChatColor.translateAlternateColorCodes('&', key));
        }
        this.lore = translatedLore;
        this.amount =  section.getInt("amount");
        this.slot =   section.getInt("slot");
        this.command = section.getString("command");
    }

    public ItemStack make(){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        item.setAmount(amount);
        return item;
    }

    public String getName() {
        return name;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public int getSlot() {
        return slot;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getLore() {
        return lore;
    }
}
