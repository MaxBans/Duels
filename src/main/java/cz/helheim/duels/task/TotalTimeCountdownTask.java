package cz.helheim.duels.task;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.manager.ScoreboardManager;
import cz.helheim.duels.player.GamePlayer;
import cz.helheim.duels.state.GameState;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TotalTimeCountdownTask extends BukkitRunnable {

    private final Arena arena;
    private int timeLeft = 600;

    public TotalTimeCountdownTask(Arena arena) {
        this.arena = arena;
    }

    public void begin() {
        this.runTaskTimer(Duels.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        timeLeft--;
        if (timeLeft == 0) {
            cancel();
            arena.reset();
        }else {
            //ScoreboardManager.updateScoreboard(arena.getScoreboard());
        }
    }

    public String formatTime(){
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        String str;
        str = String.format("%d:%02d", minutes, seconds);
        return str;
    }
}
