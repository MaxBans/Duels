package cz.helheim.duels.Listeners;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaManager;
import cz.helheim.duels.managers.ConfigManager;
import cz.helheim.duels.modes.ArenaGameMode;
import cz.helheim.duels.state.GameState;
import cz.helheim.duels.utils.KillMessages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class PlayerDeathListener implements Listener {

    private final Map<UUID, UUID> lastHitUuid = new HashMap<>();
    private final Set<UUID> causedVoid = new HashSet<>();
    ConfigManager configManager = new ConfigManager();


    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent evt) {
        if (evt.getEntity() instanceof Player) {
            Player p = (Player) evt.getEntity();
            if (ArenaManager.isPlaying(p)) {
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
            if(ArenaManager.isPlaying(p)) {
                Arena arena = ArenaManager.getArena(p);
                if (arena.getState().equals(GameState.IN_GAME)) {
                    if (e.getFinalDamage() >= p.getHealth()) {
                        if (arena.isSpectator(p) || arena.isDead(p)) {
                            return;
                        }
                        if ((e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) || (e.getCause() == EntityDamageEvent.DamageCause.FIRE)) {
                            if (lastHitUuid.containsKey(p.getUniqueId())) {
                                UUID killedID = lastHitUuid.get(p.getUniqueId());
                                Player killer = Bukkit.getPlayer(killedID);
                                arena.killPlayer(p, killer, arena.getArenaGameMode());
                            } else {
                                arena.killPlayer(p, null , arena.getArenaGameMode());
                                e.setCancelled(true);
                                p.setHealth(20D);
                                p.setFoodLevel(20);
                            }
                        } else if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                            e.setCancelled(true);
                            p.setHealth(20D);
                            p.setFoodLevel(20);
                            arena.killPlayer(p, null, arena.getArenaGameMode());

                        } else if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {

                            if (lastHitUuid.containsKey(p.getUniqueId())) {
                                UUID killedID = lastHitUuid.get(p.getUniqueId());
                                Player killer = Bukkit.getPlayer(killedID);
                                e.setCancelled(true);
                                arena.killPlayer(p, killer, arena.getArenaGameMode());

                            } else {
                                arena.killPlayer(p, null, arena.getArenaGameMode());
                                e.setCancelled(true);
                                p.setHealth(20D);
                                p.setFoodLevel(20);
                            }
                        } else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                            e.setCancelled(true);
                            if (event.getDamager() instanceof Player) {
                                Player killer = (Player) event.getDamager();
                                arena.killPlayer(p, killer, arena.getArenaGameMode());

                            } else {
                                arena.killPlayer(p, null,arena.getArenaGameMode());

                            }
                            p.setHealth(20D);
                            p.setFoodLevel(20);
                        } else if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
                            Projectile projectile = (Projectile) event.getDamager();
                            if (((projectile.getShooter() instanceof Player)) && ((event.getEntity() instanceof Player))) {
                                Player player = ((Player) event.getEntity()).getPlayer();
                                Player killer = ((Player) projectile.getShooter()).getPlayer();
                                arena.killPlayer(player, killer, arena.getArenaGameMode());

                                e.setCancelled(true);
                                event.setCancelled(true);
                            }
                        } else {
                            System.out.println("statement not added yet.");
                            e.setCancelled(true);
                            arena.killPlayer(p, null, arena.getArenaGameMode());
                            p.setHealth(20D);
                            p.setFoodLevel(20);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void checkDead(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (ArenaManager.isPlaying(player)) {
                Arena arena = ArenaManager.getArena(player);
                if (arena.isSpectator(player)) {
                    e.setCancelled(true);
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
