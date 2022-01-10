package cz.helheim.duels.task;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.state.GameState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TotalTimeCountdownTask extends BukkitRunnable {

    private final Arena arena;
    private int timeLeft = 600;
    public boolean isRunning = false;

    public TotalTimeCountdownTask(Arena arena) {
        this.arena = arena;
    }

    public void begin() {
        this.runTaskTimer(Duels.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        timeLeft--;
        isRunning = true;
        if (timeLeft == 0) {
            isRunning = false;
            arena.reset();
            for(Player player : arena.getPlayers()){
                player.setLevel(timeLeft);
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
