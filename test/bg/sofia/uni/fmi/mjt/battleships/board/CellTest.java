package bg.sofia.uni.fmi.mjt.battleships.board;

import org.junit.Test;

import static org.junit.Assert.*;

public class CellTest {

    @Test
    public void TestHitCell() {
        Cell cell = new Cell();
        assertTrue(cell.hitCell());
        assertFalse(cell.hitCell());
        cell.setState(1);
        assertTrue(cell.hitCell());
        assertEquals(-2, cell.getState());
        cell.setState(0);
        assertTrue(cell.hitCell());
        assertEquals(-1, cell.getState());
    }

    @Test
    public void TestGetFriendlyState() {
        Cell cell = new Cell();
        cell.setState(1);
        assertEquals('*', cell.getFriendlyState());
        cell.setState(-2);
        assertEquals('X', cell.getFriendlyState());
        cell.setState(-1);
        assertEquals('O', cell.getFriendlyState());
        cell.setState(0);
        assertEquals('_', cell.getFriendlyState());
    }

    @Test
    public void TestGetEnemyState() {
        Cell cell = new Cell();
        cell.setState(1);
        assertEquals('_', cell.getEnemyState());
        cell.setState(-2);
        assertEquals('X', cell.getEnemyState());
        cell.setState(-1);
        assertEquals('O', cell.getEnemyState());
        cell.setState(0);
        assertEquals('_', cell.getEnemyState());
    }
}