package cz.helheim.duels.modes;

public enum ArenaGameMode {
    BUILD_UHC("Build UHC"),
    THE_BRIDGE("The Bridge");

    private final String name;
    ArenaGameMode(String name){
        this.name = name;
    }

    public String getFormattedName() {
        return name;
    }
}
