package cz.helheim.duels.managers;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.arena.team.ArenaTeam;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreboardManager {
    private static JPerPlayerScoreboard scoreboard;
    private static Map<String, String> placeholders;

    public static List<String> replacePlaceholders(List<String> list, Map<String, String> map) {
        List<String> replaced = new ArrayList<>();
        for (String key : list) {
            boolean contained = false;
            for (String mapKey : map.keySet()) {
                if (key.contains(mapKey)) {
                    String replacement = key.replace(mapKey, String.valueOf(map.get(mapKey)));
                    replaced.add(replacement);
                    contained = true;
                }
            }
            if (!contained) {
                replaced.add(key);
            }
        }

        return replaced;
    }

    public static String formatPoints(ArenaTeam team, Integer points) {
        String format = ChatColor.DARK_GRAY + "● ● ● ● ●";
        switch (points) {
            case 0:
                return format;
            case 1:
                format = team.getColor() + "● §8● ● ● ●";
                return format;
            case 2:
                format = team.getColor() + "● ● §8● ● ●";
                return format;
            case 3:
                format = team.getColor() + "● ● ● §8● ●";
                return format;
            case 4:
                format = team.getColor() + "● ● ● ● §8●";
                return format;
            case 5:
                format = team.getColor() + "● ● ● ● ●";
                return format;
            default:
                return format;
        }
    }

    public static List<String> replace(Arena arena, Player pl, ArenaType type, ArenaMode mode) {
        List<String> list = Duels.getInstance().getConfig().getStringList(type.getFormattedName() + "." +  mode.getName() + ".Scoreboard");
        placeholders = new HashMap<>();
        placeholders.put("%map%", arena.getMap().getName());
        placeholders.put("%builder%", arena.getMap().getBuilder());
        placeholders.put("%time%", arena.getTotalTimeCountdownTask().formatTime());
        placeholders.put("%id%", String.valueOf(arena.getID()));
        placeholders.put("%state%", arena.getState().getFormattedName());
        placeholders.put("%arena_type%", arena.getArenaType().getFormattedName());
        placeholders.put("%arena_mode%", arena.getArenaMode().getName());
        placeholders.put("%kills%", String.valueOf(arena.getGame().getKills().get(pl.getUniqueId())));
        placeholders.put("%goals%", String.valueOf(arena.getGame().getGoals().get(pl.getUniqueId())));
        // placeholders.put("%opponent_hp%", (int) arena.getOpponent(pl).getHealth() + "§c§l♥");
        //placeholders.put("%gapple_amount%", String.valueOf(InventoryManager.getAmountOf(Material.GOLDEN_APPLE, pl.getInventory())));
        //placeholders.put("%arrows_amount%", String.valueOf(InventoryManager.getAmountOf(Material.ARROW, pl.getInventory())));
        placeholders.put("%blue_points%", formatPoints(arena.getBlueTeam(), arena.getGame().getPoints().get(arena.getBlueTeam())));
        placeholders.put("%red_points%", formatPoints(arena.getRedTeam(), arena.getGame().getPoints().get(arena.getRedTeam())));
        List<String> replaced = replacePlaceholders(list, placeholders);
        return replaced;

    }

    public static JPerPlayerScoreboard getScoreboard(Arena arena, ArenaType type, ArenaMode mode) {
        scoreboard = new JPerPlayerScoreboard((player) -> {
            return Duels.getInstance().getConfig().getString(type.getFormattedName() + "." + mode.getName() + ".ScoreboardTitle").replace("%time%", arena.getTotalTimeCountdownTask().formatTime());
        }, (player) -> {
            return replace(arena, player, type, mode);
        });
        return scoreboard;
    }

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

}
