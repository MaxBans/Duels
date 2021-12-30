package cz.helheim.duels.task;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PreGameCountdownTask extends BukkitRunnable {
    private final Arena arena;
    private int timeLeft = 10;
    public boolean isRunning = false;

    public PreGameCountdownTask(Arena arena) {
        this.arena = arena;
    }

    public void begin() {
        arena.setState(GameState.PREGAME);
        this.runTaskTimer(Duels.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        timeLeft--;
        isRunning = true;
        if (timeLeft <= 0) {
            isRunning = false;
            cancel();
            arena.start();
            return;
        }
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            player.setLevel(timeLeft);
        }
            for (UUID uuid : arena.getPlayers()) {
                Player player = Bukkit.getPlayer(uuid);
                 if (timeLeft == 3) {
                    TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.GREEN + "" + timeLeft + "", "§fGame starting in");
                } else if (timeLeft == 2) {
                    TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.YELLOW + "" + timeLeft + "", "§fGame starting in");
                } else if (timeLeft == 1) {
                    TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.RED + "" + timeLeft + "", "§fGame starting in");
                }
               // player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);
            }
    }

}
