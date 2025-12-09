package game.core;

import game.exceptions.BoundaryExceededException;
import game.utility.Direction;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class ControllableTest {
    Ship ship1;
    Ship ship2;

    @Before
    public void setUp() {
        ship1 = new Ship(0,0,100);
        ship2 = new Ship(20,10,100);
    }

    @Test
    public void moveOutLeft() {
        try {
            ship1.move(Direction.LEFT);
            Assert.fail("Boundary Exceeded Exception should be thrown");
        } catch (BoundaryExceededException e) {
            Assert.assertEquals("Cannot move left. Out of bounds!", e.getMessage());
           
        }
    }

    @Test
    public void moveOutUp() {
        try {
            ship1.move(Direction.UP);
            Assert.fail("Boundary Exceeded Exception should be thrown");
        } catch (BoundaryExceededException e) {
            Assert.assertEquals("Cannot move up. Out of bounds!", e.getMessage());
        }
    }

    @Test
    public void moveOutRight() {
        try {
            ship2.move(Direction.RIGHT);
            Assert.fail("Boundary Exceeded Exception should be thrown");
        } catch (BoundaryExceededException e) {
            Assert.assertEquals("Cannot move right. Out of bounds!", e.getMessage());
        }
    }

    @Test
    public void moveOutDown() {
        try {
            ship2.move(Direction.DOWN);
            Assert.fail("Boundary Exceeded Exception should be thrown");
        } catch (BoundaryExceededException e) {
            Assert.assertEquals("Cannot move down. Out of bounds!", e.getMessage());
        }
    }
}