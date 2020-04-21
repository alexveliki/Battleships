package bg.sofia.uni.fmi.mjt.battleships.gameroom;

import bg.sofia.uni.fmi.mjt.battleships.board.Board;

import java.io.Serializable;

public class GameState implements Serializable {
    private String roomName;
    private String creator;
    private Board board1;
    private Board board2;
    private String playerName1;
    private String playerName2;
    private String currentPlayerName;
    private boolean gameInProgress;

    public GameState(String roomName) {
        this.roomName = roomName;
        this.clearBoard1();
        this.clearBoard2();
    }

    public String getRoomName() {
        return roomName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void clearBoard1() {
        this.board1 = new Board();
    }

    public Board getBoard1() {
        return this.board1;
    }

    public void clearBoard2() {
        this.board2 = new Board();
    }

    public Board getBoard2() {
        return this.board2;
    }

    public String getPlayerName1() {
        return playerName1;
    }

    public void setPlayerName1(String playerName1) {
        this.playerName1 = playerName1;
    }

    public String getPlayerName2() {
        return playerName2;
    }

    public void setPlayerName2(String playerName2) {
        this.playerName2 = playerName2;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    protected void setCurrentPlayerName(String currentPlayerName) {
        this.currentPlayerName = currentPlayerName;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    protected void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }
}