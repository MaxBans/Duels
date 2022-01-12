package cz.helheim.duels.arena;

import cz.helheim.duels.arena.team.ArenaTeam;
import cz.helheim.duels.utils.Cuboid;

public class ArenaBase {

    private final Cuboid baseCuboid;
    private final Cuboid spawnPoint;
    private final Cuboid respawnPoint;
    private final Cuboid portal;
    private final Cuboid cage;
    private final Cuboid cageFloor;

    public ArenaBase(Cuboid baseCuboid, Cuboid spawnPoint, Cuboid respawnPoint, Cuboid portal, Cuboid cage, Cuboid cageFloor) {
        this.baseCuboid = baseCuboid;
        this.spawnPoint = spawnPoint;
        this.respawnPoint = respawnPoint;
        this.portal = portal;
        this.cage = cage;
        this.cageFloor = cageFloor;
    }

    public Cuboid getBaseCuboid() {
        return baseCuboid;
    }

    public Cuboid getSpawnPoint() {
        return spawnPoint;
    }

    public Cuboid getRespawnPoint() {
        return respawnPoint;
    }

    public Cuboid getPortal() {
        return portal;
    }

    public Cuboid getCage() {
        return cage;
    }

    public Cuboid getCageFloor() {
        return cageFloor;
    }
}
