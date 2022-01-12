package cz.helheim.duels.arena;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.arena.team.ArenaTeam;
import cz.helheim.duels.arena.team.TeamManager;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.items.KitItemManager;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.managers.ScoreboardManager;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.queue.Queue;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.task.CageOpenCountdown;
import cz.helheim.duels.task.EndingCountdownTask;
import cz.helheim.duels.task.PreGameCountdownTask;
import cz.helheim.duels.task.TotalTimeCountdownTask;
import cz.helheim.duels.utils.FileUtil;
import cz.helheim.duels.utils.MessageUtil;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Arena {

    private final String id;
    private final Set<Player> players;
    private final Set<Player> alivePlayers;
    private final List<Player> spectators;
    private final ArrayList<Block> placedBlocks;
    private final ArenaType arenaType;
    private final ArenaMode arenaMode;
    private final LocalGameMap map;
    private final ArenaTeam redTeam;
    private final ArenaTeam blueTeam;
    private final ArenaBase redBase;
    private final ArenaBase blueBase;
    private final List<ArenaTeam> teams;
    private final TeamManager teamManager;
    private final KitItemManager kitItemManager;
    private GameState state;
    private Game game;
    private PreGameCountdownTask preGameCountdownTask;
    private TotalTimeCountdownTask totalTimeCountdownTask;
    private CageOpenCountdown cageOpenCountdown;
    private EndingCountdownTask endingCountdownTask;
    private ArenaTeam winner;
    private ArenaTeam loser;
    private boolean isAvailable;
    private JPerPlayerScoreboard scoreboard;

    public Arena(String id, ArenaType arenaType, LocalGameMap map, ArenaMode mode) {
        this.map = map;
        this.id = id;
        players = new HashSet<>();
        alivePlayers = new HashSet<>();
        spectators = new ArrayList<>();
        placedBlocks = new ArrayList<>();
        redBase = new ArenaBase(map.getRedBase(), map.getRedSpawnPoint(), map.getRedRespawnPoint(), map.getRedPortal(), map.getRedCage(), map.getRedCageFloor());
        blueBase = new ArenaBase(map.getBlueBase(), map.getBlueSpawnPoint(), map.getBlueRespawnPoint(), map.getBluePortal(), map.getBlueCage(), map.getBlueCageFloor());
        redTeam = new ArenaTeam("Red", ChatColor.RED, mode, redBase);
        blueTeam = new ArenaTeam("Blue", ChatColor.BLUE, mode, blueBase);
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
        this.cageOpenCountdown = new CageOpenCountdown(this);
        this.endingCountdownTask = new EndingCountdownTask(this);
        this.game = new Game(this);
        teamManager = new TeamManager(this);
        kitItemManager = new KitItemManager(FileUtil.getKitYAML(), arenaType);
    }


    public void start() {
        setState(GameState.IN_GAME);
        totalTimeCountdownTask.begin();
        ArenaRegistry.getPlayableArenas(getArenaType(), getArenaMode()).remove(this);
        this.scoreboard = ScoreboardManager.getScoreboard(this, getArenaType(), getArenaMode());
        for (Player player : players) {
            alivePlayers.add(player);
            game.sendBeginningMessage(player);
            for (Player pl : players) {
                player.showPlayer(pl);
            }
            scoreboard.addPlayer(player);
        }

        for (Block block : blueBase.getPortal().blockList()){
            block.setType(Material.ENDER_PORTAL);
        }

        for (Block block : redBase.getPortal().blockList()){
            block.setType(Material.ENDER_PORTAL);
        }

        if(arenaType == ArenaType.THE_BRIDGE) {
            for (ArenaTeam team : getTeams()) {
                team.getBase().getCageFloor().reset();
            }
        }
    }

    public void reset() {
        setState(GameState.END);
        for (Player player : players) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                player.showPlayer(p);
            }
        }

        resetPlayers();
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

        if(cageOpenCountdown.isRunning){
            cageOpenCountdown.cancel();
        }

        if (endingCountdownTask.isRunning) {
            endingCountdownTask.cancel();
        }
        preGameCountdownTask = new PreGameCountdownTask(this);
        totalTimeCountdownTask = new TotalTimeCountdownTask(this);
        endingCountdownTask = new EndingCountdownTask(this);
        cageOpenCountdown = new CageOpenCountdown(this);
        alivePlayers.clear();
        game = new Game(this);
        this.isAvailable = true;
        if (scoreboard != null) {
            scoreboard.destroy();
        }
        ArenaRegistry.getPlayableArenas(getArenaType(), getArenaMode()).remove(this);
        ArenaRegistry.getActiveArenas(getArenaType(), getArenaMode()).remove(this);
    }

    public void sendMessage(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    public void sendTitle(String title, String subTitle){
        for(Player player : players){
            TitleAPI.sendTitle(player, 20, 20, 20, title, subTitle);
        }
    }

    public void addPlayer(Player player) {
        if (players.contains(player)) {
            player.sendMessage(ChatColor.RED + "You are already in the game");
            return;
        }
        players.add(player);
        if(player.getFireTicks() > 0){
            player.setFireTicks(0);
        }
        alivePlayers.add(player);
        teamManager.autoJoin(player);
        player.teleport(teamManager.getTeam(player).getBase().getSpawnPoint().getRandomLocation());
        player.setLevel(0);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        kitItemManager.addKitItems(player.getInventory());
        kitItemManager.suit(player);
        sendMessage(MessageUtil.getPrefix() + " §3" + player.getPlayer().getName() + " §7joined the game! §3(" + players.size() + ")");

        if(Queue.isPlayerInQueue(player)){
            Queue.getPlayerQueue(player).removePlayer(player);
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if(!players.contains(p)){
                player.hidePlayer(p);
            }
        }

        if (players.size() == 1) {
            setState(GameState.WAITING_FOR_OPPONENT);
        }
        if (players.size() == arenaMode.getMaxPlayers()) {
            game.start();
        }
    }

    public List<String> getOpponentsName(Player player){
       List<String> names = new ArrayList<>();
       for(Player p : getOpponents(player)){
           if(!alivePlayers.contains(p)){
               names.add(ChatColor.GRAY + p.getName());
           }else {
               names.add(p.getName());
           }
       }
       return names;
    }

    public List<Integer> getOpponentsHP(Player player){
        List<Integer> hps = new ArrayList<>();
        for(Player p : getOpponents(player)){
            hps.add((int) p.getHealth());
        }
        return hps;
    }

    public void resetPlayers(){
        for(Player player : players){
            teamManager.getTeam(player).removePlayer(player);
            player.setLevel(0);
            player.getInventory().clear();
            Game.teleportToLobby(player);
        }
    }

    public void removePlayer(Player player) {
        if (!players.contains(player)) {
            player.sendMessage(ChatColor.RED + "You are not in the game");
            return;
        }
        teamManager.getTeam(player).removePlayer(player);
        players.remove(player);
        player.setLevel(0);
        player.getInventory().clear();
        Game.teleportToLobby(player);

        if (players.size() == 0 && !getState().equals(GameState.END)) {
            reset();
        } else if (players.size() == arenaMode.getPlayersInTeam() && !getState().equals(GameState.WINNER_ANNOUNCE) && !getState().equals(GameState.END)) {
            for(Player p : players) {
                if (teamManager.isLastTeam(teamManager.getTeam(p))) {
                    setState(GameState.WINNER_ANNOUNCE);
                    return;
                }
            }
        }

    }

    public void killPlayer(Player player, Player killer, ArenaType mode) {
        if(player.getFireTicks() > 0){
            player.setFireTicks(0);
        }
        if (mode.equals(ArenaType.BUILD_UHC) || mode.equals(ArenaType.CLASSIC_DUELS)) {
            if(killer == null){
                alivePlayers.remove(player);
                teamManager.getTeam(player).getAlivePlayers().remove(player);
                player.getInventory().clear();
                addSpectator(player);
                TitleAPI.sendTitle(player, 30, 45, 30, ChatColor.RED + ChatColor.BOLD.toString() + "YOU DIED!");
                sendMessage("§3" + player.getName() + "§7 died");
                return;
            }
            alivePlayers.remove(player);
            teamManager.getTeam(player).getAlivePlayers().remove(player);
            player.getInventory().clear();
            addSpectator(player);
            TitleAPI.sendTitle(player, 30, 45, 30, ChatColor.RED + ChatColor.BOLD.toString() + "YOU DIED!");
            sendMessage("§3" + player.getName() + "§7 was killed by §3" + killer.getName());
            game.addKill(killer);

            if(teamManager.isLastTeam(teamManager.getTeam(killer))){
                setState(GameState.WINNER_ANNOUNCE);
            }
        }else if(mode.equals(ArenaType.THE_BRIDGE)){
            if(killer == null){
                player.getInventory().clear();
                kitItemManager.addKitItems(player.getInventory());
                sendMessage("§3" + player.getName() + "§7 died");
                player.teleport(teamManager.getTeam(player).getBase().getRespawnPoint().getRandomLocation());
                player.setHealth(20);
                return;
            }
            player.getInventory().clear();
            kitItemManager.addKitItems(player.getInventory());
            sendMessage("§3" + player.getName() + "§7 was killed by §3" + killer.getName());
            player.teleport(teamManager.getTeam(player).getBase().getRespawnPoint().getRandomLocation());
            game.addKill(killer);
            player.setHealth(20);

        }
    }

    public void addSpectator(Player player) {
        if (!spectators.contains(player)) {
            spectators.add(player);
            player.getPlayer().teleport(map.getSpecSpawn());
            player.sendMessage(MessageUtil.getPrefix() + " §7You are now spectating");
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
            ArenaRegistry.getPlayableArenas(this.getArenaType(), getArenaMode()).remove(this);
        }
        this.isAvailable = available;
    }

    public String  getID() {
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
            case WAITING_FOR_OPPONENT:
                setAvailable(true);
                break;
            case PREGAME:
            case IN_GAME:
            case END:
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
                if(arenaType.equals(ArenaType.BUILD_UHC) || arenaType.equals(ArenaType.CLASSIC_DUELS)) {
                    for (ArenaTeam team : getTeams()) {
                        if (teamManager.isLastTeam(team)) {
                            for (Player player : team.getMembers()) {
                                TitleAPI.sendTitle(player, 30, 45, 30, ChatColor.YELLOW + "Victory!");
                            }
                        }else{
                            for(Player player : team.getMembers()){
                                TitleAPI.sendTitle(player, 30, 45, 30, "§c§You Lost!");
                            }
                        }
                    }
                }else if(arenaType.equals(ArenaType.THE_BRIDGE)){
                    for(Player player : winner.getMembers()){
                        TitleAPI.sendTitle(player, 30, 45, 30, ChatColor.YELLOW + "Victory!");
                    }
                }
                //Bukkit.broadcastMessage(MessageUtil.getPrefix() + " §b" + winner.getName() + " §7won on arena §3" + map.getName() + "§7.");
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

    public ArenaTeam getWinner() {
        return winner;
    }

    public void setWinner(ArenaTeam winner) {
        this.winner = winner;
    }

    public ArenaTeam getLoser() {
        return loser;
    }

    public void setLoser(ArenaTeam loser) {
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

    public KitItemManager getKitItemManager() {
        return kitItemManager;
    }

    public ArenaBase getRedBase() {
        return redBase;
    }

    public ArenaBase getBlueBase() {
        return blueBase;
    }

    public CageOpenCountdown getCageOpenCountdown() {
        return cageOpenCountdown;
    }
}
