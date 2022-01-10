package cz.helheim.duels.commands;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaRegistry;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.queue.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QueueCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if(!(commandSender instanceof Player)){
            commandSender.sendMessage("§cOnly players can execute this command!");
            return true;
        }

        Player gamePlayer = (Player) commandSender;
        if(ArenaRegistry.isInArena(gamePlayer)){
            gamePlayer.sendMessage("§cCannot queue while in a game!");
            return true;
        }


        if(args.length == 0){
            gamePlayer.sendMessage("§cPlease type a mode!");
            return true;
        }

        if(args.length == 1){
            if(args[0].equalsIgnoreCase("leave")){
                if(!Queue.isPlayerInQueue(gamePlayer)){
                    gamePlayer.sendMessage("§cYou are not in a Queue");
                    return false;
                }

                Queue playersQueue = Queue.getPlayerQueue(gamePlayer);
                playersQueue.removePlayer(gamePlayer);
                return true;
            }
            gamePlayer.sendMessage("§cPlease type an type");
            return true;
        }

        if(args.length == 2) {
            if(Queue.isPlayerInQueue(gamePlayer)){
                gamePlayer.sendMessage("§cYou are already in queue dummy!");
                return false;
            }

            ArenaMode mode = ArenaMode.getByName(args[0]);
            ArenaType type = ArenaType.getByName(args[1]);
            if (mode == null) {
                gamePlayer.sendMessage(ChatColor.RED + "Invalid mode.");
                gamePlayer.sendMessage(ChatColor.DARK_AQUA + "Use one of these:");
                for (ArenaMode t : ArenaMode.values()) {
                    gamePlayer.sendMessage(ChatColor.AQUA + " - " + ChatColor.DARK_GRAY + t.getName().replace(" ", ""));
                }
                return true;
            }

            if (type == null) {
                gamePlayer.sendMessage(ChatColor.RED + "Invalid arena type.");
                gamePlayer.sendMessage(ChatColor.DARK_AQUA + "Use one of these:");
                for (ArenaType t : ArenaType.values()) {
                    gamePlayer.sendMessage(ChatColor.AQUA + " - " + ChatColor.DARK_GRAY + t.getFormattedName());
                }
                return true;
            }

            /*/if(gamePlayer.isInParty()){
                if(gamePlayer != gamePlayer.getParty().getLeader()){
                    player.sendMessage("§cOnly the leader can queue you into games!");
                    return true;
                }
            }/*/

            Queue queue = Queue.getQueues(mode, type).get(0);
            queue.addPlayer(gamePlayer);
            return true;
        }
        return false;
    }

}
