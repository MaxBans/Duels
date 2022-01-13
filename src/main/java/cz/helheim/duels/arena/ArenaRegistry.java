package cz.helheim.duels.arena;

import cz.helheim.duels.Duels;
import cz.helheim.duels.maps.LocalGameMap;
import cz.helheim.duels.maps.MapManager;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.utils.FileUtil;
import cz.helheim.duels.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaRegistry {

    private static final ArrayList<Arena> activeArenas = new ArrayList<>();
    private static final ArrayList<Arena> playableArenas = new ArrayList<>();



    public static Arena createRandomArena(ArenaType type, ArenaMode mode) {
        MapManager mapManager = new MapManager(FileUtil.getMapsYAML(), type);
        LocalGameMap randomMap = mapManager.getRandomMap(mode);
        Arena arena = new Arena(randomMap.getId(), type, randomMap, mode);
        activeArenas.add(arena);
        playableArenas.add(arena);
        return arena;
    }

    public static boolean isInArena(Player player) {
        for (Arena arena : activeArenas) {
            if (arena.getPlayers().contains(player)) {
                return true;
            }
        }

        return false;
    }

    public static Arena getArena(Player player) {
        for (Arena arena : activeArenas) {
            if (arena.getPlayers().contains(player)) {
                return arena;
            }
        }
        return null;
    }

    public static Arena getArena(String id) {
        for (Arena arena : activeArenas) {
            if (arena.getID() == id) {
                return arena;
            }
        }

        return null;
    }

    public static boolean isInGame(String id) {
        return getArena(id).getState() == GameState.IN_GAME;
    }

    public static boolean isIdle(String id) {
        return getArena(id).getState() == GameState.IDLE;
    }

    public static List<Arena> getActiveArenas(ArenaType type, ArenaMode mode) {
        List<Arena> list = new ArrayList<>();
        for (Arena arena : activeArenas) {
            if (arena.getArenaType().equals(type) && arena.getArenaMode().equals(mode)) {
                list.add(arena);
            }
        }
        return list;
    }

    public static List<Arena> getPlayableArenas(ArenaType type, ArenaMode mode) {
        ArenaRegistry registry = new ArenaRegistry();
        if (activeArenas.isEmpty()) {
                createRandomArena(type, mode);
            }

        for (Arena arena : activeArenas) {
            if (arena.isAvailable()) {
                playableArenas.add(arena);
            }
        }

        List<Arena> modeArenas = new ArrayList<>();
        for (Arena arena : playableArenas) {
            if (arena.getArenaType().equals(type) && arena.getArenaMode().equals(mode)) {
                modeArenas.add(arena);
            }
        }
        return modeArenas;
    }

    public static int getTotalPlayersPlaying(){
        int players = 0;
        if(!activeArenas.isEmpty()) {
            for (Arena arena : activeArenas) {
                if(!arena.getPlayers().isEmpty()) {
                    players = players + arena.getPlayers().size();
                }
            }
        }
        return players;
    }

    public static int getTotalPlayersPlaying(ArenaType type){
        int players = 0;
        if(!activeArenas.isEmpty()) {
            for (Arena arena : activeArenas) {
                if(arena.getArenaType() == type) {
                    if(!arena.getPlayers().isEmpty()) {
                        players = players + arena.getPlayers().size();
                    }
                }
            }
        }
        return players;
    }

    public static int getTotalPlayersPlaying(ArenaType type, ArenaMode mode){
        int players = 0;
        if(!activeArenas.isEmpty()) {
            for (Arena arena : activeArenas) {
                if(arena.getArenaType() == type && arena.getArenaMode() == mode) {
                    if(!arena.getPlayers().isEmpty()) {
                        players = players + arena.getPlayers().size();
                    }
                }
            }
        }
        return players;
    }

}
