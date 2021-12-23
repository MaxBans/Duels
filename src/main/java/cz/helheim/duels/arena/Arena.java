package cz.helheim.duels.arena;

import cz.helheim.duels.Duels;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.task.PreGameCountdownTask;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
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
    private PreGameCountdownTask countdown;

    public Arena(int id, ArenaGameMode arenaGameMode){
        this.id = id;
        this.game = new Game(this);
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        state = GameState.IDLE;
       this.arenaGameMode = arenaGameMode;
       this.map = MapManager.getRandomBuildUHCMap();
       this.countdown = new PreGameCountdownTask(this);
    }

    public void start(){
        setState(GameState.IN_GAME);
    }

    public void reset() {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).teleport(ConfigManager.getLobbySpawn());
        }
        state = GameState.IDLE;
        players.clear();
        countdown = new PreGameCountdownTask(this);
        game = new Game(this);

    }

    public void sendMessage(String message) {
        for (UUID uuid : players) {
            Bukkit.getPlayer(uuid).sendMessage(message);
        }
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        if(players.size() == 1) {
            player.teleport(map.getSPAWN_ONE());
            setState(GameState.WAITING_FOR_OPPONENT);
        }
        if (players.size() == 2) {
            player.teleport(map.getSPAWN_TWO());
            game.start();
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        player.teleport(ConfigManager.getLobbySpawn());

        player.getInventory().clear();

        if(players.size() == 0){
            reset();
        } else if (players.size() == 1) {
            if (state == GameState.IN_GAME) {
                reset();
            }
        }

    }

    public void addSpectator(Player player){
        if(!spectators.contains(player.getUniqueId())){
            spectators.add(player.getUniqueId());
            player.teleport(map.getSpecSpawn());
        }
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
    }

    public PreGameCountdownTask getCountdown(){
        return this.countdown;
    }

    public List<UUID> getSpectators() {
        return spectators;
    }
}
