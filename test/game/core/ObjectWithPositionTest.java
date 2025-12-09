package game.core;

import org.junit.Test;
import org.junit.Assert;

public class ObjectWithPositionTest {

    @Test
    public void testToString() {
        Ship ship = new Ship(1,2,50);
        Assert.assertEquals("Ship(1, 2)", ship.toString());
    }
}
