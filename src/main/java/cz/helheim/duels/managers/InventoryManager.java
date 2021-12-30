package cz.helheim.duels.managers;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {

    public static int getAmountOf(Material material, Inventory inventory){
        int amount = 0;
        for(ItemStack is : inventory.getContents()){
            if(is.getType().equals(material)){
                amount++;
            }
        }
        return amount;
    }
}
