package game.achievements;

import java.util.*;

/**
 * Handles file input/output operations for persisting achievement events data.
 */
public interface AchievementFile {

    // The value to be used if setFileLocation is not called:
    String DEFAULT_FILE_LOCATION = "achievements.log";

    /**
     * Gets the location currently being saved to.
     */
    String getFileLocation();

    /**
     * Loads and returns all previously saved data as a list of strings.
     * @return a list of saved data entries.
     */
    List<String> read();

    /**
     * Saves the given data to a file followed by a new-line character.
     */
    void save(String data);

    /**
     * Sets the file location to save to.
     * @param fileLocation - the new file location.
     */
    void setFileLocation(String fileLocation);
}
