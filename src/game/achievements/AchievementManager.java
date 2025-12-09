package game.achievements;

import java.util.*;

/**
 * GameAchievementManager coordinates achievement updates, file persistence management.
 * This includes:
 * - registering new achievements
 * - updating achievement progress, checking for Mastered achievements and log them using
 * AchievementFile.
 * - providing access to the current list of achievements
 */
public class AchievementManager {

    private AchievementFile achievementFile;
    private List<Achievement> achievementList;

    /**
     * Constructs a GameAchievementManager with the specified AchievementFile.
     * @param achievementFile - the AchievementFile instance to use (non-null)
     * @requires achievementFile is not null
     * @throws IllegalArgumentException - if achievementFile is null.
     */
    public AchievementManager(AchievementFile achievementFile) throws IllegalArgumentException {
        if (achievementFile == null) {
            throw new IllegalArgumentException("File is null.");
        }
        this.achievementFile = achievementFile;
        this.achievementList = new ArrayList<>();
    }

    /**
     * Registers a new achievement.
     * @param achievement - the Achievement to register.
     * @throws IllegalArgumentException - if achievement is already registered.
     * @requires achievement is not null
     */
    public void addAchievement(Achievement achievement) throws IllegalArgumentException {
        if (achievementList.contains(achievement)) {
            throw new IllegalArgumentException("Achievement is already registered.");
        } else {
            achievementList.add(achievement);
        }
    }

    /**
     * Sets the progress of the specified achievement to a given amount.
     * @param achievementName - the name of the achievement.
     * @param absoluteProgressValue - the value the achievement's progress will be set to.
     * @throws IllegalArgumentException - if no achievement is registered under the provided name.
     * @requires achievementName must be a non-null, non-empty string identifying a registered
     * achievement.
     */
    public void updateAchievement(String achievementName, double absoluteProgressValue) throws
            IllegalArgumentException {
        for (Achievement achievement : achievementList) {
            if (achievement.getName().equals(achievementName)) {
                achievement.setProgress(Math.round(absoluteProgressValue * 1000.0) / 1000.0);
                return;
            }
        }
        throw new IllegalArgumentException("Achievement could not be found.");
    }

    /**
     * Checks all registered achievements. For any achievement that is mastered and has not been
     * logged yet, the event is logged via AchievementFile, and marks the achievement as logged.
     */
    public void logAchievementMastered() {
        List<String> masteredAchievements = achievementFile.read();
        for (Achievement achievement : achievementList) {
            String achievementMastery = achievement.getName() + ": Mastered";
            if (!masteredAchievements.contains(achievementMastery)) {
                achievementFile.save(achievement.getName() + ": Mastered" + '\n');
            }
        }
    }

    /**
     * Returns a list of all registered achievements.
     * @return a List of Achievement objects.
     */
    public List<Achievement> getAchievements() {
        return achievementList;
    }

}
