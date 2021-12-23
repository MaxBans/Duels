package cz.helheim.duels.game;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.state.GameState;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class Game {
    private Arena arena;
    private HashMap<UUID, Integer> kills;

    public Game(Arena arena){
        this.arena = arena;
        this.kills = new HashMap<>();
    }

    public void start(){
        arena.getCountdown().begin();
        arena.sendMessage("§8[§3Duels§8]§7 Game is starting!");
    }
}
