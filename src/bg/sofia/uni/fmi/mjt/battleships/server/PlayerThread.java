package bg.sofia.uni.fmi.mjt.battleships.server;

import bg.sofia.uni.fmi.mjt.battleships.board.Board;
import bg.sofia.uni.fmi.mjt.battleships.gameroom.GameRoom;
import bg.sofia.uni.fmi.mjt.battleships.gameroom.GameState;
import bg.sofia.uni.fmi.mjt.battleships.ships.FleetOfShips;
import bg.sofia.uni.fmi.mjt.battleships.ships.Ship;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class PlayerThread implements Runnable {
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    private GameRoom gameRoom;
    private String nickname;
    private Board friendlyBoard;
    private Board enemyBoard;
    private boolean initializedBoard;
    private boolean gameInProgress;
    private PlayerThread opponent;

    private boolean readyStatus = false;

    public PlayerThread(Socket socket) {
        this.socket = socket;

        try {
            dos = new DataOutputStream(this.socket.getOutputStream());
            dis = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Player died " + e.getMessage());
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void setInitializedBoard(boolean initializedBoard) {
        this.initializedBoard = initializedBoard;
    }

    public boolean isInitializedBoard() {
        return initializedBoard;
    }

    public void setFriendlyBoard(Board board) {
        this.friendlyBoard = board;
    }

    public Board getFriendlyBoard() {
        return friendlyBoard;
    }

    public void setEnemyBoard(Board board) {
        this.enemyBoard = board;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }

    private void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    private void setOpponent(PlayerThread opponent) {
        this.opponent = opponent;
    }

    public PlayerThread getOpponent() {
        return opponent;
    }

    public boolean isReady() {
        return readyStatus;
    }

    public void setReadyStatus(boolean readyStatus) {
        this.readyStatus = readyStatus;
    }

    @Override
    public void run() {
        System.out.println("New player connected");
        while (!socket.isClosed()) {
            try {
                String command = dis.readUTF();
                if (command.startsWith("Name")) {
                    String name = command.substring(6);
                    setNickname(name);
                } else if (command.equals("help")) {
                    help();
                } else if (command.equals("exit")) {
                    exit(false);
                    break;
                } else if (command.startsWith("create-game")) {
                    String[] temp = command.split("\\s+");
                    if (temp.length != 2) {
                        dos.writeUTF("Incorrect use of command. The correct format is: create-game <game-name>");
                        continue;
                    }
                    createGame(temp[1]);
                } else if (command.equals("list-games")) {
                    listGames();
                } else if (command.startsWith("join-game")) {
                    String[] temp = command.split("\\s+");
                    if (temp.length == 2) {
                        joinGame(temp[1]);
                    } else if (temp.length == 1) {
                        joinRandomGame();
                    } else {
                        dos.writeUTF("Incorrect use of command. The correct format is: join-game <game-name>");
                    }
                } else if (command.equals("leave-game")) {
                    leaveGame();
                } else if (command.equals("start")) {
                    ready();
                } else if (command.startsWith("hit")) {
                    hit(command.substring(4));
                } else if (command.equals("save-game")) {
                    saveGame();
                } else if (command.startsWith("delete-game")) {
                    String[] temp = command.split("\\s+");
                    if (temp.length == 2) {
                        try {
                            deleteGame(temp[1]);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        dos.writeUTF("Game has been deleted.");
                    } else {
                        dos.writeUTF("Incorrect use of command. The correct format is: delete-game <game-name>");
                    }
                } else if (command.equals("saved-games")) {
                    try {
                        getMySavedGames();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (command.startsWith("load-game")) {
                    String[] temp = command.split("\\s+");
                    if (temp.length == 2) {
                        try {
                            loadGame(temp[1]);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        dos.writeUTF("Incorrect use of command. The correct format is: load-game <game-name>");
                    }
                } else {
                    dos.writeUTF("Unknown command");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setNickname(String nickname) throws IOException {
        this.nickname = nickname;
        synchronized (Server.players) {
            if (Server.players.containsKey(nickname)) {
                dos.writeUTF("Username " + nickname + " is already in use");
                exit(true);
            } else {
                Server.players.put(nickname, this);
                dos.writeUTF("Welcome, " + nickname);
                dos.writeUTF("type \"help\" to all the commands");
            }
        }
    }

    private void help() throws IOException {
        for (Commands command : Commands.values()) {
            dos.writeUTF(command.toString());
        }
    }

    private void exit(boolean takenUsername) throws IOException {
        dos.writeUTF("You have exited the game");
        synchronized (Server.players) {
            if (!takenUsername) {
                Server.players.remove(nickname);
            }
            leaveGame();
        }
        dis.close();
        dos.close();
        socket.close();

    }

    private void createGame(String roomName) throws IOException {
        synchronized (Server.gameRooms) {
            if (Server.gameRooms.containsKey(roomName)) {
                dos.writeUTF("Game room with the same name already exists");
                return;
            }
            if (roomName.length() < 3 || roomName.length() > 20) {
                dos.writeUTF("Game room name should be between 3 and 20 characters");
                return;
            }
            GameRoom newRoom = new GameRoom(roomName);
            newRoom.setCreator(nickname);
            Server.gameRooms.put(roomName, newRoom);
            dos.writeUTF("Game room successfully created");
        }
    }

    private void listGames() throws IOException {
        String response = "";


        synchronized (Server.gameRooms) {
            if (Server.gameRooms.isEmpty()) {
                response = "There are no active games at the moment.";
            } else {
                String playerName = "NAME";
                String creatorName = "CREATOR";
                String status = "STATUS";
                String players = "PLAYERS";
                response += String.format("| %-25s| %-25s| %-15s| %-8s|\n", playerName, creatorName, status, players);
                response += "|--------------------------+--------------------------+----------------+---------|\n";
                Set<String> keys = Server.gameRooms.keySet();
                for (var it : keys) {
                    int playerCount = 0;
                    playerName = it;
                    creatorName = Server.gameRooms.get(it).getCreator();
                    if (Server.gameRooms.get(it).getState().isGameInProgress()) {
                        status = "In progress";
                    } else {
                        status = "Waiting";
                    }
                    if (Server.gameRooms.get(it).getPlayer1() != null) {
                        playerCount++;
                    }
                    if (Server.gameRooms.get(it).getPlayer2() != null) {
                        playerCount++;
                    }
                    players = playerCount + "/2";
                    response += String.format("| %-25s| %-25s| %-15s| %-8s|\n", playerName, creatorName, status, players);
                }

            }
        }

        dos.writeUTF(response);
    }

    private void joinGame(String roomName) throws IOException {
        String response = "";
        if (gameRoom != null) {
            response = "You are already in a game.";
        } else {

            synchronized (Server.gameRooms) {
                if (!Server.gameRooms.containsKey(roomName)) {
                    response = "Game not found.";
                } else if (Server.gameRooms.get(roomName).getState().isGameInProgress()) {
                    response = "Game already in progress.";
                } else if (!Server.gameRooms.get(roomName).hasEmptySlot()) {
                    response = "Game is full.";
                } else {
                    response = "Joined game successfully.";
                    if (Server.gameRooms.get(roomName).getPlayer1() == null) {
                        Server.gameRooms.get(roomName).registerPlayer1(this);
                    } else {
                        Server.gameRooms.get(roomName).registerPlayer2(this);
                    }
                    gameRoom = Server.gameRooms.get(roomName);
                }

            }
        }
        dos.writeUTF(response);
    }

    private void joinRandomGame() throws IOException {
        String response = "";

        if (gameRoom != null) {
            response = "You are already in a game.";
        } else {
            synchronized (Server.gameRooms) {
                Set<String> keys = Server.gameRooms.keySet();
                for (var it : keys) {
                    if (Server.gameRooms.get(it).hasEmptySlot()) {
                        joinGame(it);
                        response = "Successfully joined game.";
                        break;
                    }
                }
            }
        }
        if (gameRoom == null) {
            response = "There are no joinable games.";
        }
        dos.writeUTF(response);
    }

    private void leaveGame() throws IOException {
        String response = "";

        if (gameRoom == null) {
            response = "You are not in a game.";
        } else {
            synchronized (Server.gameRooms) {
                if (Server.gameRooms.get(gameRoom.getState().getRoomName()).getPlayer1() == this) {
                    Server.gameRooms.get(gameRoom.getState().getRoomName()).setPlayer1(null);
                } else {
                    Server.gameRooms.get(gameRoom.getState().getRoomName()).setPlayer2(null);
                }

                readyStatus = false;
                initializedBoard = false;


                gameRoom.setGameInProgress(false);
                gameRoom = null;

                if (opponent != null) {
                    opponent.dos.writeUTF("Your opponent has left the game... Returning to lobby.");
                    opponent.opponent = null;
                    opponent.leaveGame();
                }
                opponent = null;
            }
            response = "You successfully left the game room";
        }

        dos.writeUTF(response);
    }

    private void ready() throws IOException {
        String response = "";

        if (gameRoom == null) {
            response = "You are not in a game";
        } else if (readyStatus) {
            response = "You are already ready.";
        } else if (gameRoom.getPlayer1() == null || gameRoom.getPlayer2() == null) {
            response = "Not enough players in the game.";
        } else {
            readyStatus = true;
            if (gameRoom.getPlayer1() == this) {
                gameRoom.getPlayer1().setReadyStatus(true);
                opponent = gameRoom.getPlayer2();
                // opponent.dos.writeUTF("hello");
            } else {
                gameRoom.getPlayer2().setReadyStatus(true);
                opponent = gameRoom.getPlayer1();
            }
            response = "Waiting for your opponent to start the game.";
        }

        dos.writeUTF(response);

        startGame();
    }

    private void startGame() throws IOException {
        String response = "";

        synchronized (gameRoom) {
            if (!opponent.readyStatus) {
                try {
                    gameRoom.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                gameRoom.notifyAll();
            }
        }
        if (!opponent.readyStatus) {
            response = "Opponent did no start the game.";
            readyStatus = false;
            opponent.leaveGame();
        } else {
            //gameInProgress = true;
            gameRoom.setGameInProgress(true);
            response = "The game has Started.";
        }

        dos.writeUTF(response);

        if (gameRoom.getState().isGameInProgress()) {
            initializeBoard();
        }
    }

    private void initializeBoard() throws IOException {
        dos.writeUTF("Place your ships");
        FleetOfShips fleet = new FleetOfShips();

        for (Ship ship : fleet.getShips()) {
            String input = "";
            int x = -1;
            int y = -1;
            dos.writeUTF(friendlyBoard.getFriendlyBoardState());
            dos.writeUTF("Place a ship with size " + ship.getType().size);
            boolean tryPlaceShip = false;
            do {
                input = dis.readUTF();
                if (input.length() != 4 && input.length() != 5) {
                    dos.writeUTF("Wrong command.");
                    continue;
                } else {
                    if (input.charAt(input.length() - 1) != 'h' && input.charAt(input.length() - 1) != 'v') {
                        dos.writeUTF("Wrong command.");
                        continue;
                    }
                    x = input.charAt(0) - 'A';
                    y = input.charAt(1) - '0';
                    if (input.length() == 5) {
                        y = y * 10 + input.charAt(2) - '0';
                    }
                    y--;
                    ship.setOrientation(input.charAt(input.length() - 1) == 'h');
                }
                tryPlaceShip = friendlyBoard.placeShip(x, y, ship);
                if (!tryPlaceShip) {
                    dos.writeUTF("Cannot place ship on given coordinates");
                }
            } while (!tryPlaceShip);
        }
        initializedBoard = true;
        dos.writeUTF("Successfully initialized board.");
        dos.writeUTF(friendlyBoard.getFriendlyBoardState());
        dos.writeUTF("\n");
        dos.writeUTF(enemyBoard.getEnemyBoardState());
    }

    private void hit(String coords) throws IOException {
        if (gameRoom == null) {
            dos.writeUTF("you are not in a game.");
            return;
        }
        if (!gameRoom.getState().isGameInProgress()) {
            dos.writeUTF("Game has not started yet.");
            return;
        }
        if (!initializedBoard) {
            dos.writeUTF("Board has not been initialized yet.");
            return;
        }
        if (gameRoom.getCurrentPlayer() != this) {
            dos.writeUTF("Not your turn.");
            return;
        }
        int x = 0;
        int y = 0;
        if (coords.length() == 2) {
            x = coords.charAt(0) - 'A';
            y = coords.charAt(1) - '0';
        } else if (coords.length() == 3) {
            x = coords.charAt(0) - 'A';
            y = coords.charAt(1) - '0';
            y = y * 10 + coords.charAt(2) - '0';
        } else {
            dos.writeUTF("Invalid format.");
        }
        y--;
        if (gameRoom.makeMove(this, x, y)) {
            dos.writeUTF("Successful move.");


            dos.writeUTF(friendlyBoard.getFriendlyBoardState());
            dos.writeUTF(enemyBoard.getEnemyBoardState());
            dos.writeUTF("Waiting for your opponent to make their turn.");

            opponent.dos.writeUTF(enemyBoard.getFriendlyBoardState());
            opponent.dos.writeUTF(friendlyBoard.getEnemyBoardState());
            opponent.dos.writeUTF("Your opponents last turn was: " + coords);

            if (enemyBoard.getGetShipsAlive() == 0) {
                dos.writeUTF("YOU WON!\nReturning to lobby.");
                opponent.dos.writeUTF("You lost.\nReturning to lobby.");
                gameRoom.setGameInProgress(false);
                this.leaveGame();
                opponent.leaveGame();
                return;
            }

            opponent.dos.writeUTF("Your turn!");
        } else {
            dos.writeUTF("Invalid coordinates.");
        }
    }

    private void saveGame() throws IOException {
        if (gameRoom == null) {
            dos.writeUTF("You are not in a game room.");
            return;
        } else if (!gameRoom.getState().isGameInProgress()) {
            dos.writeUTF("Game is not in progress.");
            return;
        }

        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(Server.savedGamesFile, true));

       /* try {
            deleteGame(gameRoom.getState().getRoomName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }*/


        output.writeObject(gameRoom.getState());
        output.flush();
        output.close();

        dos.writeUTF("Successfully saved the game");
    }

    private List<GameState> getSavedGames() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(Server.savedGamesFile);
        List<GameState> states = new ArrayList<>();
        boolean reachedEndOfFile = false;
        GameState state;
        while (!reachedEndOfFile) {
            if (fis.available() != 0) {
                ObjectInputStream ois = new ObjectInputStream(fis);
                state = (GameState) ois.readObject();
                states.add(state);

            } else {
                reachedEndOfFile = true;
            }
        }
        fis.close();
        return states;
    }

    private void deleteGame(String gameName) throws IOException, ClassNotFoundException {
        List<GameState> states = getSavedGames();
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(Server.savedGamesFile));
        for (GameState state : states) {
            if (!state.getRoomName().equalsIgnoreCase(gameName)) {
                output.writeObject(state);
            }
        }
        output.close();
    }

    private void getMySavedGames() throws IOException, ClassNotFoundException {
        List<GameState> states = getSavedGames();

        StringBuilder response = new StringBuilder();

        for (var it : states) {
            if (it.getPlayerName1().equals(nickname) || it.getPlayerName2().equals(nickname)) {
                response.append(it.getRoomName()).append("\n");
            }
        }
        if (response.toString().equals("")) {
            response = new StringBuilder("You have no saved games.");
        }
        dos.writeUTF(response.toString());
    }


    private void loadGame(String roomName) throws ClassNotFoundException, IOException {


        if (gameRoom != null) {
            dos.writeUTF("You are already in a game room.");
            return;
        }
        GameState state = null;
        List<GameState> states = getSavedGames();

        for (var it : states) {
            if (it.getRoomName().equals(roomName)) {
                state = it;
                break;
            }
        }

        if (state == null) {
            dos.writeUTF("Save not found.");
            return;
        }

        if (!state.getPlayerName1().equals(nickname) && !state.getPlayerName2().equals(nickname)) {
            dos.writeUTF("Your are not part of this game.");
            return;
        }
        GameRoom loadableRoom;
        if (!Server.gameRooms.containsKey(roomName)) {
            loadableRoom = new GameRoom(state.getRoomName());
            loadableRoom.setCreator(state.getCreator());
            loadableRoom.setGameInProgress(true);
            loadableRoom.setState(state);
            Server.gameRooms.put(state.getRoomName(), loadableRoom);
        } else {
            loadableRoom = Server.gameRooms.get(roomName);
        }

        if (state.getPlayerName1().equals(nickname)) {
            gameRoom = loadableRoom;
            friendlyBoard = state.getBoard1();
            enemyBoard = state.getBoard2();
            gameRoom.setPlayer1(this);
        } else if (state.getPlayerName2().equals(nickname)) {
            gameRoom = loadableRoom;
            friendlyBoard = state.getBoard2();
            enemyBoard = state.getBoard1();
            gameRoom.setPlayer2(this);
        }

        if (state.getCurrentPlayerName().equals(nickname)) {
            gameRoom.setCurrentPlayer(this);
        }

        synchronized (gameRoom) {
            if (gameRoom.getPlayer1() == null || gameRoom.getPlayer2() == null) {
                try {
                    dos.writeUTF("Waiting for the second player to load the game.");
                    gameRoom.wait(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                gameRoom.notifyAll();
            }
            initializedBoard = true;
        }
        if (gameRoom.getPlayer1() == null || gameRoom.getPlayer2() == null) {
            dos.writeUTF("The second player did not load the game.");
            leaveGame();
            return;
        } else if (gameRoom.getPlayer1() == this) {
            opponent = gameRoom.getPlayer2();
        } else {
            opponent = gameRoom.getPlayer1();
        }
        dos.writeUTF("Successfully loaded the game");
        if (gameRoom.getCurrentPlayer() == this) {
            dos.writeUTF(gameRoom.getState().getBoard1().getFriendlyBoardState());
            dos.writeUTF(gameRoom.getState().getBoard2().getEnemyBoardState());
        } else {
            dos.writeUTF(gameRoom.getState().getBoard2().getFriendlyBoardState());
            dos.writeUTF(gameRoom.getState().getBoard1().getEnemyBoardState());
        }
    }

}
