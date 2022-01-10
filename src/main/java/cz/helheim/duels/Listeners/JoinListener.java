package cz.helheim.duels.Listeners;

import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.queue.Queue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        event.getPlayer().getInventory().clear();
        event.getPlayer().teleport(ConfigManager.getLobbySpawn());
        clearArmor(event.getPlayer());
        event.getPlayer().setLevel(0);
        if(event.getPlayer().getFireTicks() > 0){
            event.getPlayer().setFireTicks(0);
        }
    }

    public void clearArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        if(Queue.isPlayerInQueue(event.getPlayer())){
            Queue queue = Queue.getPlayerQueue(event.getPlayer());
            queue.removePlayer(event.getPlayer());
        }
    }
}
