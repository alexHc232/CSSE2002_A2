package game.achievements;

/**
* A concrete implementation of the Achievement interface.
 */
public class GameAchievement implements Achievement {
    private String name;
    private String description;
    private double progress;

    /**
     * Constructs an Achievement with the specified name and description. The initial progress is 0.
     * @param name - the unique name.
     * @param description - the achievement description.
     * @requires both name and description cannot be null or empty
     */
    public GameAchievement(String name, String description) {
        this.name = name;
        this.description = description;
        this.progress = 0;
    }

    /**
     * Returns the achievement name.
     * @return the unique name of the achievement.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the achievement description.
     * @return a description of the achievement.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the achievement progress.
     * @return the current progress as a double between 0.0 (0%) and 1.0 (100%).
     * @ensures 0.0 <= getProgress() <= 1.0
     */
    public double getProgress() {
        if (this.progress < 0) {
            return 0.0;
        } else if (this.progress > 1) {
            return 1.0;
        } else {
            return this.progress;
        }
    }

    /**
     * Sets the achievement progress to the given value.
     * @param newProgress - the updated progress.
     * @requires newProgress is between 0.0 and 1.0, inclusive.
     * @ensures getProgress() == newProgress, getProgress() <= 1.0 after the update
     * (i.e., progress is capped at 1.0)., getProgress() >= 0.0 after the update.
     */
    public void setProgress(double newProgress) {
        this.progress = newProgress; // Post-condition is already satisfied given precondition.
    }

    /**
     * Returns "Novice" if getProgress() < 0.5, "Expert" if 0.5 <= getProgress() < 0.999,
     * and "Master" if getProgress() >=0.999.
     * @return the string tier representation of the progress.
     */
    public String getCurrentTier() {
        if (this.progress < 0.5) {
            return "Novice";
        } else if (0.5 <= this.progress && this.progress < 0.999) {
            return "Expert";
        } else {
            return "Master";
        }
    }
}
