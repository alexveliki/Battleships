package bg.sofia.uni.fmi.mjt.battleships.ships;

import java.util.ArrayList;
import java.util.List;

public class FleetOfShips {
    private List<Ship> ships;

    public FleetOfShips() {
        ships = new ArrayList<>();

        ships.add(new Ship(1, ShipType.CARRIER));
        ships.add(new Ship(2, ShipType.BATTLESHIP));
        ships.add(new Ship(3, ShipType.BATTLESHIP));
        ships.add(new Ship(4, ShipType.CRUISER));
        ships.add(new Ship(5, ShipType.CRUISER));
        ships.add(new Ship(6, ShipType.CRUISER));
        ships.add(new Ship(7, ShipType.DESTROYER));
        ships.add(new Ship(8, ShipType.DESTROYER));
        ships.add(new Ship(9, ShipType.DESTROYER));
        ships.add(new Ship(10, ShipType.DESTROYER));
    }

    public List<Ship> getShips() {
        return ships;
    }
}
