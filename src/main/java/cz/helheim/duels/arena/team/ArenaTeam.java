package cz.helheim.duels.arena.team;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaBase;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.utils.Cuboid;
import cz.helheim.duels.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class ArenaTeam {

    private final String name;
    private final ChatColor color;
    private final List<Player> members;
    private final List<Player> alivePlayers;
    private final ArenaMode mode;
    private final ArenaBase base;

    public ArenaTeam(String name, ChatColor color, ArenaMode mode, ArenaBase base){
        this.name = name;
        this.color = color;
        this.members = new ArrayList<>();
        this.alivePlayers = new ArrayList<>();
        this.mode = mode;
        this.base = base;
    }

    public void addPlayer(Player player){
        if(isFull()){
            player.sendMessage(MessageUtil.getPrefix() + " This team is full!");
            return;
        }
        members.add(player);
        alivePlayers.add(player);
        player.setPlayerListName(color + player.getName());
        player.sendMessage(MessageUtil.getPrefix() + ChatColor.GRAY + " You joined team " + color + name);
    }

    public void removePlayer(Player player){
        members.remove(player);
        alivePlayers.remove(player);
        player.setPlayerListName(ChatColor.WHITE + player.getName());
    }

    public boolean isFull(){
        return members.size() >= mode.getPlayersInTeam();
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public List<Player> getMembers() {
        return members;
    }

    public ArenaMode getMode() {
        return mode;
    }

    public List<Player> getAlivePlayers(){
        return alivePlayers;
    }


    public ArenaBase getBase() {
        return base;
    }
}
