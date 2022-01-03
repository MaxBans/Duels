package cz.helheim.duels.arena;

public enum ArenaMode {

    SOLO(1,2, "1vs1"),
    DUO(2,4, "2vs2"),
    TRIO(3,6, "3vs3"),
    SQUAD(4,8, "4vs4");

    private final int playersInTeam;
    private final int maxPlayers;
    private final String name;

     ArenaMode(int playersInTeam, int maxPlayers, String name){
        this.playersInTeam = playersInTeam;
        this.maxPlayers = maxPlayers;
        this.name = name;
    }

    public static ArenaMode getByName(String s){
         if(s.equalsIgnoreCase("solo") || s.equalsIgnoreCase("1vs1")){
             return SOLO;
         }else if(s.equalsIgnoreCase("duo") || s.equalsIgnoreCase("2vs2")){
             return DUO;
         }else if(s.equalsIgnoreCase("trio") || s.equalsIgnoreCase("3vs3")){
             return TRIO;
         }else if(s.equalsIgnoreCase("squad") || s.equalsIgnoreCase("4vs4")){
             return SQUAD;
         }else{
             return SOLO;
         }
    }

    public String getName(){
         return name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getPlayersInTeam() {
        return playersInTeam;
    }
}
