package cz.helheim.duels.Listeners;

import cz.helheim.duels.managers.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        event.getPlayer().teleport(ConfigManager.getLobbySpawn());
    }
}
