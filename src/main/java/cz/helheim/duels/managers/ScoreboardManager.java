package cz.helheim.duels.managers;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManager {
    private static  JPerPlayerScoreboard scoreboard;
    private static Map<String, String> placeholders;

    public static List<String> replacePlaceholders(List<String> list, Map<String, String> map) {
        List<String> replaced = new ArrayList<>();
        for(String key : list){
            boolean contained = false;
            for(String mapKey : map.keySet()){
                if(key.contains(mapKey)){
                    String replacement = key.replace(mapKey,String.valueOf(map.get(mapKey)));
                    replaced.add(replacement);
                    contained = true;
                }
            }
            if(!contained){
                replaced.add(key);
            }
        }

        return replaced;
    }

    public static List<String> replace (Arena arena, Player pl){
        List<String> list = Duels.getInstance().getConfig().getStringList("BuildUHC.Scoreboard");
        placeholders = new HashMap<>();
        placeholders.put("%map%", arena.getMap().getName());
        placeholders.put("%builder%", arena.getMap().getBuilder());
        placeholders.put("%time%", arena.getTotalTimeCountdownTask().formatTime());
        placeholders.put("%opponent%", arena.getOpponent(pl).getPlayer().getName());
        placeholders.put("%id%", String.valueOf(arena.getID()));
        placeholders.put("%state%", arena.getState().getFormattedName());
        placeholders.put("%arena_mode%", arena.getArenaGameMode().getFormattedName());
        placeholders.put("%opponent_hp%", (int) arena.getOpponent(pl).getHealth() + "§c§l♥");
        placeholders.put("%gapple_amount%", String.valueOf(InventoryManager.getAmountOf(Material.GOLDEN_APPLE, pl.getInventory())));
        placeholders.put("%op_arrows%", String.valueOf(InventoryManager.getAmountOf(Material.ARROW, pl.getInventory())));
        List<String> replaced = replacePlaceholders(list, placeholders);
        return replaced;
    }

    public Map<String, String> getPlaceholders(){
        return placeholders;
    }

    public static JPerPlayerScoreboard getBUHCScoreboard(Arena arena) {
        scoreboard = new JPerPlayerScoreboard((player) -> {
            return Duels.getInstance().getConfig().getString("BuildUHC.ScoreboardTitle").replace("%time%", arena.getTotalTimeCountdownTask().formatTime());
        }, (player) -> {
            return replace(arena, player);
        });
        return scoreboard;
    }

}
