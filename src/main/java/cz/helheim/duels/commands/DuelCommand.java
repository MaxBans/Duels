package cz.helheim.duels.commands;

import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.team.ArenaTeam;
import cz.helheim.duels.managers.InviteManager;
import cz.helheim.duels.arena.ArenaType;
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
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                defaultMessage(player);
            } else if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Invalid usage!");
            } else if (args.length == 2) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "That player is offline");
                    return false;
                }
                inviteManager.deny(player, target);
            } else if (args.length == 4 && args[0].equalsIgnoreCase("accept")) {
                Player target = Bukkit.getPlayer(args[1]);
                ArenaType type = ArenaType.getByName(args[2]);
                ArenaMode mode = ArenaMode.getByName(args[3]);
                if (target == null) {
                    player.sendMessage(ChatColor.RED + "That player is offline");
                    return false;
                }
                if (type == null) {
                    player.sendMessage(ChatColor.RED + "Invalid arena type.");
                    player.sendMessage(ChatColor.DARK_AQUA + "Use one of these:");
                    for (ArenaType t : ArenaType.values()) {
                        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.DARK_GRAY + t.getFormattedName());
                    }

                    return false;
                }
                if (mode == null) {
                    player.sendMessage(ChatColor.RED + "Invalid mode.");
                    player.sendMessage(ChatColor.DARK_AQUA + "Use one of these:");
                    for (ArenaMode t : ArenaMode.values()) {
                        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.DARK_GRAY + t.getName());
                    }

                    return false;
                }

                inviteManager.accept(player, target, type, mode);
            } else if (args.length == 3) {

                Player target = Bukkit.getPlayer(args[0]);
                ArenaType type = ArenaType.getByName(args[1]);
                ArenaMode mode = ArenaMode.getByName(args[2]);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "That player is offline");
                    return false;
                }
                if (type == null) {
                    player.sendMessage(ChatColor.RED + "Invalid arena type.");
                    player.sendMessage(ChatColor.DARK_AQUA + "Use one of these:");
                    for (ArenaType t : ArenaType.values()) {
                        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.DARK_GRAY + t.getFormattedName());
                    }

                    return false;
                }
                if (mode == null) {
                    player.sendMessage(ChatColor.RED + "Invalid mode.");
                    player.sendMessage(ChatColor.DARK_AQUA + "Use one of these:");
                    for (ArenaMode t : ArenaMode.values()) {
                        player.sendMessage(ChatColor.AQUA + " - " + ChatColor.DARK_GRAY + t.getName());
                    }

                    return false;
                }
                inviteManager.invite(player, target, type, mode);
            }

        }
        return true;
    }


    public void defaultMessage(Player player) {
        MessageUtil.sendCenteredMessage(player.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
        player.sendMessage("§3/duel <player> <type> <mode>§8 -§7 Sends duel invitation to player.");
        player.sendMessage("§3/duel accept <player> <type> <mode>§8 -§7 Accepts duel from player.");
        player.sendMessage("§3/duel deny <player>§8 -§7 Denies duel from player.");
        MessageUtil.sendCenteredMessage(player.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
    }
}
