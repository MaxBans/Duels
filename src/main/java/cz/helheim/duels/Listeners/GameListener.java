package cz.helheim.duels.Listeners;

import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.game.Game;
import cz.helheim.duels.state.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {
    private final ArenaManager arenaManager = new ArenaManager();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (ArenaManager.isPlaying(player)) {
            if (ArenaManager.getArena(player).getState().equals(GameState.PREGAME) || ArenaManager.getArena(player).getState().equals(GameState.WAITING_FOR_OPPONENT)) {
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

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getPlayer().getItemInHand() == null || e.getPlayer().getItemInHand().getType().equals(Material.AIR))
            return;
        if(ArenaManager.isPlaying(e.getPlayer())) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§e§lFind new Arena") && e.getPlayer().getItemInHand().getType().equals(Material.PAPER)) {
                    Game.autoJoin(e.getPlayer(), ArenaManager.getArena(e.getPlayer()).getArenaGameMode());
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        if (ArenaManager.isPlaying(player)) {
            e.setCancelled(!ArenaManager.getArena(player).getState().equals(GameState.IN_GAME));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (ArenaManager.isPlaying(player)) {
            ArenaManager.getArena(player).removePlayer(player);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (ArenaManager.isPlaying(player)) {
            e.setCancelled(!ArenaManager.getArena(player).getState().equals(GameState.IN_GAME));
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (ArenaManager.isPlaying(player)) {
            e.setCancelled(!e.getBlock().getType().equals(Material.WOOD));
        }
    }
}
