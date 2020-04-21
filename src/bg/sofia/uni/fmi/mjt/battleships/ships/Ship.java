package bg.sofia.uni.fmi.mjt.battleships.ships;

import java.io.Serializable;

public class Ship implements Serializable {
    private int id;
    private ShipType type;
    private boolean orientation; // 0 -> vertical, 1 -> horizontal
    private int health;

    public Ship(int id, ShipType type) {
        this.id = id;
        this.type = type;
        health = type.size;
        this.orientation = false;
    }

    public void setOrientation(boolean orientation) {
        this.orientation = orientation;
    }

    public int getId() {
        return id;
    }

    public ShipType getType() {
        return type;
    }

    public boolean getOrientation() {
        return orientation;
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health != 0;
    }

    public void hit() {
        if (isAlive()) {
            health--;
        }
    }
}
