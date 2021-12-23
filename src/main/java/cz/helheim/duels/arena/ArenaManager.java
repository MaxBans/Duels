package cz.helheim.duels.arena;

import cz.helheim.duels.game.Game;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaManager {

    private static ArrayList<Arena> activeArenas;
    private static ArrayList<Arena> availableArenas;

    public ArenaManager() {
        activeArenas = new ArrayList<>();
        availableArenas = new ArrayList<>();
    }

    public void createRandomBuildUHCArena(){
        Random random = new Random();
        int id = (int) ((Math.random() * (9999 - 1000)) + 1000);
        Bukkit.getLogger().severe(String.valueOf(id));
        Arena arena = new Arena(id, ArenaGameMode.BUILD_UHC);
        activeArenas.add(arena);
        availableArenas.add(arena);
    }

    public static List<Arena> getActiveArenas() { return activeArenas; }

    public static boolean isPlaying(Player player) {
        for(Arena arena : ArenaManager.getActiveArenas()) {
            if(arena.getPlayers().contains(player.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public static Arena getArena(Player player) {
        for(Arena arena : ArenaManager.getActiveArenas()) {
            if(arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }

    public static Arena getArena(int id) {
        for(Arena arena : activeArenas) {
            if(arena.getID() == id) {
                return arena;
            }
        }

        return null;
    }

    public List<Arena> getAvailableArenas(){
        for(Arena arena : activeArenas){
            if(arena.getState().equals(GameState.IDLE) || arena.getState().equals(GameState.WAITING_FOR_OPPONENT)){
                availableArenas.add(arena);
            }
        }
        return availableArenas;
    }
    public static boolean isIdle(int id) {return getArena(id).getState() == GameState.IDLE; }

}
