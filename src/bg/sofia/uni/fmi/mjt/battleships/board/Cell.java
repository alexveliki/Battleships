package bg.sofia.uni.fmi.mjt.battleships.board;

import java.io.Serializable;

public class Cell implements Serializable {

    /*

    -2 -> hit ship
    -1 -> hit water
     0 -> water
     1 -> friendly ship


     */
    private int state;
    private int id;

    public void Cell() {
        state = 0;
        id = 0;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public int getId() {
        return id;
    }

    public boolean hitCell() {
        if (state == 1) {
            state = -2;
            return true;
        } else if (state == 0) {
            state = -1;
            return true;
        } else {
            return false;
        }
    }

    public char getFriendlyState() {
        if (state == 0) {
            return '_';
        } else if (state == -1) {
            return 'O';
        } else if (state == -2) {
            return 'X';
        } else {
            return '*';
        }
    }

    public char getEnemyState() {
        if (state == 0) {
            return '_';
        } else if (state == -1) {
            return 'O';
        } else if (state == -2) {
            return 'X';
        } else {
            return '_';
        }
    }
}
