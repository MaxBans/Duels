package cz.helheim.duels.task;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.state.GameState;
import org.bukkit.scheduler.BukkitRunnable;

public class EndingCountdownTask extends BukkitRunnable {

    private final Arena arena;
    private int timeLeft = 15;

    public EndingCountdownTask(Arena arena) {
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
        }
    }
}
