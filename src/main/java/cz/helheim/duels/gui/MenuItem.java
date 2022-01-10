package cz.helheim.duels.gui;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

public class MenuItem {

    private final String name;
    private final Material material;
    private final String command;
    private final int amount;
    private final int slot;

    public MenuItem(ConfigurationSection section){
        this.name = section.getString("name");
        this.material = Material.valueOf(section.getString("material"));
        this.amount = section.getInt("amount");
        this.slot = section.getInt("slot");
        this.command = section.getString("command");
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
}
