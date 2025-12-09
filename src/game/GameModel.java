package game;


import game.core.*;
import game.utility.Logger;
import game.core.SpaceObject;
import game.achievements.PlayerStatsTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents the game information and state. Stores and manipulates the game state.
 */
public class GameModel {
    /** Height of the game screen. */
    public static final int GAME_HEIGHT = 20;
    /** Width of the game screen. */
    public static final int GAME_WIDTH = 10;
    /** The initial spawn rate at the start of the game. */
    public static final int START_SPAWN_RATE = 2; // spawn rate (percentage chance per tick)
    /** The amount the spawn rate increases with each new level. */
    public static final int SPAWN_RATE_INCREASE = 3; // Increase spawn rate by 5% per level
    /** The initial level. */
    public static final int START_LEVEL = 1;
    /** The score threshold required to level up. */
    public static final int SCORE_THRESHOLD = 100;
    /** The amount of damage an asteroid deals */
    public static final int ASTEROID_DAMAGE = 10;
    /** The amount of damage an enemy deals */
    public static final int ENEMY_DAMAGE = 20;
    /** The percentage of enemy spawn chance. */
    public static final double ENEMY_SPAWN_RATE = 0.5;
    /** The percentage of power up spawn chance. */
    public static final double POWER_UP_SPAWN_RATE = 0.25;

    private final Random random = new Random(); // ONLY USED IN this.spawnObjects()
    private final List<SpaceObject> spaceObjects; // List of all objects
    private Ship ship; // Core.Ship starts at (5, 10) with 100 health
    private int level; // The current game level
    private int spawnRate; // The current game spawn rate
    private Logger logger; // The Logger reference used for logging.
    private PlayerStatsTracker playerStatsTracker; // The tracker for player statistics.
    private boolean isVerbose; // The verbose state of the model.

    /**
     * Models a game, storing and modifying data relevant to the game.<br>
     * <p>
     * Logger argument should be a method reference to a .log method such as the UI.log method.<br>
     * Example: Model gameModel = new GameModel(ui::log)<br>
     * <p>
     * - Instantiates an empty list for storing all SpaceObjects (except the ship) that the model needs to track.<br>
     * - Instantiates the game level with the starting level value.<br>
     * - Instantiates the game spawn rate with the starting spawn rate.<br>
     * - Instantiates a new ship. (The ship should not be stored in the SpaceObjects list)<br>
     * - Stores reference to the given logger.<br>
     *
     * @param logger a functional interface for passing information between classes.
     */
    public GameModel(Logger logger, PlayerStatsTracker playerStatsTracker) {
        spaceObjects = new ArrayList<>();
        level = START_LEVEL;
        spawnRate = START_SPAWN_RATE;
        ship = new Ship();
        this.logger = logger;
        this.playerStatsTracker = playerStatsTracker;
    }

    /**
     * Returns the ship instance in the game.
     *
     * @return the current ship instance.
     */
    public Ship getShip() {
        return ship;
    }

    /**
     * Returns a list of all SpaceObjects in the game.
     *
     * @return a list of all spaceObjects.
     */
    public List<SpaceObject> getSpaceObjects() {
        return spaceObjects;
    }

    /**
     * Returns the current level.
     *
     * @return the current level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Adds a SpaceObject to the game.<br>
     * <p>
     * Objects are considered part of the game only when they are tracked by the model.<br>
     *
     * @param object the SpaceObject to be added to the game.
     * @requires object != null.
     */
    public void addObject(SpaceObject object) {
        this.spaceObjects.add(object);
    }

    /**
     * Returns the player stats tracker instance in the game.
     *
     * @return the current player statistics tracker.
     */
    public PlayerStatsTracker getStatsTracker() {
        return this.playerStatsTracker;
    }

    /**
     * Updates the game state by moving all objects and then removing off-screen objects.<br>
     * <p>
     * Objects should be moved by calling .tick(tick) on each object.<br>
     * The out-of-bounds objects should then be removed.
     *
     * @param tick the tick value passed through to the objects tick() method.
     */
    public void updateGame(int tick) {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            obj.tick(tick); // Move objects downward
            if (!isInBounds(obj)) { // Remove objects that move off-screen
                toRemove.add(obj);
            }
        }
        spaceObjects.removeAll(toRemove);
    }

    /**
     * Sets the verbose parameter of the Game Model to the provided verbose state.
     * @param verbose the new verbose state the model is set to.
     */
    public void setVerbose(boolean verbose) {
        this.isVerbose = verbose;
    }

    /**
     * Spawns new objects (asteroids, enemies, and power-ups) at random positions.
     * Uses this.random to make EXACTLY 6 calls to random.nextInt() and 1 random.nextBoolean.
     * <p>
     * Random calls should be in the following order:<br>
     * 1. Check if an asteroid should spawn (random.nextInt(100) &lt; spawnRate)<br>
     * 2. If spawning an asteroid, spawn at x-coordinate = random.nextInt(GAME_WIDTH)<br>
     * 3. Check if an enemy should spawn (random.nextInt(100) &lt; spawnRate * ENEMY_SPAWN_RATE)<br>
     * 4. If spawning an enemy, spawn at x-coordinate = random.nextInt(GAME_WIDTH)<br>
     * 5. Check if a power-up should spawn (random.nextInt(100) &lt; spawnRate *
     * POWER_UP_SPAWN_RATE)<br>
     * 6. If spawning a power-up, spawn at x-coordinate = random.nextInt(GAME_WIDTH)<br>
     * 7. If spawning a power-up, spawn a ShieldPowerUp if random.nextBoolean(), else a
     * HealthPowerUp.<br> <p>
     * Failure to match random calls correctly will result in failed tests.<br>
     * <p>
     * Objects spawn at y = 0 (top of the screen).<br>
     * Objects may not spawn if there is a ship or space object at the intended spawn location.<br>
     * This should NOT impact calls to random.<br>
     */
    public void spawnObjects() {
        spawnAsteroids();
        spawnEnemies();
        spawnPowerUps();
    }

    /**
     * Spawns asteroids, at a random spawn location at the top of the screen (y = 0).
     * Asteroids may not spawn if there is a ship or space object at the intended spawn location.
     */
    public void spawnAsteroids() {
        // Spawn asteroids with a chance determined by spawnRate
        if (random.nextInt(100) < spawnRate) {
            int x = random.nextInt(GAME_WIDTH); // Random x-coordinate
            int y = 0; // Spawn at the top of the screen
            if (!isColliding(x, y)) {
                spaceObjects.add(new Asteroid(x, y));
            }
        }
    }

    /**
     * Spawns enemies, at a random spawn location at the top of the screen (y = 0).
     * Enemies may not spawn if there is a ship or space object at the intended spawn location.
     */
    public void spawnEnemies() {
        // Spawn enemies with a lower chance
        // Half the rate of asteroids
        if (random.nextInt(100) < spawnRate * ENEMY_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            int y = 0;
            if (!isColliding(x, y)) {
                spaceObjects.add(new Enemy(x, y));
            }
        }
    }

    /**
     * Spawns PowerUps, at a random spawn location at the top of the screen (y = 0).
     * PowerUps may not spawn if there is a ship or space object at the intended spawn location.
     */
    public void spawnPowerUps() {
        // Spawn power-ups with an even lower chance
        // One-fourth the spawn rate of asteroids
        if (random.nextInt(100) < spawnRate * POWER_UP_SPAWN_RATE) {
            int x = random.nextInt(GAME_WIDTH);
            int y = 0;
            PowerUp powerUp = random.nextBoolean() ? new ShieldPowerUp(x, y) :
                    new HealthPowerUp(x, y);
            if (!isColliding(x, y)) {
                spaceObjects.add(powerUp);
            }
        }
    }

    /**
     * If level progression requirements are satisfied, levels up the game by
     * increasing the spawn rate and level number.<br>
     * <p>
     * To level up, the score must not be less than the current level multiplied by the score
     * threshold.<br>
     * To increase the level the spawn rate should increase by SPAWN_RATE_INCREASE, and the level
     * number should increase by 1.<br>
     * If the level is increased and verbose is set to true, log the following:
     * "Level Up! Welcome to Level {new level}. Spawn rate increased to {new spawn rate}%."<br>
     */
    public void levelUp() {
        if (ship.getScore() < level * SCORE_THRESHOLD) {
            return;
        }
        level++;
        spawnRate += SPAWN_RATE_INCREASE;
        if (isVerbose) {
            logger.log("Level Up! Welcome to Level " + level + ". Spawn rate increased to "
                    + spawnRate + "%.");
        }
    }

    /**
     * Fires a bullet from the ship's current position.<br>
     * <p>
     * Creates a new bullet at the coordinates the ship occupies.<br>
     */
    public void fireBullet() {
        int bulletX = ship.getX();
        int bulletY = ship.getY(); // Core.Bullet starts just above the ship
        spaceObjects.add(new Bullet(bulletX, bulletY));
    }

    /**
     * Detects and handles collisions between spaceObjects (Ship and Bullet collisions).<br>
     * Objects are considered to be colliding if they share x and y coordinates.<br>
     *
     * Ship collisions:
     * - If the ship is colliding with a powerup, apply the effect, and if verbose is true,
     * .log("PowerUp collected: " + obj.render())<br>
     * - If the ship is colliding with an asteroid, take the appropriate damage, and if verbose,
     * .log("Hit by asteroid! Health reduced by " + ASTEROID_DAMAGE + ".")<br>
     * - If the ship is colliding with an enemy, take the appropriate damage, and if verbose,
     * .log("Hit by enemy! Health reduced by " + ENEMY_DAMAGE + ".")<br>
     * For any collisions with the ship, the colliding object should be removed.<br>
     *
     * Bullet collisions:
     * If a bullet collides with an enemy, remove both the enemy and the bullet. Also, record the
     * shot hit using recordShotHit() to track successful hits. If a Bullet collides with an
     * Asteroid, remove just the Bullet. No logging required.
     */
    public void checkCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        toRemove.addAll(checkBulletCollisions());
        toRemove.addAll(checkShipCollisions());
        spaceObjects.removeAll(toRemove); // Remove all collided objects
    }

    /**
     * Detects collisions between the Ship and other SpaceObjects.
     * Objects are considered to be colliding if they share x and y coordinates.
     * Returns any SpaceObjects which should be removed from the game.
     *
     * - If the ship is colliding with a powerup, apply the effect, and if verbose is true,
     * .log("PowerUp collected: " + obj.render())<br>
     * - If the ship is colliding with an asteroid, take the appropriate damage, and if verbose,
     * .log("Hit by asteroid! Health reduced by " + ASTEROID_DAMAGE + ".")<br>
     * - If the ship is colliding with an enemy, take the appropriate damage, and if verbose,
     * .log("Hit by enemy! Health reduced by " + ENEMY_DAMAGE + ".")<br>
     * For any collisions with the ship, the colliding object should be removed.<br>
     *
     * @return a list of all SpaceObjects which should be removed.
     * */
    private List<SpaceObject> checkShipCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            // Skip checking Ships (No ships should be in this list)
            if (obj instanceof Ship) {
                continue;
            }
            // Check Ship collision (except Bullets)
            if (isCollidingWithShip(obj.getX(), obj.getY()) && !(obj instanceof Bullet)) {
                // Handle collision effects
                switch (obj) {
                    case PowerUp powerUp -> {
                        powerUp.applyEffect(ship);
                        if (isVerbose) {
                            logger.log("PowerUp collected: " + obj.render());
                        }
                    }
                    case Asteroid asteroid -> {
                        ship.takeDamage(ASTEROID_DAMAGE);
                        if (isVerbose) {
                            logger.log("Hit by " + obj.render() + "! Health reduced by "
                                    + ASTEROID_DAMAGE + ".");
                        }
                    }
                    case Enemy enemy -> {
                        ship.takeDamage(ENEMY_DAMAGE);
                        if (isVerbose) {
                            logger.log("Hit by " + obj.render() + "! Health reduced by "
                                    + ENEMY_DAMAGE + ".");
                        }
                    }
                    default -> {
                    }
                }
                toRemove.add(obj);
            }
        }
        return toRemove;
    }

    /**
     * Detects collisions between Bullets and other SpaceObjects.
     * Objects are considered to be colliding if they share x and y coordinates.
     * Returns any SpaceObjects which should be removed from the game.
     *
     * If a bullet collides with an enemy, remove both the enemy and the bullet. Also, record the
     * shot hit using recordShotHit() to track successful hits. If a Bullet collides with an
     * Asteroid, remove just the Bullet. No logging required.
     *
     * @return a list of all SpaceObjects which should be removed.
     * */
    private List<SpaceObject> checkBulletCollisions() {
        List<SpaceObject> toRemove = new ArrayList<>();
        for (SpaceObject obj : spaceObjects) {
            // Check only Bullets
            if (!(obj instanceof Bullet)) {
                continue;
            }
            // Check Bullet collision
            for (SpaceObject other : spaceObjects) {
                // Check only Enemies
                if (other instanceof Enemy) {
                    if ((obj.getX() == other.getX()) && (obj.getY() == other.getY())) {
                        toRemove.add(obj);  // Remove bullet
                        toRemove.add(other); // Remove enemy
                        getStatsTracker().recordShotHit();
                        break;
                    }
                } else if ((other instanceof Asteroid) && (obj.getX() == other.getX())
                        && (obj.getY() == other.getY())) {
                    toRemove.add(obj);
                    // If a bullet and asteroid collide, only the bullet should be removed.
                    break;
                }
            }
        }
        return toRemove;
    }

    /**
     * Sets the seed of the Random instance created in the constructor using .setSeed().<br>
     * <p>
     * This method should NEVER be called.
     *
     * @param seed to be set for the Random instance
     * @provided
     */
    public void setRandomSeed(int seed) {
        this.random.setSeed(seed);
    }

    /**
     * Checks whether the game is over. The game ends when the ships health reaches 0.
     * @return true if the game is over, false otherwise.
     */
    public boolean checkGameOver() {
        return ship.getHealth() <= 0;
    }

    /**
     * Returns true if the given space object is within the game boundaries, and false otherwise.
     *
     * @param spaceObject the space object that's position is being checked.
     * @return whether the space object is in bounds.
     */
    public static boolean isInBounds(SpaceObject spaceObject) {
        return (spaceObject.getX() < GAME_WIDTH
                && spaceObject.getY() < GAME_HEIGHT
                && spaceObject.getX() >= 0
                && spaceObject.getY() >= 0);
    }

    /**
     * Checks if a given position would collide with the ship.
     *
     * @param x the x-coordinate to check.
     * @param y the y-coordinate to check.
     * @return true if the position collides with the ship, false otherwise.
     */
    private boolean isCollidingWithShip(int x, int y) {
        return (ship.getX() == x && ship.getY() == y);
    }

    /**
     * Checks if a given position would collide with any space object or ship.
     *
     * @param x the x-coordinate to check.
     * @param y the y-coordinate to check.
     * @return true if the position collides with any object, false otherwise.
     */
    private boolean isColliding(int x, int y) {
        for (SpaceObject spaceObject : getSpaceObjects()) {
            if ((spaceObject.getX() == x) && (spaceObject.getY() == y)) {
                return true;
            }
        }
        return isCollidingWithShip(x, y);
    }

    /**
     * Returns the logger used by the model.
     * @return the Logger currently being used.
     */
    protected Logger getLogger() {
        return this.logger;
    }
}