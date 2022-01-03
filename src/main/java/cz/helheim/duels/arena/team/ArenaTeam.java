package cz.helheim.duels.arena.team;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
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
    private final Cuboid spawn;
    private final Cuboid portal;
    private final ArenaMode mode;

    public ArenaTeam(String name, ChatColor color, Cuboid spawn, Cuboid portal, ArenaMode mode){
        this.name = name;
        this.color = color;
        this.members = new ArrayList<>();
        this.alivePlayers = new ArrayList<>();
        this.spawn = spawn;
        this.portal = portal;
        this.mode = mode;
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

    public Cuboid getSpawn() {
        return spawn;
    }

    public Cuboid getPortal() {
        return portal;
    }

    public ArenaMode getMode() {
        return mode;
    }

    public List<Player> getAlivePlayers(){
        return alivePlayers;
    }

}
