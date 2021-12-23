package cz.helheim.duels.task;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.player.GamePlayer;
import cz.helheim.duels.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PreGameCountdownTask extends BukkitRunnable {
    private final Arena arena;
    private int timeLeft = 5;

    public PreGameCountdownTask(Arena arena) {
        this.arena = arena;
    }

    public void begin() {
        arena.setState(GameState.PREGAME);
        this.runTaskTimer(Duels.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        if (timeLeft == 0) {
            cancel();
            arena.start();
        }else {
            Bukkit.getLogger().severe("DEBUG: Game starting in arena: " + String.valueOf(arena.getID()));
            for (GamePlayer pl : arena.getPlayers()) {
                Player player = pl.getPlayer();
                if (timeLeft == 5 || timeLeft == 4) {
                    return;
                } else if (timeLeft == 3) {
                    TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.GREEN + "" + timeLeft + "", "§fGame starting in");
                } else if (timeLeft == 2) {
                    TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.YELLOW + "" + timeLeft + "", "§fGame starting in");
                } else if (timeLeft == 1) {
                    TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.RED + "" + timeLeft + "", "§fGame starting in");
                }
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);
            }
        }

    }

}
