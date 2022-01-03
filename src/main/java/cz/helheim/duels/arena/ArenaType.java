package cz.helheim.duels.arena;

public enum ArenaType {
    BUILD_UHC("Build UHC"),
    THE_BRIDGE("The Bridge"),
    CLASSIC_DUELS("Classic Duels");

    private final String name;
    ArenaType(String name){
        this.name = name;
    }

    public String getFormattedName() {
        return name;
    }

    public static ArenaType getByName(String mode){
        if(mode.equalsIgnoreCase("builduhc") || mode.equalsIgnoreCase("build_uhc") || mode.equalsIgnoreCase("uhc") || mode.equalsIgnoreCase("buhc")){
            return BUILD_UHC;
        }else if(mode.equalsIgnoreCase("thebridge") || mode.equalsIgnoreCase("bridge") || mode.equalsIgnoreCase("the_bridge") || mode.equalsIgnoreCase("tb")){
            return THE_BRIDGE;
        }
        else if(mode.equalsIgnoreCase("classic_duels") || mode.equalsIgnoreCase("classicduels") || mode.equalsIgnoreCase("cd") || mode.equalsIgnoreCase("classic")){
            return CLASSIC_DUELS;
        }
        else{
            return null;
        }
    }
}
