package cz.helheim.duels.arena;

import cz.helheim.duels.Duels;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.player.GamePlayer;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.task.PreGameCountdownTask;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.chrono.HijrahChronology;
import java.util.*;

public class Arena {

    private final int id;
    private final List<GamePlayer> players;
    private final List<GamePlayer> spectators;
    private GameState state;
    private final ArenaGameMode arenaGameMode;
    private LocalGameMap map;
    private Game game;
    private PreGameCountdownTask countdown;
    private GamePlayer winner;
    private GamePlayer loser;
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
       this.countdown = new PreGameCountdownTask(this);
       this.isAvailable = true;
    }

    public void start(){
        setState(GameState.IN_GAME);
    }

    public void reset() {
        for (GamePlayer player : players) {
           player.getPlayer().teleport(ConfigManager.getLobbySpawn());
        }
        state = GameState.IDLE;
        map.unload();
        players.clear();
        countdown = new PreGameCountdownTask(this);
        game = new Game(this);
        this.isAvailable = true;

    }

    public void sendMessage(String message) {
        for (GamePlayer player : players) {
            player.getPlayer().sendMessage(message);
        }
    }

    public void addPlayer(GamePlayer player) {
        players.add(player);
        player.setArena(this);
        if(players.size() == 1) {
            player.getPlayer().teleport(map.getSPAWN_ONE());
            setState(GameState.WAITING_FOR_OPPONENT);
        }
        if (players.size() == 2) {
            player.getPlayer().teleport(map.getSPAWN_TWO());
            game.start();
        }
    }

    public void removePlayer(GamePlayer player) {
        players.remove(player);
        player.setArena(null);
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

    public void addSpectator(GamePlayer player){
        if(!spectators.contains(player)){
            spectators.add(player);
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

    public List<GamePlayer> getPlayers() {
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

    public PreGameCountdownTask getCountdown(){
        return this.countdown;
    }

    public List<GamePlayer> getSpectators() {
        return spectators;
    }

    public ArenaGameMode getArenaGameMode() {
        return arenaGameMode;
    }

    public GamePlayer getWinner() {
        return winner;
    }

    public void setWinner(GamePlayer winner) {
        this.winner = winner;
    }

    public GamePlayer getLoser() {
        return loser;
    }

    public void setLoser(GamePlayer loser) {
        this.loser = loser;
    }

    public JPerPlayerScoreboard getScoreboard(){
        List<String> list = Duels.getInstance().getConfig().getStringList("BuildUHC.Scoreboard");
        int mapIndex = list.indexOf("%map%");

    }
}
