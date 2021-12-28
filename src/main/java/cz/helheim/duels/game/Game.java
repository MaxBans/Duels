package cz.helheim.duels.game;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Game {
    private Arena arena;
    private HashMap<UUID, Integer> kills;
    public static ArenaManager arenaManager = new ArenaManager();

    public Game(Arena arena){
        this.arena = arena;
        this.kills = new HashMap<>();
    }

    public void start(){
        arena.getPreGameCountdownTask().begin();
        arena.sendMessage(MessageUtil.getPrefix() + " §7 Game is starting!");
        arenaManager.getPlayableArenas(arena.getArenaGameMode()).remove(arena);
        for(UUID uuid : arena.getPlayers()){
            kills.put(uuid, 0);
        }
    }

    public static void autoJoin(Player player, ArenaGameMode mode) {
        if(ArenaManager.isPlaying(player)){
            ArenaManager.getArena(player).removePlayer(player);
        }

        if (arenaManager.getActiveArenas(mode).isEmpty()) {
            ArenaManager.createRandomArena(mode);
        }

        for (Arena arena : arenaManager.getPlayableArenas(mode)) {
            if (arena.getState().equals(GameState.WAITING_FOR_OPPONENT)) {
                arena.addPlayer(player);
                return;
            }
        }
        Random random = new Random();
        Arena arena = arenaManager.getPlayableArenas(mode).get(random.nextInt(arenaManager.getPlayableArenas(mode).size()));
        arena.addPlayer(player);

        }

        public void sendBeginningMessage(Player target){
            MessageUtil.sendCenteredMessage(target, ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
            MessageUtil.sendCenteredMessage(target, ChatColor.WHITE + ChatColor.BOLD.toString() + arena.getArenaGameMode().getFormattedName());
            MessageUtil.sendCenteredMessage(target, "§b§lEliminate your opponent!");
            MessageUtil.sendCenteredMessage(target, "§7§lOpponent: §b" + arena.getOpponent(target).getName());
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

        public static ArenaManager getArenaManager(){
        return arenaManager;
        }

    public void addKill(Player player){
        int currentKills = getKills().get(player.getUniqueId());
        kills.put(player.getUniqueId(), currentKills++);
    }
    public HashMap<UUID, Integer> getKills() {
        return kills;
    }
}

