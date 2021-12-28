package cz.helheim.duels.arena;

import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArenaManager {

    private static ArrayList<Arena> activeArenas;
    private static ArrayList<Arena> playableArenas;

    public ArenaManager() {
        activeArenas = new ArrayList<>();
        playableArenas = new ArrayList<>();
    }

    public static Arena createRandomArena(ArenaGameMode mode) {
        int id = (int) ((Math.random() * (9999 - 1000)) + 1000);
        Bukkit.getLogger().severe(String.valueOf(id));
        Arena arena = new Arena(id, mode, MapManager.getRandomMap(mode));
        activeArenas.add(arena);
        playableArenas.add(arena);
        return arena;
    }

    public static boolean isPlaying(Player player) {
        for (Arena arena : activeArenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public static Arena getArena(Player player) {
        for (Arena arena : activeArenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }
        return null;
    }

    public static Arena getArena(int id) {
        for (Arena arena : activeArenas) {
            if (arena.getID() == id) {
                return arena;
            }
        }

        return null;
    }

    public static boolean isInGame(int id) {
        return getArena(id).getState() == GameState.IN_GAME;
    }

    public static boolean isIdle(int id) {
        return getArena(id).getState() == GameState.IDLE;
    }

    public List<Arena> getActiveArenas(ArenaGameMode mode) {
        List<Arena> list = new ArrayList<>();
        for (Arena arena : activeArenas) {
            if (arena.getArenaGameMode().equals(mode)) {
                list.add(arena);
            }
        }
        return list;
    }

    public List<Arena> getPlayableArenas(ArenaGameMode mode) {
        if (activeArenas.isEmpty()) {
                createRandomArena(mode);
            }

        for (Arena arena : activeArenas) {
            if (arena.isAvailable()) {
                playableArenas.add(arena);
            }
        }
        List<Arena> modeArenas = new ArrayList<>();
        for (Arena arena : playableArenas) {
            if (arena.getArenaGameMode().equals(mode)) {
                modeArenas.add(arena);
            }
        }
        return modeArenas;
    }

}
