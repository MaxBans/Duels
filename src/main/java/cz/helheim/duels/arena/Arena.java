package cz.helheim.duels.arena;

import cz.helheim.duels.game.Game;
import cz.helheim.duels.managers.ScoreboardManager;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.task.PreGameCountdownTask;
import cz.helheim.duels.task.TotalTimeCountdownTask;
import cz.helheim.duels.utils.MessageUtil;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Arena {

    private final int id;
    private final List<UUID> players;
    private final List<UUID> spectators;
    private GameState state;
    private final ArenaGameMode arenaGameMode;
    private LocalGameMap map;
    private Game game;
    private PreGameCountdownTask preGameCountdownTask;
    private final TotalTimeCountdownTask totalTimeCountdownTask;
    private Player winner;
    private Player loser;
    private boolean isAvailable;
    private JPerPlayerScoreboard scoreboard;

    public Arena(int id, ArenaGameMode arenaGameMode){
        this.map = MapManager.getRandomBuildUHCMap();
        this.id = id;
        this.game = new Game(this);
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        this.winner = null;
        this.loser = null;
        state = GameState.IDLE;
       this.arenaGameMode = arenaGameMode;
       this.preGameCountdownTask = new PreGameCountdownTask(this);
       this.totalTimeCountdownTask = new TotalTimeCountdownTask(this);
       this.isAvailable = true;
    }

    public void start(){
        setState(GameState.IN_GAME);
        totalTimeCountdownTask.begin();
        sendMessage(ChatColor.GREEN + "Game started!");
        for(UUID uuid : players){
            Player player = Bukkit.getPlayer(uuid);
            this.scoreboard = ScoreboardManager.getBUHCScoreboard(this, player);
            ScoreboardManager.addToScoreboard(getScoreboard(), player);
        }
    }

    public void reset() {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
           player.teleport(ConfigManager.getLobbySpawn());
        }
        state = GameState.IDLE;
        map.unload();
        players.clear();
        preGameCountdownTask = new PreGameCountdownTask(this);
        game = new Game(this);
        this.isAvailable = true;

    }

    public LocalGameMap getMap(){
        return map;
    }

    public void sendMessage(String message) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            player.sendMessage(message);
        }
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        sendMessage(MessageUtil.getPrefix() + " §3" + player.getPlayer().getName() + " §7joined the game! §3(" + players.size() + ")");
        if(players.size() == 1) {
            player.getPlayer().teleport(map.getSPAWN_ONE());
            setState(GameState.WAITING_FOR_OPPONENT);
        }
        if (players.size() == 2) {
            player.getPlayer().teleport(map.getSPAWN_TWO());
            game.start();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        player.getPlayer().teleport(ConfigManager.getLobbySpawn());

        player.getPlayer().getInventory().clear();

        if(players.size() == 0){
            reset();
        } else if (players.size() == 1) {
            if (state == GameState.IN_GAME) {
                reset();
            }
        }

    }

    public void addSpectator(Player player){
        if(!spectators.contains(player)){
            spectators.add(player.getUniqueId());
            player.getPlayer().teleport(map.getSpecSpawn());
        }
    }

    public boolean isAvailable(){
        return isAvailable;
    }

    public void setAvailable(boolean available){
        this.isAvailable = available;
    }

    public int getID() {
        return id;
    }

    public List<UUID> getPlayers() {
        return players;
    }


    public GameState getState() {
        return state;
    }

    public Game getGame() {
        return game;
    }

    public void setState(GameState state) {
        this.state = state;
        switch (state){
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
                break;
            case END:
                setAvailable(false);
                break;

        }
    }

    public Player getOpponent(Player player){
        if(players.indexOf(player.getUniqueId()) == 0){
            return Bukkit.getPlayer(players.get(1));
        }else if(players.indexOf(player.getUniqueId()) == 1){
            return Bukkit.getPlayer(players.get(0));
        }
        return null;
    }

    public PreGameCountdownTask getPreGameCountdownTask(){
        return this.preGameCountdownTask;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }

    public ArenaGameMode getArenaGameMode() {
        return arenaGameMode;
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

    public TotalTimeCountdownTask getTotalTimeCountdownTask(){
        return this.totalTimeCountdownTask;
    }
}
