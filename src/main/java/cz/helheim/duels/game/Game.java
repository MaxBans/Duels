package cz.helheim.duels.game;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaRegistry;
import cz.helheim.duels.arena.team.ArenaTeam;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Game {
    private final Arena arena;
    private final HashMap<UUID, Integer> kills;
    private final HashMap<UUID, Integer> goals;

    private final HashMap<ArenaTeam, Integer> points;
    public static ArenaRegistry arenaManager;

    public Game(Arena arena){
        ArenaRegistry arenaManager = new ArenaRegistry();
        this.arena = arena;
        this.kills = new HashMap<>();
        this.points = new HashMap<>();
        this.goals = new HashMap<>();
    }

    public void start(){
        arena.getPreGameCountdownTask().begin();
        arena.sendMessage(MessageUtil.getPrefix() + " §7 Game is starting!");
        arenaManager.getPlayableArenas(arena.getArenaType(), arena.getArenaMode()).remove(arena);
        for(ArenaTeam team : arena.getTeams()){
            points.put(team, 0);
        }
        for(Player player : arena.getPlayers()){
            kills.put(player.getUniqueId(), 0);
            goals.put(player.getUniqueId(), 0);
        }
    }

    public static void autoJoin(Player player, ArenaType type, ArenaMode mode) {
        if(ArenaRegistry.isInArena(player)){
            ArenaRegistry.getArena(player).removePlayer(player);
        }
        if(!arenaManager.getPlayableArenas(type, mode).isEmpty()) {
            for (Arena arena : arenaManager.getPlayableArenas(type, mode)) {
                if (arena.getState().equals(GameState.WAITING_FOR_OPPONENT)) {
                    arena.addPlayer(player);
                    return;
                }
            }
        }else {
            Random random = new Random();
            Arena arena = arenaManager.getPlayableArenas(type, mode).get(random.nextInt(arenaManager.getPlayableArenas(type, mode).size() + 1));
            arena.addPlayer(player);
        }

        }

        public void sendBeginningMessage(Player target){
            MessageUtil.sendCenteredMessage(target, ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
            MessageUtil.sendCenteredMessage(target, ChatColor.WHITE + ChatColor.BOLD.toString() + arena.getArenaType().getFormattedName());
            MessageUtil.sendCenteredMessage(target, ChatColor.GREEN + ChatColor.BOLD.toString() + arena.getArenaMode().getName());
            MessageUtil.sendCenteredMessage(target, "§b§lEliminate your opponent!");
            MessageUtil.sendCenteredMessage(target, "§7§lOpponent(s): §b");
            for(Player opponent : arena.getOpponents(target))
            MessageUtil.sendCenteredMessage(target, "§f§l" + opponent.getName());
            target.sendMessage(" ");
            MessageUtil.sendCenteredMessage(target, "§e§lGood Luck!");
            MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
        }

        public static void teleportToLobby(Player player){
            player.teleport(ConfigManager.getLobbySpawn());
            player.getInventory().clear();
            clearArmor(player);
            for(Player pl : Bukkit.getOnlinePlayers()){
                player.showPlayer(pl);
            }
        }

    public static void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

        public static ArenaRegistry getArenaManager(){
        return arenaManager;
        }

    public void addKill(Player player){
        int currentKills = getKills().get(player.getUniqueId());
        kills.put(player.getUniqueId(), currentKills++);
    }
    public HashMap<UUID, Integer> getKills() {
        return kills;
    }

    public HashMap<ArenaTeam, Integer> getPoints() {
        return points;
    }

    public HashMap<UUID, Integer> getGoals() {
        return goals;
    }

}

