package game;

import game.achievements.Achievement;
import game.achievements.PlayerStatsTracker;
import game.core.*;
import game.ui.KeyHandler;
import game.ui.Tickable;
import game.ui.UI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ModelTests {
    ModelTestUI testUI;
    PlayerStatsTracker playerStatsTracker;
    GameModel gameModel;

    @Before
    public void setUp() {
        testUI = new ModelTestUI();
        gameModel = new GameModel(testUI::log, playerStatsTracker);
    }

    @Test
    public void testGetStatsTracker() {
        Assert.assertEquals(playerStatsTracker, gameModel.getStatsTracker());
    }

    @Test
    public void testUpdateGame() {
        Bullet bullet = new Bullet(1,2);
        // Enemy should go offscreen next update (when the tick is a multiple of 10).
        Enemy enemy = new Enemy(3,19);
        gameModel.addObject(bullet);
        gameModel.addObject(enemy);
        gameModel.updateGame(10);
        Assert.assertEquals(1, bullet.getY());
        Assert.assertEquals(20, enemy.getY());
        Assert.assertTrue(gameModel.getSpaceObjects().contains(bullet));
        Assert.assertFalse(gameModel.getSpaceObjects().contains(enemy));
    }

    @Test
    public void testSpawnObjects() {
        // Create an existing object to avoid spawning on top of.
        Enemy enemy = new Enemy(5,0);
        gameModel.addObject(enemy);

        for (int i = 0; i < 100; i++) {
            gameModel.spawnObjects(); // Calls spawn objects 100 times, without updating the game,
            // meaning all objects are spawning in the top row, to check for overlap.
        }
        boolean collision = false;
        for (SpaceObject spaceObject : gameModel.getSpaceObjects()) {
            if (spaceObject != enemy && spaceObject.getX() == enemy.getX() && spaceObject.getY()
            == enemy.getY()) {
                collision = true;
            }
            Assert.assertEquals(0, spaceObject.getY());
        }
        Assert.assertFalse(collision);
    }

    @Test
    public void testSetVerbose() {
        gameModel.setVerbose(false);
        gameModel.getShip().addScore(GameModel.SCORE_THRESHOLD);
        // Sufficient to pass the first level threshold.
        gameModel.levelUp();
        Assert.assertEquals("",testUI.message);

        gameModel.setVerbose(true);
        gameModel.getShip().addScore(GameModel.SCORE_THRESHOLD);
        // Sufficient to pass the second level threshold.
        gameModel.levelUp();
        Assert.assertEquals("Level Up! Welcome to Level 3. Spawn rate increased to "
                + (GameModel.START_SPAWN_RATE + 2 * GameModel.SPAWN_RATE_INCREASE)
                + "%.", testUI.message);
    }

    @Test
    public void testLevelUp() {
        gameModel.setVerbose(true);
        gameModel.levelUp();
        Assert.assertEquals(1, gameModel.getLevel());
        Assert.assertEquals("", testUI.message);

        gameModel.getShip().addScore(100);
        gameModel.levelUp();
        Assert.assertEquals(2, gameModel.getLevel());
        Assert.assertEquals("Level Up! Welcome to Level 2. Spawn rate increased to " +
                (GameModel.START_SPAWN_RATE + GameModel.SPAWN_RATE_INCREASE) + "%.",
                testUI.message);
    }

    @Test
    public void testFireBullet() {
        gameModel.fireBullet();
        Assert.assertEquals(1, gameModel.getSpaceObjects().size());
        Assert.assertEquals("", testUI.message);
    }

    @Test
    public void testShipPowerUpCollisions() {
        int shipX = gameModel.getShip().getX();
        int shipY = gameModel.getShip().getY();
        ShieldPowerUp powerUp = new ShieldPowerUp(shipX, shipY);

        // First test ship and power up collisions, when isVerbose is false.
        gameModel.addObject(powerUp);
        gameModel.checkCollisions();
        Assert.assertEquals(50, gameModel.getShip().getScore());
        Assert.assertEquals("", testUI.message);
        Assert.assertFalse(gameModel.getSpaceObjects().contains(powerUp));

        // First test ship and power up collisions, when isVerbose is true.
        gameModel.setVerbose(true);
        gameModel.addObject(powerUp);
        gameModel.checkCollisions();
        Assert.assertEquals(100, gameModel.getShip().getScore());
        Assert.assertEquals("PowerUp collected: " + powerUp.render(), testUI.message);
        Assert.assertFalse(gameModel.getSpaceObjects().contains(powerUp));
    }

    @Test
    public void testShipEnemyCollisions() {
        int shipX = gameModel.getShip().getX();
        int shipY = gameModel.getShip().getY();
        Enemy enemy = new Enemy(shipX, shipY);

        // First test ship and enemy collisions, when isVerbose is false.
        gameModel.addObject(enemy);
        gameModel.checkCollisions();
        Assert.assertEquals(100 - GameModel.ENEMY_DAMAGE,
                gameModel.getShip().getHealth());
        Assert.assertEquals("Hit by " + enemy.render() + "! Health reduced by "
                + GameModel.ENEMY_DAMAGE + ".", testUI.message);
        Assert.assertFalse(gameModel.getSpaceObjects().contains(enemy));

        // First test ship and enemy collisions, when isVerbose is true.
        gameModel.setVerbose(true);
        gameModel.addObject(enemy);
        gameModel.checkCollisions();
        Assert.assertEquals(100 - 2 * GameModel.ENEMY_DAMAGE,
                gameModel.getShip().getHealth());
        Assert.assertEquals("Hit by " + enemy.render() + "! Health reduced by "
                        + GameModel.ENEMY_DAMAGE + ".", testUI.message);
        Assert.assertFalse(gameModel.getSpaceObjects().contains(enemy));
    }

    @Test
    public void testShipAsteroidCollisions() {
        int shipX = gameModel.getShip().getX();
        int shipY = gameModel.getShip().getY();
        Asteroid asteroid = new Asteroid(shipX, shipY);

        // First test ship and asteroid collisions, when isVerbose is false.
        gameModel.addObject(asteroid);
        gameModel.checkCollisions();
        Assert.assertEquals(100 - GameModel.ASTEROID_DAMAGE,
                gameModel.getShip().getHealth());
        Assert.assertEquals("Hit by " + asteroid.render() + "! Health reduced by "
                + GameModel.ASTEROID_DAMAGE + ".", testUI.message);
        Assert.assertFalse(gameModel.getSpaceObjects().contains(asteroid));

        // First test ship and asteroid collisions, when isVerbose is true.
        gameModel.setVerbose(true);
        gameModel.addObject(asteroid);
        gameModel.checkCollisions();
        Assert.assertEquals(100 - 2 * GameModel.ASTEROID_DAMAGE,
                gameModel.getShip().getHealth());
        Assert.assertEquals("Hit by " + asteroid.render() + "! Health reduced by "
                + GameModel.ASTEROID_DAMAGE + ".", testUI.message);
        Assert.assertFalse(gameModel.getSpaceObjects().contains(asteroid));
    }

    @Test
    public void testEnemyBulletCollisions() {
        Bullet bullet = new Bullet(2,5);
        Enemy enemy = new Enemy(2,5);
        gameModel.addObject(bullet);
        gameModel.addObject(enemy);
        gameModel.checkCollisions();
        Assert.assertEquals("", testUI.message);
        Assert.assertFalse(gameModel.getSpaceObjects().contains(enemy));
        Assert.assertFalse(gameModel.getSpaceObjects().contains(bullet));
        Assert.assertEquals(1, playerStatsTracker.getShotsHit());
    }

    @Test
    public void testAsteroidBulletCollisions() {
        // Adds a colliding asteroid and bullet to the game
        Bullet bullet = new Bullet(2,5);
        Asteroid asteroid = new Asteroid(2,5);
        gameModel.addObject(bullet);
        gameModel.addObject(asteroid);

        gameModel.checkCollisions();
        // Checks no message is logged
        Assert.assertEquals("", testUI.message);

        // Checks the asteroid is in the game, but the bullet is not.
        Assert.assertTrue(gameModel.getSpaceObjects().contains(asteroid));
        Assert.assertFalse(gameModel.getSpaceObjects().contains(bullet));

        //Makes sure no shots hit were recorded, as hitting an asteroid does not count.
        Assert.assertEquals(0, playerStatsTracker.getShotsHit());
    }

    @Test
    public void testGameOver() {
        Assert.assertFalse(gameModel.checkGameOver());
        gameModel.getShip().takeDamage(gameModel.getShip().getHealth());
        Assert.assertTrue(gameModel.checkGameOver());
    }

    @Test
    public void testIsInBounds() {
        int width = GameModel.GAME_WIDTH;
        int height = GameModel.GAME_HEIGHT;

        Assert.assertTrue(GameModel.isInBounds(new Bullet(width - 1, height - 1)));
        Assert.assertFalse(GameModel.isInBounds(new Bullet(width - 1, height)));
        Assert.assertFalse(GameModel.isInBounds(new Bullet(width, height - 1)));
        Assert.assertFalse(GameModel.isInBounds(new Bullet(width, height)));

        Assert.assertTrue(GameModel.isInBounds(new Bullet(0, 0)));
        Assert.assertFalse(GameModel.isInBounds(new Bullet(-1, 0)));
        Assert.assertFalse(GameModel.isInBounds(new Bullet(-1, -1)));
        Assert.assertFalse(GameModel.isInBounds(new Bullet(-1, -1)));

    }
}

class ModelTestUI implements UI {
    String message = "";
    boolean paused = false;

    public void log(String message) {
        this.message = message;
    }

    public void pause() {
        this.paused = !paused;
    }

    public void start() {
    }


    public void stop() {
    }

    public void onStep(Tickable tickable) {

    }

    public void onKey(KeyHandler key) {
    }

    public void render(List<SpaceObject> objects) {
    }

    public void setStat(String label, String value) {
    }

    public void logAchievementMastered(String message) {
    }

    public void logAchievements(List<Achievement> achievements) {
    }

    public void setAchievementProgressStat(String achievementName, double progressPercentage) {
    }
}
