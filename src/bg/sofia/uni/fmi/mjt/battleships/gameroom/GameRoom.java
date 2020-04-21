package bg.sofia.uni.fmi.mjt.battleships.gameroom;

import bg.sofia.uni.fmi.mjt.battleships.server.PlayerThread;

import java.io.Serializable;

public class GameRoom implements Serializable {
    String creator;
    private GameState state;
    private PlayerThread player1;
    private PlayerThread player2;
    private PlayerThread currentPlayer;


    public GameRoom(String roomName) {
        this.state = new GameState(roomName);
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public PlayerThread getPlayer1() {
        return player1;
    }

    public PlayerThread getPlayer2() {
        return player2;
    }

    public PlayerThread getCurrentPlayer() {
        return currentPlayer;
    }

    public void setPlayer1(PlayerThread player1) {
        this.player1 = player1;
    }


    public void setPlayer2(PlayerThread player2) {
        this.player2 = player2;
    }

    public boolean hasEmptySlot() {
        return player1 == null || player2 == null;
    }

    public void setCurrentPlayer(PlayerThread currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean checkGameOver() {
        return state.getBoard1().getGetShipsAlive() == 0 || state.getBoard2().getGetShipsAlive() == 0;
    }

    public void setGameInProgress(boolean gameInProgress) {
        state.setGameInProgress(gameInProgress);
    }


    public synchronized boolean makeMove(PlayerThread player, int x, int y) {
        if (player == currentPlayer && player.getEnemyBoard().hit(x, y)) {
            currentPlayer = player.getOpponent();
            state.setCurrentPlayerName(currentPlayer.getNickname());
            return true;
        }
        return false;
    }


    public void registerPlayer1(PlayerThread player) {
        this.player1 = player;
        this.state.setPlayerName1(player.getNickname());
        this.state.setCurrentPlayerName(this.player1.getNickname());
        this.currentPlayer = player1;
        player1.setFriendlyBoard(state.getBoard1());
        player1.setEnemyBoard(state.getBoard2());
        player1.setInitializedBoard(false);
        player1.setGameRoom(this);
        player1.setReadyStatus(false);
    }

    public void registerPlayer2(PlayerThread player) {
        this.player2 = player;
        this.state.setPlayerName2(player.getNickname());
        player2.setFriendlyBoard(state.getBoard2());
        player2.setEnemyBoard(state.getBoard1());
        player2.setInitializedBoard(false);
        player2.setGameRoom(this);
        player2.setReadyStatus(false);
    }
}