package game.achievements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileInputStream;

/**
 * A concrete implementation of AchievementFile using standard file I/O.
 */
public class FileHandler implements AchievementFile {

    private String fileLocation;

    /**
     * Constructs a new FileHandler and sets the file location to the default location.
     */
    public FileHandler() {
        this.fileLocation = DEFAULT_FILE_LOCATION;
    }

    /**
     * Gets the location currently being saved to.
     */
    public String getFileLocation() {
        return this.fileLocation;
    }

    /**
     * Loads and returns all previously saved data as a list of strings.
     * @return a list of saved data entries.
     */
    public List<String> read() {
        String inputRead = readData();

        // Initialise a list of Strings to add the data to.
        List<String> returnData = new ArrayList<>();

        if (inputRead.isEmpty()) {
            return returnData;
        }

        // Splits the data into separate strings, and adds them to the return list.
        String[] dataRead = inputRead.split("\n");
        returnData.addAll(Arrays.asList(dataRead));

        return returnData;
    }

    /**
     * Reads all the data from the file location, and returns it as one string.
     * @return the string representation of all the data in the fileLocation.
     */
    private String readData() {
        FileInputStream inputStream = null;
        String inputRead = "";

        try {
            // Creates a new input stream depending on the file location, and attempts to read
            // all data, while catching any exceptions that occur.
            inputStream = new FileInputStream(this.fileLocation);
            byte[] data = inputStream.readAllBytes();
            inputRead = new String(data);
        } catch (FileNotFoundException fileNotFound) {
            System.out.println("File could not be found.");
        } catch (IOException e) {
            System.out.println("I/O error occurred.");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    System.out.println("Error occurred closing the file input stream.");
                }
            }
        }
        return inputRead;
    }

    /**
     * Saves the given data to a file followed by a new-line character.
     */
    public void save(String data) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(this.fileLocation, true);
            for (char letter : data.toCharArray()) {
                fileOutputStream.write(letter);
            }
            fileOutputStream.write('\n');
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found.");
        } catch (IOException e) {
            System.out.println("I/O error occurred.");
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    System.out.println("Error occurred closing the file output stream.");
                }
            }
        }
    }

    /**
     * Sets the file location to save to.
     * @param fileLocation - the new file location.
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }
}
