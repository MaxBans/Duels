package cz.helheim.duels.arena.team;

import cz.helheim.duels.arena.Arena;
import cz.helheim.duels.arena.ArenaMode;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamManager {

    private final Arena arena;
    private final ArenaTeam redTeam;
    private final ArenaTeam blueTeam;

    public TeamManager(Arena arena){
        this.arena = arena;
        this.redTeam = arena.getRedTeam();
        this.blueTeam = arena.getBlueTeam();
    }

    public void autoJoin(Player player){
        if(redTeam.getMembers().size() == 0){
            redTeam.addPlayer(player);
        }else if(redTeam.isFull()){
            blueTeam.addPlayer(player);
        }else if(blueTeam.isFull()){
            redTeam.addPlayer(player);
        }else if(blueTeam.getMembers().size() == 0){
            blueTeam.addPlayer(player);
        }else if(blueTeam.getMembers().size() > redTeam.getMembers().size()){
            redTeam.addPlayer(player);
        }else if(redTeam.getMembers().size() > blueTeam.getMembers().size()){
            blueTeam.addPlayer(player);
        }else if(redTeam.getMembers().size() == blueTeam.getMembers().size()){
            redTeam.addPlayer(player);
        }
    }

    public ArenaTeam getTeam(Player player){
        for(ArenaTeam team : arena.getTeams()){
            if(team.getMembers().contains(player)){
                return team;
            }
        }
        return null;
    }

    public boolean isLastTeam(ArenaTeam team){
        for(ArenaTeam arenaTeam : arena.getTeams()){
            if(arenaTeam.getAlivePlayers().isEmpty()){
                return arenaTeam != team;
            }
        }
        return false;
    }
}
