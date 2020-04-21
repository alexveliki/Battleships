package bg.sofia.uni.fmi.mjt.battleships.server;

public enum Commands {

    HELP("help", "shows list of available commands"),
    HIT("hit", "shoot the enemy field on given coordinates (for example \"hit A1\" "),
    LIST_GAMES("list-games", "shows all game rooms"),
    CREATE_GAME("create-game", "creates a game with a given name"),
    JOIN_GAME("join-game", "join a game room with a given name or join a random one if a name is not provided"),
    LEAVE_GAME("leave-game", "leave your current game"),
    START("start", "sets your status to ready and starts the game when both players are ready"),
    EXIT("exit", "exit the game and server"),
    SAVE_GAME("save-game", "creates a save on a file"),
    LOAD_GAME("load-game", "load a save with a given name"),
    SHOW_MY_GAMES("saved-games", "shows your saves"),
    DELETE_GAME("delete-game", "deletes your save with a given name");


    private String command;
    private String description;

    Commands(String command, String description) {
        this.command = command;
        this.description = description;
    }


    @Override
    public String toString() {
        return String.format("%1$-15s|%2$s", command, description);
    }
}
