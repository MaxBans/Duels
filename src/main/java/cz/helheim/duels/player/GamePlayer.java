package cz.helheim.duels.player;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GamePlayer {
    private final Player player;
    private Arena arena;


    public GamePlayer(Player player){
        this.player = player;
    }

    public boolean isPlaying() {
        return arena != null;
    }

    public void setArena(Arena arena){
        this.arena = arena;
    }

    public Arena getArena() {
        return this.arena;
    }


    public Player getPlayer() {
        return player;
    }
}
