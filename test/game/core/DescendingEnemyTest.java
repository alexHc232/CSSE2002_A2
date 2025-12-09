package game.core;

import org.junit.Assert;
import org.junit.Test;

public class DescendingEnemyTest {

    @Test
    public void testMovesDown() {
        Enemy enemy = new Enemy(3,4);
        enemy.tick(10);
        Assert.assertEquals(5, enemy.getY());
    }

    @Test
    public void testNoMovement() {
        Enemy enemy = new Enemy(3,4);
        enemy.tick(3);
        Assert.assertEquals(4, enemy.getY());
    }
}
