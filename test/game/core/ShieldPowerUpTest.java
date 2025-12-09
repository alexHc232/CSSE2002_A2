package game.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ShieldPowerUpTest {
    private ShieldPowerUp shieldPowerUp;
    private Ship ship;

    @Before
    public void setUp() {
        shieldPowerUp = new ShieldPowerUp(2,4);
        ship = new Ship();
    }

    @Test
    public void testScoreAmount() {
        shieldPowerUp.applyEffect(ship);
        Assert.assertEquals(50, ship.getScore());
    }

    @Test
    public void testNoPrinting() {
        PrintStream systemOutStream = System.out;
        ByteArrayOutputStream outputStore = new ByteArrayOutputStream();
        PrintStream fakeOutputStream = new PrintStream(outputStore);
        System.setOut(fakeOutputStream);

        shieldPowerUp.applyEffect(ship);
        Assert.assertEquals("", outputStore.toString());

        System.setOut(systemOutStream);
    }

    @After
    public void tearDown() {
        shieldPowerUp = null;
        ship = null;
    }
}
