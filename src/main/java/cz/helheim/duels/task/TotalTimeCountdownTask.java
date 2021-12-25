package cz.helheim.duels.task;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.managers.ScoreboardManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

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
            for(UUID uuid : arena.getPlayers()) {
                arena.getScoreboard().removePlayer(Bukkit.getPlayer(uuid));
            }
            cancel();
        }else {
            if(arena.getState().equals(GameState.IN_GAME)) {
                arena.getScoreboard().updateScoreboard();
            }
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
