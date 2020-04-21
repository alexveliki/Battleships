package bg.sofia.uni.fmi.mjt.battleships.board;

import bg.sofia.uni.fmi.mjt.battleships.ships.Ship;

import java.io.Serializable;

public class Board implements Serializable {

    private static final int BOARD_SIZE = 10;
    private static final int MAX_SHIP_COUNT = 10;
    Cell[][] board;
    Ship[] ships;
    int shipsAlive;

    public Board() {
        board = new Cell[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = new Cell();
            }
        }
        ships = new Ship[MAX_SHIP_COUNT + 1];
        shipsAlive = 0;
    }

    public int getGetShipsAlive() {
        return shipsAlive;
    }

    private boolean checkCoordsInBounds(int x, int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public boolean placeShip(int x, int y, Ship ship) {
        boolean orientation = ship.getOrientation();
        int shipSize = ship.getType().getSize();

        int xIncrease;
        int yIncrease;

        if (orientation) {
            xIncrease = 0;
            yIncrease = 1;
        } else {
            xIncrease = 1;
            yIncrease = 0;
        }

        int newX = x;
        int newY = y;
        for (int i = 0; i < shipSize; i++) {
            if (!checkCoordsInBounds(newX, newY) || board[newX][newY].getState() != 0) {
                return false;
            }
            newX += xIncrease;
            newY += yIncrease;
        }
        newX = x;
        newY = y;
        for (int i = 0; i < shipSize; i++) {
            board[newX][newY].setId(ship.getId());
            board[newX][newY].setState(1);
            newX += xIncrease;
            newY += yIncrease;
        }
        shipsAlive++;
        ships[ship.getId()] = ship;
        return true;
    }

    public boolean hit(int x, int y) {
        if (!checkCoordsInBounds(x, y)) {
            return false;
        }
        if (!board[x][y].hitCell()) {
            return false;
        }
        if (board[x][y].getId() == 0) {
            return true;
        }
        ships[board[x][y].getId()].hit();
        if (!ships[board[x][y].getId()].isAlive()) {
            shipsAlive--;
        }
        return true;
    }

    public String getFriendlyBoardState() {
        StringBuilder boardState = new StringBuilder();
        boardState.append("       YOUR BOARD\n");
        boardState.append("   1 2 3 4 5 6 7 8 9 10\n");
        boardState.append("   _ _ _ _ _ _ _ _ _ _\n");

        for (int i = 0; i < BOARD_SIZE; i++) {
            boardState.append((char) (i + 'A')).append(" |");
            for (int j = 0; j < BOARD_SIZE; j++) {

                if (board[i][j].getState() == -2 && !ships[board[i][j].getId()].isAlive()) {
                    boardState.append('&');
                } else {
                    boardState.append(board[i][j].getFriendlyState());
                }
                boardState.append('|');
            }
            boardState.append('\n');
        }
        return boardState.toString();
    }

    public String getEnemyBoardState() {
        StringBuilder boardState = new StringBuilder();
        boardState.append("       ENEMY BOARD\n");
        boardState.append("   1 2 3 4 5 6 7 8 9 10\n");
        boardState.append("   _ _ _ _ _ _ _ _ _ _\n");

        for (int i = 0; i < BOARD_SIZE; i++) {
            boardState.append((char) (i + 'A')).append(" |");
            for (int j = 0; j < BOARD_SIZE; j++) {

                if (board[i][j].getState() == -2 && !ships[board[i][j].getId()].isAlive()) {
                    boardState.append('&');
                } else {
                    boardState.append(board[i][j].getEnemyState());
                }
                boardState.append('|');
            }
            boardState.append('\n');
        }
        return boardState.toString();
    }

}
