package cz.helheim.duels.managers;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import sun.java2d.HeadlessGraphicsEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InviteManager {

    private HashMap<Player, Player> invites = new HashMap<>();
    private ArenaGameMode inviteGameMode;
    private int task;


    public void invite(Player inviter, Player target, ArenaGameMode mode){
        if(ArenaManager.isPlaying(inviter)){
            inviter.sendMessage(ChatColor.RED + "You are already in a duel!");
            return;
        }
        if(invites.containsValue(target)){
            inviter.sendMessage(ChatColor.RED + "You already invited this player!");
            return;
        }
        if(invites.containsKey(inviter)){
            inviter.sendMessage(ChatColor.RED + "You can only invite 1 player!");
            return;
        }
        if(ArenaManager.isPlaying(target)){
            inviter.sendMessage(ChatColor.RED + "That player is currently In Game");
            return;
        }
        this.inviteGameMode = mode;
        invites.put(inviter, target);
        task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Duels.getInstance(), new Runnable() {
            @Override
            public void run() {
               inviter.sendMessage(ChatColor.RED + "§8[§3Duels§8]§7 Duel invitation for player " + target.getPlayer().getName() + " has expired!");
               invites.remove(inviter);
            }
        }, 1200L);

        inviter.sendMessage("§8[§3Duels§8]§7 You invited §3" + target.getPlayer().getName() + "§7 to a duel! They have §360 seconds to accept!");
        MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
        MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.WHITE + ChatColor.BOLD.toString() + mode.getFormattedName());
        target.getPlayer().sendMessage(" ");
        MessageUtil.sendCenteredMessage(target.getPlayer(), "§7You were challenged to a duel!");
        MessageUtil.sendCenteredMessage(target.getPlayer(), "§7By: §b" + inviter.getName());
        target.getPlayer().sendMessage(" ");
        MessageUtil.sendCenteredMessage(target.getPlayer(), "§a§l[ACCEPT]    §c§l[DECLINE]");
        MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");

    }

    public int getTask() {
        return task;
    }

    public ArenaGameMode getInviteGameMode() {
        return inviteGameMode;
    }

    public HashMap<Player, Player> getInvites() {
        return invites;
    }
}
