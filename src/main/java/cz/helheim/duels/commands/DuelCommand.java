package cz.helheim.duels.commands;

import cz.helheim.duels.managers.InviteManager;
import cz.helheim.duels.modes.ArenaGameMode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {
    private InviteManager inviteManager = new InviteManager();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        //TEST COMMAND
        if(args.length == 1){
            Player target = Bukkit.getPlayer(args[0]);
            inviteManager.invite(player, target, ArenaGameMode.BUILD_UHC);
        }
        return false;
    }
}
