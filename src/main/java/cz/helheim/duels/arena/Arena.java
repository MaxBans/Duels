package cz.helheim.duels.arena;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.Duels;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.items.KitItemManager;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.managers.ScoreboardManager;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.modes.ArenaGameMode;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Arena {

    private final int id;
    private final List<UUID> players;
    private final List<UUID> alivePlayers;
    private final List<UUID> spectators;
    private final ArenaGameMode arenaGameMode;
    private final LocalGameMap map;
    private GameState state;
    private Game game;
    private PreGameCountdownTask preGameCountdownTask;
    private TotalTimeCountdownTask totalTimeCountdownTask;
    private EndingCountdownTask endingCountdownTask;
    private Player winner;
    private Player loser;
    private boolean isAvailable;
    private JPerPlayerScoreboard scoreboard;

    public Arena(int id, ArenaGameMode arenaGameMode, LocalGameMap map) {
        this.map = map;
        this.id = id;
        this.game = new Game(this);
        players = new ArrayList<>();
        spectators = new ArrayList<>();
        alivePlayers = new ArrayList<>();
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
            alivePlayers.add(player.getUniqueId());
            game.sendBeginningMessage(player);
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
        Game.getArenaManager().getPlayableArenas(getArenaGameMode()).remove(this);
        Game.getArenaManager().getActiveArenas(getArenaGameMode()).remove(this);

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
        if (players.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the game");
            return;
        }
        player.setLevel(0);
        player.setGameMode(GameMode.SURVIVAL);
        players.add(player.getUniqueId());
        alivePlayers.add(player.getUniqueId());
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
        if (!players.contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not in the game");
            return;
        }
        players.remove(player.getUniqueId());
        player.getInventory().clear();
        Game.teleportToLobby(player);

        if (players.size() == 0) {
            reset();
        } else if (players.size() == 1) {
            if (state == GameState.IN_GAME) {
                setState(GameState.WINNER_ANNOUNCE);
            }
        }

    }

    public void killPlayer(Player player, Player killer, ArenaGameMode mode) {
        if (mode.equals(ArenaGameMode.BUILD_UHC)) {
            if(killer == null){
                alivePlayers.remove(player.getUniqueId());
                player.getInventory().clear();
                addSpectator(player);
                TitleAPI.sendTitle(player, 30, 45, 30, ChatColor.RED + ChatColor.BOLD.toString() + "YOU DIED!");
                sendMessage("§3" + player.getName() + "§7 died");
                setState(GameState.WINNER_ANNOUNCE);
                return;
            }
            alivePlayers.remove(player.getUniqueId());
            player.getInventory().clear();
            addSpectator(player);
            TitleAPI.sendTitle(player, 30, 45, 30, ChatColor.RED + ChatColor.BOLD.toString() + "YOU DIED!");
            sendMessage("§3" + player.getName() + "§7 was killed by §3" + killer.getName());
            setState(GameState.WINNER_ANNOUNCE);
            game.addKill(killer);
        }
    }

    public void addSpectator(Player player) {
        if (!spectators.contains(player.getUniqueId())) {
            spectators.add(player.getUniqueId());
            player.getPlayer().teleport(map.getSpecSpawn());
            for (UUID uuid : players) {
                Player p = Bukkit.getPlayer(uuid);
                p.hidePlayer(player);
            }

        }
    }

    public boolean isDead(Player player) {
        return !alivePlayers.contains(player.getUniqueId());
    }

    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        if(!available){
            Game.getArenaManager().getActiveArenas(this.getArenaGameMode()).remove(this);
        }
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
                for (UUID id : players) {
                    Player player = Bukkit.getPlayer(id);
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
                for (UUID uuid : alivePlayers) {
                    this.winner = Bukkit.getPlayer(uuid);
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

    public List<UUID> getAlivePlayers() {
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
}
