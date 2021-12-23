package cz.helheim.duels.game;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.state.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public class Game {
    private Arena arena;
    private HashMap<UUID, Integer> kills;
    private static ArenaManager arenaManager = new ArenaManager();

    public Game(Arena arena){
        this.arena = arena;
        this.kills = new HashMap<>();
    }

    public void start(){
        arena.getCountdown().begin();
        arena.sendMessage("§8[§3Duels§8]§7 Game is starting!");
    }

    public static void autoJoin(Player player) {
        if (arenaManager.getActiveArenas().isEmpty()) {
            ArenaManager.createRandomBuildUHCArena();
        }

        for (Arena arena : arenaManager.getAvailableArenas()) {
            if (arena.getState().equals(GameState.WAITING_FOR_OPPONENT)) {
                arena.addPlayer(player);
                return;
            }
        }
        Random random = new Random();
        Arena arena = arenaManager.getAvailableArenas().get(random.nextInt(arenaManager.getAvailableArenas().size()));
        arena.addPlayer(player);

        }
    }

