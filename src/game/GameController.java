package game;

import game.achievements.Achievement;
import game.achievements.AchievementManager;
import game.achievements.GameAchievement;
import game.achievements.PlayerStatsTracker;
import game.core.SpaceObject;
import game.ui.UI;
import game.utility.Direction;

import java.util.*;
import java.util.function.Consumer;

/**
 * The Controller handling the game flow and interactions.
 * <p>
 * Holds references to the UI and the Model, so it can pass information and references back and forth as necessary.<br>
 * Manages changes to the game, which are stored in the Model, and displayed by the UI.<br>
 */
public class GameController {
    private final long startTime;
    private final UI ui;
    private final GameModel model;
    private final AchievementManager achievementManager;

    /**
     * An internal variable indicating whether certain methods should log their actions.
     * Not all methods respect isVerbose.
     */
    private boolean isVerbose = false;

    private final Map<String, Consumer<Achievement>> refreshMethods = new HashMap<>();

    private boolean isPaused = false;

    /**
     * Initializes the game controller with the given UI, GameModel and AchievementManager.<br>
     * Stores the UI, GameModel, AchievementManager and start time.<br>
     * The start time System.currentTimeMillis() should be stored as a long.<br>
     * Starts the UI using UI.start().<br>
     *
     * @param ui the UI used to draw the Game
     * @param model the model used to maintain game information
     * @param achievementManager the manager used to maintain achievement information
     *
     * @requires ui is not null
     * @requires model is not null
     * @requires achievementManager is not null
     * @provided
     */
    public GameController(UI ui, GameModel model, AchievementManager achievementManager) {
        this.ui = ui;
        ui.start();
        this.model = model;
        this.startTime = System.currentTimeMillis(); // Current time
        this.achievementManager = achievementManager;

        refreshMethods.put("Survivor", this::refreshSurvivor);
        refreshMethods.put("Enemy Exterminator", this::refreshEnemyExterminator);
        refreshMethods.put("Sharp Shooter", this::refreshSharpShooter);
    }


    /**
     * Initializes the game controller with the given UI and GameModel.<br>
     * Stores the ui, model and start time.<br>
     * The start time System.currentTimeMillis() should be stored as a long.<br>
     *
     * @param ui    the UI used to draw the Game
     * @param achievementManager the manager used to maintain achievement information
     *
     * @requires ui is not null
     * @requires achievementManager is not null
     * @provided
     */
    public GameController(UI ui, AchievementManager achievementManager) {
        this(ui, new GameModel(ui::log, new PlayerStatsTracker()), achievementManager);
    }

    /**
     * Starts the main game loop.<br>
     * <p>
     * Passes onTick and handlePlayerInput to ui.onStep and ui.onKey respectively.
     * @provided
     */
    public void startGame() {
        ui.onStep(this::onTick);
        ui.onKey(this::handlePlayerInput);
    }

    /** Returns the player stats tracker being used. */
    public PlayerStatsTracker getStatsTracker() {
        return model.getStatsTracker();
    }

    /** Returns the current game model */
    public GameModel getModel() {
        return model;
    }

    /**
     * Uses the provided tick to call and advance the following:<br>
     * - A call to model.updateGame(tick) to advance the game by the given tick.<br>
     * - A call to model.checkCollisions() to handle game interactions.<br>
     * - A call to model.spawnObjects() to handle object creation.<br>
     * - A call to model.levelUp() to check and handle leveling.<br>
     * - A call to refreshAchievements(tick) to handle achievement updating.<br>
     * - A call to renderGame() to draw the current state of the game.<br>
     *  - Checks if the game is over, and if so calls showGameOverWindow() to display the window.
     * @param tick the provided tick
     * @provided
     */
    public void onTick(int tick) {
        model.updateGame(tick); // Update GameObjects
        model.checkCollisions(); // Check for Collisions
        model.spawnObjects(); // Handles new spawns
        model.levelUp(); // Level up when score threshold is met
        refreshAchievements(tick); // Handle achievement updating.
        renderGame(); // Update Visual

        // Check game over
        if (model.checkGameOver()) {
            pauseGame();
            showGameOverWindow();
        }
    }

    /**
     * Displays a Game Over window containing the player's final statistics and achievement
     * progress.<br>
     * <p>
     * This window includes:<br>
     * - Number of shots fired and shots hit<br>
     * - Number of Enemies destroyed<br>
     * - Survival time in seconds<br>
     * - Progress for each achievement, including name, description, completion percentage
     * and current tier<br>
     * @provided
     */
    private void showGameOverWindow() {

        // Create a new window to display game over stats.
        javax.swing.JFrame gameOverFrame = new javax.swing.JFrame("Game Over - Player Stats");
        gameOverFrame.setSize(400, 300);
        gameOverFrame.setLocationRelativeTo(null); // center on screen
        gameOverFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);


        StringBuilder sb = new StringBuilder();
        sb.append("Shots Fired: ").append(getStatsTracker().getShotsFired()).append("\n");
        sb.append("Shots Hit: ").append(getStatsTracker().getShotsHit()).append("\n");
        sb.append("Enemies Destroyed: ").append(getStatsTracker().getShotsHit()).append("\n");
        sb.append("Survival Time: ").append(getStatsTracker().getElapsedSeconds())
                .append(" seconds\n");

        List<Achievement> achievements = achievementManager.getAchievements();
        for (Achievement ach : achievements) {
            double progressPercent = ach.getProgress() * 100;
            sb.append(ach.getName())
                    .append(" - ")
                    .append(ach.getDescription())
                    .append(" (")
                    .append(String.format("%.0f%%", progressPercent))
                    .append(" complete, Tier: ")
                    .append(ach.getCurrentTier())
                    .append(")\n");
        }

        String statsText = sb.toString();

        // Create a text area to show stats.
        javax.swing.JTextArea statsArea = new javax.swing.JTextArea(statsText);
        statsArea.setEditable(false);
        statsArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));

        // Add the text area to a scroll pane (optional) and add it to the frame.
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(statsArea);
        gameOverFrame.add(scrollPane);

        // Make the window visible.
        gameOverFrame.setVisible(true);
    }

    /**
     * Renders the current game state. This includes updating the score, health, level, ship
     * position and time survived, as well as rendering all SpaceObjects.
     */
    public void renderGame() {
        // Updating level, score, health and time survived to the current statistics.
        ui.setStat("Level", Integer.toString(model.getLevel()));
        ui.setStat("Score", Integer.toString(model.getShip().getScore()));
        ui.setStat("Health", Integer.toString(model.getShip().getHealth()));
        ui.setStat("Time Survived", getStatsTracker().getElapsedSeconds()
                + " seconds");

        // Instantiates a local list to store all SpaceObjects as well as the ship
        List<SpaceObject> allSpaceObjects = new ArrayList<>(model.getSpaceObjects());
        allSpaceObjects.add(model.getShip());
        ui.render(allSpaceObjects);
    }

    /** Pauses the game until the method is called again */
    public void pauseGame() {
        ui.pause();
        isPaused = !isPaused;
        if (isPaused) {
            model.getLogger().log("Game paused.");
        } else {
            model.getLogger().log("Game unpaused.");
        }
    }

    /**
     * Handles players input to perform a variety of actions, including moving the ship, firing
     * bullets and pausing the game.
     * @param input the player's input command
     */
    public void handlePlayerInput(String input) {
        // Creating a new Map, mapping each directional input to their respective Direction
        Map<String, Direction> movementMap = new HashMap<>();
        movementMap.put("W", Direction.UP);
        movementMap.put("w", Direction.UP);
        movementMap.put("A", Direction.LEFT);
        movementMap.put("a", Direction.LEFT);
        movementMap.put("S", Direction.DOWN);
        movementMap.put("s", Direction.DOWN);
        movementMap.put("D", Direction.RIGHT);
        movementMap.put("d", Direction.RIGHT);

        // Creates a list of all directional inputs, to check if input is a movement
        List<String> movementInputs = Arrays.asList("W", "w", "A", "a", "S", "s", "D", "d");

        // Performs relevant action depending on if input is to move, fire bullet, pause or invalid,
        // only if the game is not paused.
        if (!isPaused) {
            if (movementInputs.contains(input)) {
                model.getShip().move(movementMap.get(input));
                if (isVerbose) {
                    model.getLogger().log("Ship moved to (" + model.getShip().getX()
                            + ", " + model.getShip().getY() + ")");
                }
            } else if (input.equals("F") || input.equals("f")) {
                model.fireBullet();
                model.getStatsTracker().recordShotFired();
            } else {
                model.getLogger().log("Invalid input. Use W, A, S, D, F, or P.");
            }
        }
        if (input.equals("P") || input.equals("p")) {
            pauseGame();
        }
    }

    /**
     * Refreshes the Sharp Shooter achievement, by setting its progress to the relevant value
     * (the shooting accuracy) if more than 10 shots have been fired.
     * @param achievement the Sharp Shooter achievement being refreshed.
     */
    private void refreshSharpShooter(Achievement achievement) {
        double progress = getStatsTracker().getAccuracy() / 0.99;
        if (getStatsTracker().getShotsFired() > 10) {
            if (progress > 1) {
                achievement.setProgress(1.0);
            } else {
                achievementManager.updateAchievement(achievement.getName(), progress);
            }
        }
    }

    /**
     * Refreshes the Survivor achievement, by setting its progress to the relevant value,
     * depending on the survival time since the start of the game.
     * @param achievement the Survivor achievement being refreshed.
     */
    private void refreshSurvivor(Achievement achievement) {
        double progress = getStatsTracker().getElapsedSeconds() / 120.0;
        if (progress > 1) {
            achievement.setProgress(1.0);
        } else {
            achievementManager.updateAchievement(achievement.getName(), progress);
        }
    }

    /**
     * Refreshes the Enemy Exterminator achievement, by setting its progress to the relevant value,
     * depending on the number of shots hit in the current game.
     * @param achievement the Enemy Exterminator achievement being refreshed.
     */
    private void refreshEnemyExterminator(Achievement achievement) {
        double progress = getStatsTracker().getShotsHit() / 20.0;
        if (progress > 1) {
            achievement.setProgress(1.0);
        } else {
            achievementManager.updateAchievement(achievement.getName(), progress);
        }
    }

    /**
     * Updates the player's progress towards achievements on every game tick, and uses the
     * achievementManager to track and update the player's achievements.
     */
    public void refreshAchievements(int tick) {
        for (Achievement achievement : achievementManager.getAchievements()) {
            if (achievement.getProgress() < 1) {
                // Refreshes all achievements which are not yet mastered.
                Consumer<Achievement> refreshMethod = refreshMethods.get(achievement.getName());
                refreshMethod.accept(achievement);

                // Checks if they have now become mastered, and if so logs this.
                if (achievement.getProgress() == 1) {
                    achievementManager.logAchievementMastered();
                    // If isVerbose is true, new achievement mastery should be logged to the UI.
                    if (isVerbose) {
                        ui.logAchievementMastered(achievement.getName());
                    }
                }
            }
            // Updates the UI with all the achievement statistics every tick.
            ui.setStat(achievement.getName(), String.valueOf(achievement.getProgress()));

            // Logs the achievements to the UI every 100 ticks, if verbose.
            if (isVerbose && tick % 100 == 0) {
                ui.logAchievements(achievementManager.getAchievements());
            }
        }
    }

    /**
     * Sets the Game Controller to verbose, and updates the Game Model to be verbose.
     */
    public void setVerbose(boolean verbose) {
        isVerbose = verbose;
        model.setVerbose(verbose);
    }
}

