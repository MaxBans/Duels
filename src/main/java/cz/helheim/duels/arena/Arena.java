package cz.helheim.duels.arena;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.arena.team.ArenaTeam;
import cz.helheim.duels.arena.team.TeamManager;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.items.KitItemManager;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.managers.ScoreboardManager;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.task.EndingCountdownTask;
import cz.helheim.duels.task.PreGameCountdownTask;
import cz.helheim.duels.task.TotalTimeCountdownTask;
import cz.helheim.duels.utils.MessageUtil;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Arena {

    private final int id;
    private final Set<Player> players;
    private final Set<Player> alivePlayers;
    private final List<Player> spectators;
    private final ArrayList<Block> placedBlocks;
    private final ArenaType arenaType;
    private final ArenaMode arenaMode;
    private final LocalGameMap map;
    private final ArenaTeam redTeam;
    private final ArenaTeam blueTeam;
    private final List<ArenaTeam> teams;
    private final TeamManager teamManager;
    private GameState state;
    private Game game;
    private PreGameCountdownTask preGameCountdownTask;
    private TotalTimeCountdownTask totalTimeCountdownTask;
    private EndingCountdownTask endingCountdownTask;
    private Player winner;
    private Player loser;
    private boolean isAvailable;
    private JPerPlayerScoreboard scoreboard;

    public Arena(int id, ArenaType arenaType, LocalGameMap map, ArenaMode mode) {
        this.map = map;
        this.id = id;
        players = new HashSet<>();
        alivePlayers = new HashSet<>();
        spectators = new ArrayList<>();
        placedBlocks = new ArrayList<>();
        redTeam = new ArenaTeam("Red", ChatColor.RED, map.getSPAWN_ONE(), map.getPORTAL_ONE(), mode);
        blueTeam = new ArenaTeam("Blue", ChatColor.BLUE, map.getSPAWN_TWO(), map.getPORTAL_TWO(), mode);
        teams = new ArrayList<>();
        teams.add(redTeam);
        teams.add(blueTeam);
        this.winner = null;
        this.loser = null;
        arenaMode = mode;
        state = GameState.IDLE;
        this.arenaType = arenaType;
        this.isAvailable = true;

        this.preGameCountdownTask = new PreGameCountdownTask(this);
        this.totalTimeCountdownTask = new TotalTimeCountdownTask(this);
        this.endingCountdownTask = new EndingCountdownTask(this);
        this.game = new Game(this);
        teamManager = new TeamManager(this);
    }


    public void start() {
        setState(GameState.IN_GAME);
        totalTimeCountdownTask.begin();
        sendMessage(ChatColor.GREEN + "Game started!");
        this.scoreboard = ScoreboardManager.getScoreboard(this, getArenaType());
        for (Player player : players) {
            alivePlayers.add(player);
            game.sendBeginningMessage(player);
            for (Player pl : players) {
                player.showPlayer(pl);
            }
            scoreboard.addPlayer(player);
        }
    }

    public void reset() {
        for (Player player : players) {
            player.teleport(ConfigManager.getLobbySpawn());
            System.out.println("DEBUG: " + player.getName());
            player.getInventory().clear();
            for (Player p : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(p);
            }
        }
        map.unload();
        teams.clear();
        state = GameState.IDLE;
        players.clear();
        spectators.clear();
        if(preGameCountdownTask.isRunning) {
            preGameCountdownTask.cancel();
        }
        if(totalTimeCountdownTask.isRunning) {
            totalTimeCountdownTask.cancel();
        }
        if (endingCountdownTask.isRunning) {
            endingCountdownTask.cancel();
        }
        preGameCountdownTask = new PreGameCountdownTask(this);
        totalTimeCountdownTask = new TotalTimeCountdownTask(this);
        endingCountdownTask = new EndingCountdownTask(this);
        alivePlayers.clear();
        game = new Game(this);
        this.isAvailable = true;
        if (scoreboard != null) {
            scoreboard.destroy();
        }
        Game.getArenaManager().getPlayableArenas(getArenaType(), getArenaMode()).remove(this);
        Game.getArenaManager().getActiveArenas(getArenaType(), getArenaMode()).remove(this);

    }

    public void sendMessage(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public void addPlayer(Player player) {
        if (players.contains(player)) {
            player.sendMessage(ChatColor.RED + "You are already in the game");
            return;
        }
        players.add(player);
        alivePlayers.add(player);
        teamManager.autoJoin(player);
        player.teleport(teamManager.getTeam(player).getSpawn().getRandomLocation());
        player.setLevel(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        KitItemManager.addKitItems(player.getInventory());
        KitItemManager.suit(player);
        sendMessage(MessageUtil.getPrefix() + " §3" + player.getPlayer().getName() + " §7joined the game! §3(" + players.size() + ")");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if(!players.contains(p)){
                player.hidePlayer(p);
            }
        }

        player.sendMessage("DEBUG: "  + arenaMode.getName());
        player.sendMessage("DEBUG: " + arenaType.getFormattedName());
        //player.sendMessage("DEBUG: " + );

        if (players.size() == 1) {
            setState(GameState.WAITING_FOR_OPPONENT);
        }
        if (players.size() == arenaMode.getMaxPlayers()) {
            game.start();
        }
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) {
            player.sendMessage(ChatColor.RED + "You are not in the game");
            return;
        }
        teamManager.getTeam(player).removePlayer(player);
        players.remove(player);
        player.getInventory().clear();
        Game.teleportToLobby(player);

        if (players.size() == 0) {
            reset();
        } else if (players.size() == arenaMode.getPlayersInTeam()) {
            for(Player p : players) {
                if (teamManager.isLastTeam(teamManager.getTeam(p))) {
                    setState(GameState.WINNER_ANNOUNCE);
                    return;
                }
            }
        }

    }

    public void killPlayer(Player player, Player killer, ArenaType mode) {
        if (!mode.equals(ArenaType.THE_BRIDGE)) {
            if(killer == null){
                alivePlayers.remove(player);
                player.getInventory().clear();
                addSpectator(player);
                TitleAPI.sendTitle(player, 30, 45, 30, ChatColor.RED + ChatColor.BOLD.toString() + "YOU DIED!");
                sendMessage("§3" + player.getName() + "§7 died");
                return;
            }
            alivePlayers.remove(player);
            player.getInventory().clear();
            addSpectator(player);
            TitleAPI.sendTitle(player, 30, 45, 30, ChatColor.RED + ChatColor.BOLD.toString() + "YOU DIED!");
            sendMessage("§3" + player.getName() + "§7 was killed by §3" + killer.getName());
            game.addKill(killer);

            if(teamManager.isLastTeam(teamManager.getTeam(killer))){
                setState(GameState.WINNER_ANNOUNCE);
            }
        }
    }

    public void addSpectator(Player player) {
        if (!spectators.contains(player)) {
            spectators.add(player);
            player.getPlayer().teleport(map.getSpecSpawn());
            for (Player p : players) {
                p.hidePlayer(player);
            }

        }
    }

    public boolean isDead(Player player) {
        return !alivePlayers.contains(player);
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player);
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        if(!available){
            Game.getArenaManager().getPlayableArenas(this.getArenaType(), getArenaMode()).remove(this);
        }
        this.isAvailable = available;
    }

    public int getID() {
        return id;
    }

    public Set<Player> getPlayers() {
        return players;
    }


    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
        switch (state) {
            case IDLE:
                setAvailable(true);
                break;
            case WAITING_FOR_OPPONENT:
                setAvailable(true);
                break;
            case PREGAME:
                setAvailable(false);
                break;
            case IN_GAME:
                setAvailable(false);
                break;
            case WINNER_ANNOUNCE:
                setAvailable(false);
                endingCountdownTask.begin();
                for (Player player : players) {
                    scoreboard.removePlayer(player);
                    player.getInventory().clear();
                    ItemStack next = new ItemStack(Material.PAPER);
                    ItemMeta nMeta = next.getItemMeta();
                    nMeta.setDisplayName("§e§lFind new Arena");
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Multi-Click to find new game");
                    next.setItemMeta(nMeta);
                    nMeta.setLore(lore);
                    player.getInventory().setItem(4, next);
                }
                for (Player player : alivePlayers) {
                    this.winner = player;
                    TitleAPI.sendTitle(winner, 30, 45, 30, ChatColor.YELLOW + "Victory!");
                }
                Bukkit.broadcastMessage(MessageUtil.getPrefix() + " §b" + winner.getName() + " §7won on arena §3" + map.getName() + "§7.");
                break;
            case END:
                setAvailable(false);
                break;

        }
    }

    public Game getGame() {
        return game;
    }

    public List<Player> getOpponents(Player player) {
       if(teamManager.getTeam(player).equals(redTeam)){
           return blueTeam.getMembers();
       }else if(teamManager.getTeam(player).equals(blueTeam)){
           return redTeam.getMembers();
       }

       return null;
    }

    public PreGameCountdownTask getPreGameCountdownTask() {
        return this.preGameCountdownTask;
    }

    public LocalGameMap getMap() {
        return map;
    }


    public List<Player> getSpectators() {
        return spectators;
    }

    public ArenaType getArenaType() {
        return arenaType;
    }

    public Set<Player> getAlivePlayers() {
        return alivePlayers;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Player getLoser() {
        return loser;
    }

    public void setLoser(Player loser) {
        this.loser = loser;
    }

    public JPerPlayerScoreboard getScoreboard() {
        return scoreboard;
    }

    public TotalTimeCountdownTask getTotalTimeCountdownTask() {
        return this.totalTimeCountdownTask;
    }

    public ArrayList<Block> getPlacedBlocks() {
        return placedBlocks;
    }

    public ArenaMode getArenaMode() {
        return arenaMode;
    }

    public ArenaTeam getBlueTeam() {
        return blueTeam;
    }

    public ArenaTeam getRedTeam() {
        return redTeam;
    }

    public List<ArenaTeam> getTeams(){
        return teams;
    }

    public TeamManager getTeamManager(){
        return teamManager;
    }
}
