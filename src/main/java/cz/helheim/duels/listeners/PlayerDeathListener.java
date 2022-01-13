package cz.helheim.duels.listeners;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaRegistry;
import cz.helheim.duels.arena.ArenaType;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class PlayerDeathListener implements Listener {

    private final Map<UUID, UUID> lastHitUuid = new HashMap<>();
    private final Set<UUID> causedVoid = new HashSet<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player p = (Player) evt.getEntity();
            if (ArenaRegistry.isInArena(p)) {
                UUID uuid = p.getUniqueId();
                Entity damager = evt.getDamager();
                if (damager instanceof HumanEntity) {
                    if (damager instanceof Player) {
                        lastHitUuid.put(uuid, damager.getUniqueId());
                    } else {
                        lastHitUuid.remove(uuid);
                    }
                } else if (damager instanceof Projectile) {
                    ProjectileSource shooter = ((Projectile) damager).getShooter();
                    if (shooter == null || !(shooter instanceof LivingEntity)) { // we want to make sure the shooter is a LivingEntity
                        lastHitUuid.remove(uuid);
                    } else {
                        if (shooter instanceof HumanEntity) {
                            if (shooter instanceof Player) {
                                lastHitUuid.put(uuid, ((Player) shooter).getUniqueId());
                            } else {
                                lastHitUuid.remove(uuid);
                            }

                        } else {
                            String customName = ((LivingEntity) shooter).getCustomName();
                            lastHitUuid.remove(uuid);
                        }
                    }
                } else if (damager instanceof LivingEntity) {
                    lastHitUuid.remove(uuid);
                } else {
                    lastHitUuid.remove(uuid);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            UUID uuid = evt.getEntity().getUniqueId();
            //if(!ArenaRegistry.getArena(((Player) evt.getEntity()).getPlayer()).getArenaType().equals(ArenaType.THE_BRIDGE))
            if (evt.getCause() == EntityDamageEvent.DamageCause.VOID) {
                causedVoid.add(uuid);
            } else {
                causedVoid.remove(uuid);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void findDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(ArenaRegistry.isInArena(p)) {
                Arena arena = ArenaRegistry.getArena(p);
                if (arena.getState().equals(GameState.IN_GAME)) {
                    if (e.getFinalDamage() >= p.getHealth()) {
                        if (arena.isSpectator(p) || arena.isDead(p)) {
                            return;
                        }
                            if ((e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) || (e.getCause() == EntityDamageEvent.DamageCause.FIRE)) {
                                if (lastHitUuid.containsKey(p.getUniqueId())) {
                                    UUID killedID = lastHitUuid.get(p.getUniqueId());
                                    Player killer = Bukkit.getPlayer(killedID);
                                    arena.killPlayer(p, killer, arena.getArenaType());
                                } else {
                                    e.setCancelled(true);
                                    arena.killPlayer(p, null, arena.getArenaType());
                                }
                            } else if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                                arena.killPlayer(p, null, arena.getArenaType());
                                Bukkit.getLogger().warning("Called Fall 01");
                                p.setFallDistance(0);
                                e.setCancelled(true);
                            } else if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                                wasHit(e, p, arena);

                            } else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                                e.setCancelled(true);
                                if (event.getDamager() instanceof Player) {
                                    Player killer = (Player) event.getDamager();
                                    Bukkit.getLogger().warning("Called Entity Attack 01");
                                    arena.killPlayer(p, killer, arena.getArenaType());
                                } else {
                                    Bukkit.getLogger().warning("Called Entity Attack 02");
                                    arena.killPlayer(p, null, arena.getArenaType());
                                }
                                e.setCancelled(true);
                            } else if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                                Projectile projectile = (Projectile) event.getDamager();
                                if (((projectile.getShooter() instanceof Player)) && ((event.getEntity() instanceof Player))) {
                                    Player player = ((Player) event.getEntity()).getPlayer();
                                    Player killer = ((Player) projectile.getShooter()).getPlayer();
                                    e.setCancelled(true);
                                    event.setCancelled(true);
                                    arena.killPlayer(player, killer, arena.getArenaType());
                                }
                            } else {
                                Bukkit.getLogger().warning("statement not added yet.");
                                e.setCancelled(true);
                                arena.killPlayer(p, null, arena.getArenaType());
                            }
                        }
                    }
                }
            }
         }

      @EventHandler(priority = EventPriority.HIGHEST)
      public void checkBridgeFall(EntityDamageEvent e){
          Entity p = e.getEntity();
          if (((p instanceof Player))){
              Player player = (Player) p;
              if(!ArenaRegistry.isInArena(player)) return;
              Arena arena = ArenaRegistry.getArena(player);
              if(arena.getArenaType() != ArenaType.THE_BRIDGE) return;

              if(e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                  wasHit(e, player, arena);
                  e.setCancelled(true);
                  player.setFallDistance(0);
              }

          }

      }

    public void wasHit(EntityDamageEvent e, Player p, Arena arena) {
        if (lastHitUuid.containsKey(p.getUniqueId())) {
            UUID killedID = lastHitUuid.get(p.getUniqueId());
            Player killer = Bukkit.getPlayer(killedID);
            p.setFallDistance(0);
            arena.killPlayer(p, killer, arena.getArenaType());
        } else {
            p.setFallDistance(0);
            arena.killPlayer(p, null, arena.getArenaType());
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkDead(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (ArenaRegistry.isInArena(player)) {
                Arena arena = ArenaRegistry.getArena(player);
                if (arena.isSpectator(player)) {
                    e.setCancelled(true);
                }

                if(arena.getArenaType() == ArenaType.THE_BRIDGE){
                    if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void checkTeammate(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof  Player){
            Player player = (Player) e.getEntity();
            if(e.getDamager() instanceof Player){
                Player damager = (Player) e.getDamager();
                if(ArenaRegistry.isInArena(player) && ArenaRegistry.isInArena(damager)){
                    if(ArenaRegistry.getArena(player).equals(ArenaRegistry.getArena(damager))){
                        Arena arena = ArenaRegistry.getArena(player);
                        if(arena.getTeamManager().getTeam(player).equals(arena.getTeamManager().getTeam(damager))){
                            e.setCancelled(true);
                        }else{
                            e.setCancelled(false);
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onPearl(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.getPlayer().setNoDamageTicks(1);
            event.getPlayer().teleport(event.getTo());
        }
    }

    public void clear() {
        this.lastHitUuid.clear();
        this.causedVoid.clear();
    }

}
