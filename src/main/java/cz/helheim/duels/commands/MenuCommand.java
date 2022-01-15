package cz.helheim.duels.commands;

import cz.helheim.duels.gui.Menu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {

    /*/
    This is an example command. Will be deleted soon!
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            return false;
        }
        Player player = (Player) sender;

        if(args.length == 0){
            player.sendMessage(ChatColor.RED + "Please type menu name");
            return false;
        }
        String menuString = args[0];

        Menu menu = new Menu(menuString);
        player.openInventory(menu.getGui());
        return false;
    }
}
