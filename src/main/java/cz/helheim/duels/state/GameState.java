package cz.helheim.duels.state;

public enum GameState {

    IDLE("Idle"),
    WAITING_FOR_OPPONENT("Waiting"),
    PREGAME("Starting"),
    IN_GAME("In Game"),
    WINNER_ANNOUNCE("Ending"),
    END("Restarting");

    private final String name;
    GameState(String name){
        this.name = name;
    }

    public String getFormattedName() {
        return name;
    }

}
