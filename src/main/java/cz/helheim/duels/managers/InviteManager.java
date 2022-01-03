package cz.helheim.duels.managers;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaRegistry;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.utils.MessageUtil;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class InviteManager {

    private static HashMap<Player, Player> invites = new HashMap<>();

    private int task;


    public void invite(Player inviter, Player target, ArenaType type, ArenaMode mode){
        if(ArenaRegistry.isInArena(inviter)){
            inviter.sendMessage(ChatColor.RED + "You are already in a duel!");
            return;
        }

        if(inviter == target){
            inviter.sendMessage(ChatColor.RED + "You can't duel yourself dummy!");
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
        if(ArenaRegistry.isInArena(target)){
            inviter.sendMessage(ChatColor.RED + "That player is currently In Game");
            return;
        }
        invites.put(inviter, target);
        task = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Duels.getInstance(), new Runnable() {
            @Override
            public void run() {
               inviter.sendMessage(ChatColor.RED + "§8[§3Duels§8]§7 Duel invitation for player " + target.getPlayer().getName() + " has expired!");
               invites.remove(inviter);
            }
        }, 1200L);

        inviter.sendMessage("§8[§3Duels§8]§7 You invited §3" + target.getPlayer().getName() + "§7 to a duel! They have §360§7 seconds to accept!");
        MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
        MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.WHITE + ChatColor.BOLD.toString() + type.getFormattedName());
        MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.GREEN + ChatColor.BOLD.toString() + mode.getName());
        target.getPlayer().sendMessage(" ");
        MessageUtil.sendCenteredMessage(target.getPlayer(), "§7You were challenged to a duel!");
        MessageUtil.sendCenteredMessage(target.getPlayer(), "§7By: §b" + inviter.getName());
        target.getPlayer().sendMessage(" ");
        target.spigot().sendMessage(getAcceptMessage(inviter, type, mode), getDenyMessage(inviter));
        MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");

    }

    public void accept(Player accepter, Player inviter, ArenaType type, ArenaMode mode){
        if(!invites.containsValue(accepter)){
            accepter.sendMessage(ChatColor.RED + "That player didn't invite you");
            return;
        }

        inviter.sendMessage(MessageUtil.getPrefix() +  " §7Player " + accepter.getName() + " accepted your duel!");

        Arena arena = ArenaRegistry.createRandomArena(type, mode);
        invites.remove(inviter);
        Bukkit.getScheduler().cancelTask(task);
        arena.addPlayer(accepter);
        arena.addPlayer(inviter);
    }

    public void deny(Player denier, Player inviter){
        if(!invites.containsValue(denier)){
            denier.sendMessage(ChatColor.RED + "That player didn't invite you");
            return;
        }
        inviter.sendMessage(MessageUtil.getPrefix() +  " §7Player " + denier.getName() + " denied your duel!");
        invites.remove(inviter);
        Bukkit.getScheduler().cancelTask(task);
    }

    public static BaseComponent getAcceptMessage(Player sender, ArenaType type, ArenaMode mode){
        BaseComponent acceptmessage = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "                  [ACCEPT]");
        acceptmessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept!").create()));
        acceptmessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel accept " + sender.getName() + " " + type.toString() + " " + mode.getName()));
        return acceptmessage;
    }

    public static BaseComponent getDenyMessage(Player sender){
        BaseComponent denymessage = new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "       [DENY]");
        denymessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to deny!").create()));
        denymessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/duel deny " + sender.getName()));
        return denymessage;
    }

    public int getTask() {
        return task;
    }

    public HashMap<Player, Player> getInvites() {
        return invites;
    }
}
