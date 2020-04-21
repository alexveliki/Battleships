package bg.sofia.uni.fmi.mjt.battleships.ships;

import java.io.Serializable;

public enum ShipType implements Serializable {
    CARRIER(5),
    BATTLESHIP(4),
    CRUISER(3),
    DESTROYER(2);

    public final int size;

    ShipType(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}