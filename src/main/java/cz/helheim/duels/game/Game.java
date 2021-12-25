package cz.helheim.duels.game;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.utils.MessageUtil;
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
    }

    public static void autoJoin(Player player) {
        if (arenaManager.getActiveArenas().isEmpty()) {
            ArenaManager.createRandomBuildUHCArena();
        }

        for (Arena arena : arenaManager.getPlayableArenas()) {
            if (arena.getState().equals(GameState.WAITING_FOR_OPPONENT)) {
                arena.addPlayer(player);
                return;
            }
        }
        Random random = new Random();
        Arena arena = arenaManager.getPlayableArenas().get(random.nextInt(arenaManager.getPlayableArenas().size()));
        player.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + arena.getArenaGameMode().toString().replace("_", " "));
        arena.addPlayer(player);

        }

        public static ArenaManager getArenaManager(){
        return arenaManager;
        }
    }

