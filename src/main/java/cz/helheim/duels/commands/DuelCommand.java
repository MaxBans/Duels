package cz.helheim.duels.commands;

import cz.helheim.duels.managers.InviteManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {
    private final InviteManager inviteManager = new InviteManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 0) {
            defaultMessage(player);
        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null){
                player.sendMessage(ChatColor.RED + "That player doesn't exist or is offline!");
                return false;
            }
            inviteManager.invite(player,target, ArenaGameMode.CLASSIC_DUELS);
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("accept")) {
                Player inviter = Bukkit.getPlayer(args[1]);
                ArenaGameMode mode = ArenaGameMode.getByName(args[2]);

                if (inviter == null) {
                    player.sendMessage(ChatColor.RED +  "That player doesn't exist or is offline!");
                    return false;
                }

                if (mode == null) {
                    player.sendMessage(ChatColor.RED + "That is not valid game mode");
                    player.sendMessage(ChatColor.RED + "Available modes:");
                    for (ArenaGameMode m : ArenaGameMode.values()) {
                        player.sendMessage(m.getFormattedName());
                    }
                    return false;
                }


                inviteManager.accept(player, inviter, mode);
                return true;
            } else if (args[0].equalsIgnoreCase("deny")) {
                Player inviter = Bukkit.getPlayer(args[1]);
                if (inviter == null) {
                    player.sendMessage(ChatColor.RED + "That player doesn't exist or is offline!");
                    return false;
                }
                inviteManager.deny(player, inviter);
                return true;
            } else {
                Player target = Bukkit.getPlayer(args[0]);
                ArenaGameMode mode = ArenaGameMode.getByName(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "That player doesn't exist or is offline!");
                    return false;
                }
                if(target.getName().equals(player.getName())){
                    player.sendMessage(ChatColor.RED + "You can't duel yourself");
                    return false;
                }

                if (mode == null) {
                    player.sendMessage(ChatColor.RED + "That is not valid game mode");
                    player.sendMessage(ChatColor.RED + "Available modes:");
                    for (ArenaGameMode m : ArenaGameMode.values()) {
                        player.sendMessage(m.getFormattedName());
                    }
                    return false;
                }
                inviteManager.invite(player, target, mode);
                return true;
            }
        }
        return false;
    }

    public void defaultMessage(Player player){
        MessageUtil.sendCenteredMessage(player.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
        player.sendMessage("§3/duel <player> <mode>§8 -§7 Sends duel invitation to player.");
        player.sendMessage("§3/duel accept <player>§8 -§7 Accepts duel from player.");
        player.sendMessage("§3/duel deny <player>§8 -§7 Denies duel from player.");
        MessageUtil.sendCenteredMessage(player.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
    }
}
