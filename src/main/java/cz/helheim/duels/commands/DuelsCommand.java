package cz.helheim.duels.commands;

import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.player.GamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can send duels!");
            return true;
        }
        Player player = (Player) sender;
        GamePlayer gamePlayer = new GamePlayer(player);

        if (gamePlayer.isPlaying()) {
            player.sendMessage("§cYou can't send duels while in-game!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cMissing player!");
            return true;
        }

        if (args.length == 1) {
            if (!args[0].equalsIgnoreCase("accept")) {
                player.sendMessage("§cMissing duel mode!");
                return true;
            } else {

            }
        }
        return false;
    }
}
