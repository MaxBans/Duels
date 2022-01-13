package cz.helheim.duels.managers;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.arena.team.ArenaTeam;
import cz.helheim.duels.utils.PlaceholderUtil;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class ScoreboardManager {
    private static JPerPlayerScoreboard scoreboard;

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

    public static List<String> replaceScoreboard(Arena arena, Player pl, ArenaType type, ArenaMode mode) {
        List<String> list = Duels.getInstance().getConfig().getStringList(type.getFormattedName() + "." +  mode.getName() + ".Scoreboard");
        List<String> replaced = PlaceholderUtil.replacePlaceholders(list, PlaceholderUtil.getArenaPlaceholders(arena, pl));
        return replaced;

    }

    public static JPerPlayerScoreboard getScoreboard(Arena arena, ArenaType type, ArenaMode mode) {
        scoreboard = new JPerPlayerScoreboard((player) -> {
            return Duels.getInstance().getConfig().getString(type.getFormattedName() + "." + mode.getName() + ".ScoreboardTitle").replace("%time%", arena.getTotalTimeCountdownTask().formatTime());
        }, (player) -> {
            return replaceScoreboard(arena, player, type, mode);
        });
        return scoreboard;
    }

}
