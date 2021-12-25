package cz.helheim.duels.managers;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.player.GamePlayer;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManager {

    public static List<String> replacePlaceholders(List<String> list, Map<String, String> map) {
        List<String> replaced = new ArrayList<>();
        for (String key : list) {
            for (String mapKey : map.keySet()) {
                if (key.contains(mapKey)) {
                    String replacement = key.replace(mapKey, String.valueOf(map.get(mapKey)));
                    replaced.add(replacement);
                } else {
                    replaced.add(key);
                }
            }
        }

        return replaced;
    }

    public static JPerPlayerScoreboard getBUHCScoreboard(Arena arena, GamePlayer pl) {
        List<String> list = Duels.getInstance().getConfig().getStringList("BuildUHC.Scoreboard");
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%map%", arena.getMap().getName());
        placeholders.put("%builder%", arena.getMap().getBuilder());
        placeholders.put("%time%", arena.getTotalTimeCountdownTask().formatTime());
        placeholders.put("%opponent%", arena.getOpponent(pl).getPlayer().getName());
        placeholders.put("%id%", String.valueOf(arena.getID()));
        placeholders.put("%state%", arena.getState().getFormattedName());
        placeholders.put("%arena_mode%", arena.getArenaGameMode().getFormattedName());
        List<String> replaced = replacePlaceholders(list, placeholders);
        JPerPlayerScoreboard scoreboard = new JPerPlayerScoreboard((player) -> {
            return replaced.get(0);
        }, (player) -> {
            return replaced;
        });
        return scoreboard;
    }

    public static void updateScoreboard(JPerPlayerScoreboard scoreboard) {
        scoreboard.updateScoreboard();
    }

    public static void addToScoreboard(JPerPlayerScoreboard scoreboard, Player player) {
        scoreboard.addPlayer(player);
    }
}
