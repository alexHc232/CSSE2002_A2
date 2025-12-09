package game;

import game.achievements.Achievement;
import game.achievements.AchievementManager;
import game.achievements.FileHandler;

import game.achievements.PlayerStatsTracker;
import game.core.SpaceObject;
import game.ui.KeyHandler;
import game.ui.Tickable;
import game.ui.UI;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ControllerTests {
    GameController gameController;
    TestUI testUI;
    PlayerStatsTracker playerStatsTracker;
    GameModel gameModel;

    @Before
    public void setUp() {
        testUI = new TestUI();
        playerStatsTracker = new PlayerStatsTracker();
        gameModel = new GameModel(testUI::log, playerStatsTracker);
        gameController = new GameController(testUI, gameModel,
                new AchievementManager(new FileHandler()));
    }

    @Test
    public void testPausedHandling() {
        int startingY = gameController.getModel().getShip().getY();
        gameController.setVerbose(true);

        // Checks that the player cannot move when the game is paused
        gameController.pauseGame();
        gameController.handlePlayerInput("W");
        Assert.assertEquals(startingY, gameController.getModel().getShip().getY());
        Assert.assertEquals("", testUI.message);

        // Checks that the player can move correctly when the game is unpaused.
        gameController.handlePlayerInput("W");
        Assert.assertEquals(startingY - 1, gameController.getModel().getShip().getY());
    }


    @Test
    public void testVerboseHandling() {
        gameController.setVerbose(true);
        gameController.handlePlayerInput("W");
        Assert.assertTrue(testUI.messageLogged);
        Assert.assertEquals(testUI.message, "Ship moved to ("
                + gameController.getModel().getShip().getX() + ", "
                + gameController.getModel().getShip().getY() + ")");
    }

    @Test
    public void testNotVerboseHandling() {
        gameController.handlePlayerInput("W");
        Assert.assertFalse(testUI.messageLogged);
    }

    @Test
    public void testInvalidInputHandling() {
        gameController.handlePlayerInput("L");
        Assert.assertTrue(testUI.messageLogged);
        Assert.assertEquals("Invalid input. Use W, A, S, D, F, or P.", testUI.message);
    }

    @Test
    public void testPauseHandling() {
        gameController.handlePlayerInput("P");
        Assert.assertTrue(testUI.messageLogged);
        // The pauseGame() method logs this message, so it can be used to check if it was called:
        Assert.assertEquals("Game paused.", testUI.message);
    }

    @Test
    public void testGamePause() {
        gameController.pauseGame();
        Assert.assertTrue(testUI.paused);
        Assert.assertEquals("Game paused.", testUI.message);
        gameController.pauseGame();
        Assert.assertFalse(testUI.paused);
        Assert.assertEquals("Game unpaused.", testUI.message);
    }

    @Test
    public void testSetVerbose() {
        // Checks verbose is false, as no message is logged
        gameController.handlePlayerInput("W");
        Assert.assertFalse(testUI.messageLogged);

        // Checks that setVerbose correctly changes the verbosity to true, as the message should
        // now be logged
        gameController.setVerbose(true);
        gameController.handlePlayerInput("W");
        Assert.assertTrue(testUI.messageLogged);
    }

    @Test
    public void testGetStatsTracker() {
        Assert.assertEquals(playerStatsTracker, gameController.getStatsTracker());
    }

    @Test
    public void testGetModel() {
        Assert.assertEquals(gameModel, gameController.getModel());
    }
}

class TestUI implements UI {
    boolean messageLogged = false;
    String message = "";
    boolean paused = false;

    public void log(String message) {
        this.messageLogged = true;
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
