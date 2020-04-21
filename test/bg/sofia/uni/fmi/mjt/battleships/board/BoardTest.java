package bg.sofia.uni.fmi.mjt.battleships.board;


import bg.sofia.uni.fmi.mjt.battleships.ships.Ship;
import bg.sofia.uni.fmi.mjt.battleships.ships.ShipType;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void TestPlaceShip() {
        Board board = new Board();
        Ship verticalDestroyer = new Ship(1, ShipType.DESTROYER);
        verticalDestroyer.setOrientation(false);
        assertFalse(board.placeShip(-1, -1, verticalDestroyer));
        assertFalse(board.placeShip(10, 10, verticalDestroyer));
        assertTrue(board.placeShip(1, 1, verticalDestroyer));

        Ship horizontalDestroyer = new Ship(1, ShipType.DESTROYER);
        horizontalDestroyer = new Ship(2, ShipType.DESTROYER);
        horizontalDestroyer.setOrientation(true);
        assertFalse((board.placeShip(2, 1, horizontalDestroyer)));
    }

    @Test
    public void TestHit() {
        Board board = new Board();
        Ship verticalDestroyer = new Ship(1, ShipType.DESTROYER);
        verticalDestroyer.setOrientation(false);
        board.placeShip(1, 1, verticalDestroyer);

        assertFalse(board.hit(-1, -1));
        assertTrue(board.hit(5, 5));
        assertTrue(board.hit(1, 1));
        assertFalse(board.hit(1, 1));
        assertEquals(1, board.shipsAlive);
        assertTrue(board.hit(2, 1));
        assertEquals(0, board.shipsAlive);

    }

    @Test
    public void TestGetFriendlyBoardState() {
        Board board = new Board();
        Ship verticalDestroyer = new Ship(1, ShipType.DESTROYER);
        verticalDestroyer.setOrientation(false);
        board.placeShip(0, 0, verticalDestroyer);

        Ship horizontalDestroyer = new Ship(1, ShipType.DESTROYER);
        horizontalDestroyer = new Ship(2, ShipType.DESTROYER);
        horizontalDestroyer.setOrientation(true);
        board.placeShip(3, 0, horizontalDestroyer);

        board.hit(0, 0);
        board.hit(1, 0);
        board.hit(3, 1);
        board.hit(0, 3);


        String expected =
                "       YOUR BOARD\n" +
                        "   1 2 3 4 5 6 7 8 9 10\n" +
                        "   _ _ _ _ _ _ _ _ _ _\n" +
                        "A |&|_|_|O|_|_|_|_|_|_|\n" +
                        "B |&|_|_|_|_|_|_|_|_|_|\n" +
                        "C |_|_|_|_|_|_|_|_|_|_|\n" +
                        "D |*|X|_|_|_|_|_|_|_|_|\n" +
                        "E |_|_|_|_|_|_|_|_|_|_|\n" +
                        "F |_|_|_|_|_|_|_|_|_|_|\n" +
                        "G |_|_|_|_|_|_|_|_|_|_|\n" +
                        "H |_|_|_|_|_|_|_|_|_|_|\n" +
                        "I |_|_|_|_|_|_|_|_|_|_|\n" +
                        "J |_|_|_|_|_|_|_|_|_|_|\n";
        assertEquals(expected, board.getFriendlyBoardState());
    }

    @Test
    public void TestGetEnemyBoardState() {
        Board board = new Board();
        Ship verticalDestroyer = new Ship(1, ShipType.DESTROYER);
        verticalDestroyer.setOrientation(false);
        board.placeShip(0, 0, verticalDestroyer);

        Ship horizontalDestroyer = new Ship(1, ShipType.DESTROYER);
        horizontalDestroyer = new Ship(2, ShipType.DESTROYER);
        horizontalDestroyer.setOrientation(true);
        board.placeShip(3, 0, horizontalDestroyer);

        board.hit(0, 0);
        board.hit(1, 0);
        board.hit(3, 1);
        board.hit(0, 3);


        String expected =
                "       ENEMY BOARD\n" +
                        "   1 2 3 4 5 6 7 8 9 10\n" +
                        "   _ _ _ _ _ _ _ _ _ _\n" +
                        "A |&|_|_|O|_|_|_|_|_|_|\n" +
                        "B |&|_|_|_|_|_|_|_|_|_|\n" +
                        "C |_|_|_|_|_|_|_|_|_|_|\n" +
                        "D |_|X|_|_|_|_|_|_|_|_|\n" +
                        "E |_|_|_|_|_|_|_|_|_|_|\n" +
                        "F |_|_|_|_|_|_|_|_|_|_|\n" +
                        "G |_|_|_|_|_|_|_|_|_|_|\n" +
                        "H |_|_|_|_|_|_|_|_|_|_|\n" +
                        "I |_|_|_|_|_|_|_|_|_|_|\n" +
                        "J |_|_|_|_|_|_|_|_|_|_|\n";
        assertEquals(expected, board.getEnemyBoardState());
    }


}
