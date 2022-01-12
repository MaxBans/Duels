package cz.helheim.duels.game;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaRegistry;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.arena.team.ArenaTeam;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Game {
    private final Arena arena;
    private final HashMap<UUID, Integer> kills;
    private final HashMap<UUID, Integer> goals;

    private final HashMap<ArenaTeam, Integer> points;

    private int timeLeft = 8;
    private BukkitTask cageTask;

    public Game(Arena arena) {
        this.arena = arena;
        this.kills = new HashMap<>();
        this.points = new HashMap<>();
        this.goals = new HashMap<>();
    }

    public static void autoJoin(Player player, ArenaType type, ArenaMode mode) {
        if (ArenaRegistry.isInArena(player)) {
            ArenaRegistry.getArena(player).removePlayer(player);
        }
        if (!ArenaRegistry.getPlayableArenas(type, mode).isEmpty()) {
            for (Arena arena : ArenaRegistry.getPlayableArenas(type, mode)) {
                if (arena.getState().equals(GameState.WAITING_FOR_OPPONENT)) {
                    arena.addPlayer(player);
                    return;
                }
            }
        } else {
            Random random = new Random();
            Arena arena = ArenaRegistry.getPlayableArenas(type, mode).get(random.nextInt(ArenaRegistry.getPlayableArenas(type, mode).size() + 1));
            arena.addPlayer(player);
        }

    }

    public static void teleportToLobby(Player player) {
        player.teleport(ConfigManager.getLobbySpawn());
        player.getInventory().clear();
        clearArmor(player);
        for (Player pl : Bukkit.getOnlinePlayers()) {
            player.showPlayer(pl);
        }
    }

    public static void clearArmor(Player player) {
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public void start() {
        arena.getPreGameCountdownTask().begin();
        arena.sendMessage(MessageUtil.getPrefix() + " §7 Game is starting!");
        ArenaRegistry.getPlayableArenas(arena.getArenaType(), arena.getArenaMode()).remove(arena);
        for (ArenaTeam team : arena.getTeams()) {
            points.put(team, 0);
        }
        for (Player player : arena.getPlayers()) {
            kills.put(player.getUniqueId(), 0);
            goals.put(player.getUniqueId(), 0);
        }
    }

    public void sendBeginningMessage(Player target) {
        MessageUtil.sendCenteredMessage(target, ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
        MessageUtil.sendCenteredMessage(target, ChatColor.WHITE + ChatColor.BOLD.toString() + arena.getArenaType().getFormattedName());
        MessageUtil.sendCenteredMessage(target, ChatColor.GREEN + ChatColor.BOLD.toString() + arena.getArenaMode().getName());
        MessageUtil.sendCenteredMessage(target, "§b§lEliminate your opponent!");
        MessageUtil.sendCenteredMessage(target, "§7§lOpponent(s): §b");
        for (Player opponent : arena.getOpponents(target)) {
            MessageUtil.sendCenteredMessage(target, "§f§l" + opponent.getName());
        }
        target.sendMessage(" ");
        MessageUtil.sendCenteredMessage(target, "§e§lGood Luck!");
        MessageUtil.sendCenteredMessage(target.getPlayer(), ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "-------------------------------------------------");
    }

    public void addKill(Player player) {
        kills.put(player.getUniqueId(), getKills().get(player.getUniqueId()) + 1);
    }

    public void score(ArenaTeam team, Player scorer) {
        getGoals().put(scorer.getUniqueId(), getGoals().get(scorer.getUniqueId()) + 1);
        getPoints().put(team, getPoints().get(team) + 1);
        arena.sendMessage(MessageUtil.getPrefix() + team.getColor() + " " + scorer.getName() + " §7scored a goal!");
        arena.sendTitle(team.getColor() + "§l" + scorer.getName() + " scored!", "§7" + getPoints().get(arena.getRedTeam()) + " : " + getPoints().get(arena.getBlueTeam()));
        scorer.getInventory().clear();
        for (Block block : arena.getBlueBase().getCageFloor().blockList()) {
            block.setType(Material.STAINED_GLASS);
        }
        for (Block block : arena.getRedBase().getCageFloor().blockList()) {
            block.setType(Material.STAINED_GLASS);
        }
        timeLeft = 8;

        cageTask = Bukkit.getServer().getScheduler().runTaskTimer(Duels.getInstance(), () -> {
            timeLeft--;
            if (timeLeft <= 0) {
                arena.getRedBase().getCageFloor().reset();
                arena.getBlueBase().getCageFloor().reset();
                cageTask.cancel();
                for (Player player : arena.getPlayers()) {
                    TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.AQUA + "Fight", "--");
                }
            }
            for (Player player : arena.getPlayers()) {
                if (timeLeft <= 3 && timeLeft != 0) {
                    TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.GREEN + "" + timeLeft + "", "§fCages open in...");
                }
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);
            }
        }, 0, 20);
        Bukkit.getScheduler().runTask(Duels.getInstance(), (Runnable) cageTask);

        for (Player pl : arena.getPlayers()) {
            pl.teleport(arena.getTeamManager().getTeam(pl).getBase().getSpawnPoint().getRandomLocation());
        }

        arena.getKitItemManager().addKitItems(scorer.getInventory());
        if (getPoints().get(team) >= 5) {
            arena.setWinner(team);
            arena.setState(GameState.WINNER_ANNOUNCE);
        }

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

    public BukkitTask getCageTask() {
        return cageTask;
    }
}

