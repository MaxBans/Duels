package cz.helheim.duels.utils;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaRegistry;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.managers.ScoreboardManager;
import cz.helheim.duels.queue.Queue;
import org.bukkit.entity.Player;

import java.util.*;

public class PlaceholderUtil {

    public static Map<String, Object> getArenaPlaceholders(Arena arena, Player pl){
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%map%", arena.getMap().getName());
        placeholders.put("%builder%", arena.getMap().getBuilder());
        placeholders.put("%time%", arena.getTotalTimeCountdownTask().formatTime());
        placeholders.put("%id%", String.valueOf(arena.getID()));
        placeholders.put("%opponents%", arena.getOpponentsName(pl));
        placeholders.put("%hp%", arena.getOpponentsHP(pl));
        placeholders.put("%state%", arena.getState().getFormattedName());
        placeholders.put("%arena_type%", arena.getArenaType().getFormattedName());
        placeholders.put("%arena_mode%", arena.getArenaMode().getName());
        placeholders.put("%kills%", String.valueOf(arena.getGame().getKills().get(pl.getUniqueId())));
        placeholders.put("%goals%", String.valueOf(arena.getGame().getGoals().get(pl.getUniqueId())));
        // placeholders.put("%opponent_hp%", (int) arena.getOpponent(pl).getHealth() + "§c§l♥");
        //placeholders.put("%gapple_amount%", String.valueOf(InventoryManager.getAmountOf(Material.GOLDEN_APPLE, pl.getInventory())));
        //placeholders.put("%arrows_amount%", String.valueOf(InventoryManager.getAmountOf(Material.ARROW, pl.getInventory())));
        placeholders.put("%blue_points%", ScoreboardManager.formatPoints(arena.getBlueTeam(), arena.getGame().getPoints().get(arena.getBlueTeam())));
        placeholders.put("%red_points%", ScoreboardManager.formatPoints(arena.getRedTeam(), arena.getGame().getPoints().get(arena.getRedTeam())));
        return placeholders;
    }

    public static Map<String, Object> getGlobalPlaceholders(){
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%duels_playing%", String.valueOf(ArenaRegistry.getTotalPlayersPlaying()));
        placeholders.put("%queue_total%", String.valueOf(Queue.getTotalPlayersQueuingCount()));
        for(ArenaType type : ArenaType.values()){
            placeholders.put("%" + type.getFormattedName() + "_total%", String.valueOf(ArenaRegistry.getTotalPlayersPlaying(type)));
            placeholders.put("%queue_" + type.getFormattedName() + "_total%", String.valueOf(Queue.getTotalPlayersQueuingCount(type)));

            for(ArenaMode mode : ArenaMode.values()){
                placeholders.put("%" + type.getFormattedName() + "_" + mode.getName() + "_total%", String.valueOf(ArenaRegistry.getTotalPlayersPlaying(type, mode)));
                placeholders.put("%queue_" + type.getFormattedName() + "_" + mode.getName() + "_total%", String.valueOf(Queue.getTotalPlayersQueuingCount(type, mode)));
            }
        }

        return placeholders;
    }

    public static List<String> replacePlaceholders(List<String> list, Map<String, Object> map) {
        List<String> replaced = new ArrayList<>();

        skip:
        for(String s : list){
            for(Map.Entry<String, Object> entry : map.entrySet()){
                String key = entry.getKey();
                Object value = entry.getValue();

                if (s.contains(key) && value instanceof Collection) {
                    replaced.addAll((Collection<String>) value);
                    continue skip;
                } else {
                    s = s.replaceAll(key, value.toString());
                }
            }
            replaced.add(s);
        }
        return replaced;
    }
}
