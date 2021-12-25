package cz.helheim.duels.arena;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.Duels;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.items.KitItemManager;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.managers.ScoreboardManager;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.task.EndingCountdownTask;
import cz.helheim.duels.task.PreGameCountdownTask;
import cz.helheim.duels.task.TotalTimeCountdownTask;
import cz.helheim.duels.utils.MessageUtil;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arena {

    private final int id;
    private final List<UUID> players;
    private final List<UUID> spectators;
    private final ArenaGameMode arenaGameMode;
    private GameState state;
    private final LocalGameMap map;
    private Game game;
    private PreGameCountdownTask preGameCountdownTask;
    private TotalTimeCountdownTask totalTimeCountdownTask;
    private EndingCountdownTask endingCountdownTask;
    private Player winner;
    private Player loser;
    private boolean isAvailable;
    private JPerPlayerScoreboard scoreboard;

    public Arena(int id, ArenaGameMode arenaGameMode) {
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
        this.endingCountdownTask = new EndingCountdownTask(this);
        this.isAvailable = true;
    }

    public void start() {
        setState(GameState.IN_GAME);
        totalTimeCountdownTask.begin();
        sendMessage(ChatColor.GREEN + "Game started!");
        this.scoreboard = ScoreboardManager.getBUHCScoreboard(this);
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            for (UUID uid : players) {
                player.showPlayer(Bukkit.getPlayer(uid));
            }
            scoreboard.addPlayer(player);
        }
    }

    public void reset() {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            player.teleport(ConfigManager.getLobbySpawn());
            System.out.println("DEBUG: " + player.getName());
            player.getInventory().clear();
            for (Player p : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(p);
            }
        }
        map.unload();
        state = GameState.IDLE;
        players.clear();
        spectators.clear();
        preGameCountdownTask = new PreGameCountdownTask(this);
        totalTimeCountdownTask = new TotalTimeCountdownTask(this);
        endingCountdownTask = new EndingCountdownTask(this);
        game = new Game(this);
        this.isAvailable = true;
        if(scoreboard != null) {
            scoreboard.destroy();
        }
        Game.getArenaManager().getPlayableArenas().remove(this);
        Game.getArenaManager().getActiveArenas().remove(this);

    }

    public LocalGameMap getMap() {
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
        player.getInventory().clear();
        KitItemManager.addKitItems(player.getInventory());
        KitItemManager.suit(player);
        for (Player p : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(p);
        }
        if (players.size() == 1) {
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

        if (players.size() == 0) {
            reset();
        } else if (players.size() == 1) {
            if (state == GameState.IN_GAME) {
                setState(GameState.WINNER_ANNOUNCE);
            }
        }

    }

    public void addSpectator(Player player) {
        if (!spectators.contains(player)) {
            spectators.add(player.getUniqueId());
            player.getPlayer().teleport(map.getSpecSpawn());
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
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
                for (UUID uuid : players) {
                    this.winner = Bukkit.getPlayer(uuid);
                    winner.sendMessage(MessageUtil.getPrefix() + "§7 You won the game!");
                    TitleAPI.sendTitle(winner, 30, 45, 30, ChatColor.YELLOW + "Victory!");
                }
                break;
            case END:
                setAvailable(false);
                break;

        }
    }

    public Game getGame() {
        return game;
    }

    public Player getOpponent(Player player) {
        if (players.indexOf(player.getUniqueId()) == 0) {
            return Bukkit.getPlayer(players.get(1));
        } else if (players.indexOf(player.getUniqueId()) == 1) {
            return Bukkit.getPlayer(players.get(0));
        }
        return null;
    }

    public PreGameCountdownTask getPreGameCountdownTask() {
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

    public TotalTimeCountdownTask getTotalTimeCountdownTask() {
        return this.totalTimeCountdownTask;
    }
}
