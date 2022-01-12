package cz.helheim.duels.task;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.Duels;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.team.ArenaTeam;
import cz.helheim.duels.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CageOpenCountdown extends BukkitRunnable {

    private final Arena arena;
    private byte timeLeft = 8;
    public boolean isRunning = false;

    public CageOpenCountdown(Arena arena) {
        this.arena = arena;
    }

    public void begin() {
        if(isRunning) this.cancel();
        this.runTaskTimer(Duels.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        timeLeft--;
        isRunning = true;
        if (timeLeft <= 0) {
            isRunning = false;
            for(ArenaTeam team : arena.getTeams()){
                team.getBase().getCageFloor().reset();
            }
            this.cancel();
        }
        for (Player player : arena.getPlayers()) {
            if (timeLeft <= 3) {
                TitleAPI.sendTitle(player, 10, 15, 10, ChatColor.GREEN + "" + timeLeft + "", "§fCages open in...");
            }
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 20, 20);
        }
    }
}
