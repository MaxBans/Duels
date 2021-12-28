package cz.helheim.duels.task;

import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class EndingCountdownTask extends BukkitRunnable {

    private final Arena arena;
    private int timeLeft = 15;
    public boolean isRunning = false;

    public EndingCountdownTask(Arena arena) {
        this.arena = arena;
    }

    public void begin() {
        this.runTaskTimer(Duels.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        isRunning = true;
        timeLeft--;
        if (timeLeft == 0) {
            isRunning = false;
            cancel();
            arena.reset();
        }
        for(UUID uuid : arena.getPlayers()){
            Player player = Bukkit.getPlayer(uuid);
            player.setLevel(timeLeft);
        }
    }
}
