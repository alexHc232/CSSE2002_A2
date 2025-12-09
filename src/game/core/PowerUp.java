package game.core;

/**
 * Represents a power-up in the game.
 */
public abstract class PowerUp extends ObjectWithPosition implements PowerUpEffect {

    /**
     * Creates a new PowerUp with the given coordinate.
     *
     * @param x the given x coordinate
     * @param y the given y coordinate
     */
    public PowerUp(int x, int y) {
        super(x, y);
    }

    /**
     * Moves the PowerUp down by 1, whenever the tick is a multiple of 10.
     *
     * @param tick the given game tick.
     */
    public void tick(int tick) {
        if (tick % 10 == 0) {
            y++;
        }
    }
}
