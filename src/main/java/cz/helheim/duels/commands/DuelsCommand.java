package cz.helheim.duels.commands;

import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.arena.ArenaType;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //TESTING COMMAND! WILL BE DELETED SOON
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can send duels!");
            return true;
        }
        Player player = (Player) sender;
        Game.autoJoin(player, ArenaType.getByName(args[0]), ArenaMode.getByName(args[1]));
        return true;
    }
}
