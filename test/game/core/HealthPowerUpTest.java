package game.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class HealthPowerUpTest {

    private HealthPowerUp healthPowerUp;
    private Ship ship;

    @Before
    public void setUp() {
        healthPowerUp = new HealthPowerUp(2,4);
        ship = new Ship();
        ship.takeDamage(30);
    }

    @Test
    public void testHealingAmount() {
        healthPowerUp.applyEffect(ship);
        Assert.assertEquals(90, ship.getHealth());
    }

    @Test
    public void testNoPrinting() {
        PrintStream systemOutStream = System.out;
        ByteArrayOutputStream outputStore = new ByteArrayOutputStream();
        PrintStream fakeOutputStream = new PrintStream(outputStore);
        System.setOut(fakeOutputStream);

        healthPowerUp.applyEffect(ship);
        Assert.assertEquals("", outputStore.toString());

        System.setOut(systemOutStream);
    }

    @Test
    public void testMovement() {
        // Check the PowerUp doesn't move when the tick is not a multiple of 10.
        healthPowerUp.tick(3);
        Assert.assertEquals(2, healthPowerUp.getX());
        Assert.assertEquals(4, healthPowerUp.getY());

        // Check the power up descends by 1 when the tick is a multiple of 10.
        healthPowerUp.tick(10);
        Assert.assertEquals(2, healthPowerUp.getX());
        Assert.assertEquals(5,healthPowerUp.getY());
    }

    @After
    public void tearDown() {
        healthPowerUp = null;
        ship = null;
    }
}
