package cz.helheim.duels.Listeners;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.player.GamePlayer;
import cz.helheim.duels.state.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class GameListener implements Listener {
    private ArenaManager arenaManager = new ArenaManager();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = new GamePlayer(player);
        if(gamePlayer.isPlaying()) {
            if (gamePlayer.getArena().getState().equals(GameState.PREGAME) || gamePlayer.getArena().getState().equals(GameState.WAITING_FOR_OPPONENT)) {
                double xTo = event.getTo().getX();
                double xFrom = event.getFrom().getX();
                double yTo = event.getTo().getY();
                double yFrom = event.getFrom().getY();
                double zTo = event.getTo().getZ();
                double zFrom = event.getFrom().getZ();
                if (event.getTo().locToBlock(xTo) != event.getFrom().locToBlock(xFrom) || event.getTo().locToBlock(zTo) != event.getFrom().locToBlock(zFrom) || event.getTo().locToBlock(yTo) != event.getFrom().locToBlock(yFrom)) {
                    player.teleport(event.getFrom());
                }
            }
        }
    }
}