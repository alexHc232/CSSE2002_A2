package game.achievements;

/**
 * Represents a single achievement with progress tracking and tier information.
 */
public interface Achievement {

    /**
     * Returns "Novice" if getProgress() < 0.5, "Expert" if 0.5 <= getProgress() < 0.999,
     * and "Master" if getProgress() >=0.999.
     * @return the string tier representation of the progress.
     */
    String getCurrentTier();

    /**
     * Returns the achievement description.
     * @return a description of the achievement.
     */
    String getDescription();

    /**
     * Returns the achievement name.
     * @return the unique name of the achievement.
     */
    String getName();

    /**
     * Returns the achievement progress.
     * @return the current progress as a double between 0.0 (0%) and 1.0 (100%).
     * @ensures 0.0 <= getProgress() <= 1.0
     */
    double getProgress();

    /**
     * Sets the achievement progress to the given value.
     * @param newProgress - the updated progress.
     * @requires newProgress is between 0.0 and 1.0, inclusive.
     * @ensures getProgress() == newProgress, getProgress() <= 1.0 after the update
     * (i.e., progress is capped at 1.0)., getProgress() >= 0.0 after the update.
     */
    void setProgress(double newProgress);
}
