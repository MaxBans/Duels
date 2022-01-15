package cz.helheim.duels.listeners;

import cz.helheim.duels.gui.Menu;
import cz.helheim.duels.gui.MenuItem;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        Inventory inventory = e.getClickedInventory();
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        for(Menu menu : Menu.getMENUS()){
            if(!menu.getGui().getTitle().equals(inventory.getTitle())){
                return;
            }
            for(MenuItem menuItem : menu.getItems()){
                if(item.getItemMeta().getDisplayName().equals(menuItem.getName()) && item.hasItemMeta()){
                    if(menuItem.getCommand() != null) {
                        Bukkit.dispatchCommand((CommandSender) player, menuItem.getCommand());
                        player.closeInventory();
                        return;
                    }
                }
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        for(Menu menu : Menu.getMENUS()){
            if(menu.getGui() == e.getInventory()){
                menu.getUpdateTask().cancel();
            }
        }
    }

}
