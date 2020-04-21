package bg.sofia.uni.fmi.mjt.battleships.ships;

import org.junit.Test;

import static org.junit.Assert.*;

public class ShipTest {

    @Test
    public void TestHit() {
        Ship ship = new Ship(1, ShipType.DESTROYER);
        assertTrue(ship.isAlive());
        ship.hit();
        assertTrue(ship.isAlive());
        ship.hit();
        assertFalse(ship.isAlive());
    }
}
