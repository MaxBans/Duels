package cz.helheim.duels.Listeners;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaRegistry;
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
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (ArenaRegistry.isInArena(player)) {
            if (ArenaRegistry.getArena(player).getState().equals(GameState.PREGAME) || ArenaRegistry.getArena(player).getState().equals(GameState.WAITING_FOR_OPPONENT) || ArenaRegistry.getArena(player).getState().equals(GameState.IDLE)) {
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
        if(ArenaRegistry.isInArena(e.getPlayer())) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (e.getPlayer().getItemInHand().getItemMeta().getDisplayName().equals("§e§lFind new Arena") && e.getPlayer().getItemInHand().getType().equals(Material.PAPER)) {
                    Game.autoJoin(e.getPlayer(), ArenaRegistry.getArena(e.getPlayer()).getArenaType(), ArenaRegistry.getArena(e.getPlayer()).getArenaMode());
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e){
        Player player = e.getPlayer();
        if (ArenaRegistry.isInArena(player)) {
            e.setCancelled(!ArenaRegistry.getArena(player).getState().equals(GameState.IN_GAME));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (ArenaRegistry.isInArena(player)) {
            ArenaRegistry.getArena(player).removePlayer(player);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (ArenaRegistry.isInArena(player)) {
            if(!ArenaRegistry.getArena(player).getState().equals(GameState.IN_GAME)){
                e.setCancelled(true);
                return;
            }

            ArenaRegistry.getArena(player).getPlacedBlocks().add(e.getBlock());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        if (ArenaRegistry.isInArena(player)) {
            if(!ArenaRegistry.getArena(player).getState().equals(GameState.IN_GAME)){
                e.setCancelled(true);
                return;
            }
            e.setCancelled(!ArenaRegistry.getArena(player).getPlacedBlocks().contains(e.getBlock()));
        }
    }
}
