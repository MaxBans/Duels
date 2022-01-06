package cz.helheim.duels.queue;

import com.connorlinfoot.titleapi.TitleAPI;
import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import cz.helheim.duels.arena.ArenaRegistry;
import cz.helheim.duels.arena.ArenaType;
import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Queue {

    public static final ArrayList<Queue> OPEN_QUEUES = new ArrayList<>();
    public static final ArrayList<Queue> CLOSED_QUEUES = new ArrayList<>();

    private final ArrayDeque<Player> playersQueued;
    private final ArenaMode arenaMode;
    private final ArenaType arenaType;
    private boolean started = false;
    private int coolDownTicks;

    public Queue(ArenaMode arenaMode, ArenaType arenaType) {
        this.playersQueued = new ArrayDeque<>();
        this.arenaMode = arenaMode;
        this.arenaType = arenaType;
    }

    public static List<Queue> getQueues(ArenaMode mode, ArenaType type) {
        if(OPEN_QUEUES.isEmpty()){
            for(ArenaMode newMode : ArenaMode.values()){
                for(ArenaType newType : ArenaType.values()){
                    Queue queue = new Queue(newMode, newType);
                    OPEN_QUEUES.add(queue);
                }
            }
        }

        ArrayList<Queue> queues = new ArrayList<>();
        for (Queue queue : OPEN_QUEUES) {
            if (!CLOSED_QUEUES.contains(queue)) {
                if (queue.getArenaMode().equals(mode) && queue.getArenaType().equals(type)) {
                    System.out.println("DEBUG: Found queue");
                    queues.add(queue);
                }
            }
        }
        return queues;
    }

    public static Queue[] getQueues(ArenaMode mode, ArenaType type, int freeSlotsNeeded) {
        ArrayList<Queue> queues = new ArrayList<>();
        for (Queue queue : OPEN_QUEUES) {
            if (!CLOSED_QUEUES.contains(queue)) {
                if (queue.getArenaType().equals(type) && queue.getArenaMode().equals(mode) && queue.playersQueued.size() + freeSlotsNeeded <= queue.getArenaMode().getPlayersInTeam()) {
                    queues.add(queue);
                }
            }
        }
        return queues.toArray(new Queue[0]);
    }

    public static Queue getPlayerQueue(Player player) {
        Queue queueIn = null;

        for (Queue queue : OPEN_QUEUES) {
            if (queue.playersQueued.contains(player)) {
                queueIn = queue;
                break;
            }
        }

        for (Queue queue : CLOSED_QUEUES) {
            if (queueIn == null) {
                if (queue.playersQueued.contains(player)) {
                    queueIn = queue;
                    break;
                }
            } else {
                break;
            }
        }

        return queueIn;
    }

    public static boolean isPlayerInQueue(Player player) {
        return getPlayerQueue(player) != null;
    }

    public void addPlayer(Player player) {
        playersQueued.add(player);
        TitleAPI.sendTitle(player, 20, 30, 20, "§aYou joined the queue!");
        for (Player gamePlayer : playersQueued) {
            int needed = getArenaMode().getMaxPlayers() - playersQueued.size();
            if (needed == 0) {
                gamePlayer.getPlayer().sendMessage("§8" + player.getPlayer().getName() + " §7has joined the queue! (Full queue!)");
            } else if (needed == 1) {
                gamePlayer.getPlayer().sendMessage("§8" + player.getPlayer().getName() + " §7has joined the queue! (need " + needed + " more player)");
            } else {
                gamePlayer.getPlayer().sendMessage("§8" + player.getPlayer().getName() + " §7has joined the queue! (need " + needed + " more players)");
            }
        }
        if (playersQueued.size() == getArenaMode().getMaxPlayers()) {
            onQueueFull();
        }
    }

    public void removePlayer(Player player) {
        playersQueued.remove(player);
        TitleAPI.sendTitle(player, 20, 30, 20, "§cYou left the queue!");
        if (playersQueued.size() < getArenaMode().getMaxPlayers() - 1) {
            onQueueUnFull();
        }
    }

    public void onQueueFull() {
        OPEN_QUEUES.remove(this);
        CLOSED_QUEUES.add(this);
        this.started = true;

        Arena arena = ArenaRegistry.createRandomArena(arenaType, arenaMode);
        for(Player player : playersQueued){
            arena.addPlayer(player);
        }
    }

    public void onQueueUnFull() {
        OPEN_QUEUES.add(this);
        CLOSED_QUEUES.remove(this);

        for (Player player : playersQueued) {
            player.getPlayer().sendMessage("§cStarting cancelled because a player left the queue!");
        }
        this.started = false;
    }

    public ArenaMode getArenaMode() {
        return arenaMode;
    }

    public ArenaType getArenaType() {
        return arenaType;
    }

    public ArrayDeque<Player> getPlayersQueued() {
        return playersQueued;
    }

    public boolean isStarted() {
        return started;
    }

}
