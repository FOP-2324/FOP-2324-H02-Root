package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

import static org.tudalgo.algoutils.student.Student.crash;

/**
 * A class that controls the {@linkplain Robot robots} and their actions.
 */
public class ControlCenter {

    /**
     * Creates a new line of {@linkplain ScanRobot ScanRobots}.
     *
     * @return An array containing the newly initialised robots
     */
    public ScanRobot[] initScanRobots() {
        // TODO: H1.1

        ScanRobot[] robots = new ScanRobot[World.getWidth() -1];
        for (int i = 1; i < World.getWidth(); i++) {
            ScanRobot tBot = new ScanRobot(i, 0, Direction.UP, 0);
            robots[i-1] = tBot;
        }

        return robots;
    }

    /**
     * Creates a new line of {@linkplain CleanRobot CleanRobots}.
     *
     * @return An array containing the newly initialised robots
     */
    public CleanRobot[] initCleaningRobots() {
        //TODO: H1.2

        CleanRobot[] robots = new CleanRobot[World.getHeight() -1];
        for (int i = 1; i < World.getHeight(); i++) {
            CleanRobot tBot = new CleanRobot(0, i, Direction.RIGHT, 0);
            robots[i-1] = tBot;
        }

        return robots;
    }

    /**
     * Inverts the given array by swapping the first and last entry, continuing with the second and second last entry and so on until the entire array has been inverted.
     *
     * @param robots The array to invert
     */
    public void reverseRobots(Robot[] robots) {
        // TODO: H3.1

         if (robots.length < 2) return;

         for (int i = 0; i < robots.length/2; i++) {
             Robot tempRobot = robots[i];
             robots[i] = robots[robots.length - (i+1)];
             robots[robots.length - (i+1)] = tempRobot;
         }
    }

    /**
     * Rotates the {@linkplain Robot robots} in the given array in ascending order and calls {@link #checkForDamage} on every {@link Robot} after its rotation.
     *
     * @param robots The array of {@linkplain Robot robots} to rotate
     */
    public void rotateRobots(Robot[] robots) {
        // TODO: H3.2

        for (Robot robot : robots) {
            robot.turnLeft();
            robot.turnLeft();
            checkForDamage(robot);
        }
    }

    /**
     * Simulates inspecting a {@link Robot} for wear, turning it off if it should no longer serve. Currently implemented as a coin flip.
     *
     * @param robot The {@link Robot} to inspect
     */
    public void checkForDamage(Robot robot) {
        final double p = 0.5;
        if (Math.random() > p) {
            robot.turnOff();
        }
    }

    /**
     * Replaces the {@linkplain Robot robots} that are turned off in the provided array with new ones. <br>
     * The method expects either an array of {@linkplain ScanRobot ScanRobots} or {@linkplain CleanRobot CleanRobots} and uses the correct class when replacing the robots.
     *
     * @param robots An array possibly containing {@linkplain Robot robots} that are turned off and need to be replaced
     */
    public void replaceBrokenRobots(Robot[] robots) {
        // TODO: H3.3

        boolean isScanArray = isScanRobotArray(robots);

        for (int i = 0; i < robots.length; i++) {
            Robot tBot = robots[i];

            if (tBot.isTurnedOff()) {
                Direction dir = tBot.getDirection();
                int coinCount = tBot.getNumberOfCoins();
                int[] pos = {tBot.getX(), tBot.getY()};

                if (isScanArray) {
                    robots[i] = new ScanRobot(pos[0], pos[1], dir, coinCount);
                } else {
                    robots[i] = new CleanRobot(pos[0], pos[1], dir, coinCount);
                }
            }
        }
    }

    /**
     * Tests whether the given array is an array of {@linkplain ScanRobot ScanRobots} or not.
     *
     * @param robots The array to test
     * @return Whether the given array is an array of {@linkplain ScanRobot ScanRobots}
     */
    public boolean isScanRobotArray(Robot[] robots) {
        return robots instanceof ScanRobot[];
    }

    /**
     * Calls {@link #reverseRobots}, {@link #rotateRobots} and {@link #replaceBrokenRobots} in that order, with the given array as the argument
     *
     * @param robots The array to perform the aforementioned actions on
     */
    public void spinRobots(Robot[] robots) {
        // TODO: H3.4

        reverseRobots(robots);
        rotateRobots(robots);
        replaceBrokenRobots(robots);
    }

    /**
     * Moves the robots to the end of the world, in ascending order and one at a time.
     *
     * @param robots The robots to move
     */
    public void returnRobots(Robot[] robots) {
        // TODO: H4.1

        for (Robot tBot : robots) {
            while (tBot.isFrontClear()) {
                tBot.move();
            }
        }
    }
    /**
     * Scans the world using the provided {@linkplain ScanRobot ScanRobots} and returns an array containing the scan results.
     *
     * @param scanRobots The robots to scan the world with
     * @return An array detailing which world fields contain at least one coin
     */
    public boolean[][] scanWorld(ScanRobot[] scanRobots) {
        // TODO: H4.2

        boolean[][] coinScanMap = new boolean[World.getWidth()][World.getHeight()];
        for (int i = 0; i < World.getWidth(); i++) {
            for (int j = 0; j < World.getHeight(); j++) {
                coinScanMap[i][j] = false;
            }
        }

        while (scanRobots[(scanRobots.length-1)].isFrontClear()) {
            for (ScanRobot tBot : scanRobots) {
                if (tBot.isOnACoin()) coinScanMap[tBot.getX()][tBot.getY()] = true;
                tBot.move();
            }
        }
        for (ScanRobot tBot : scanRobots) {
            if (tBot.isOnACoin()) coinScanMap[tBot.getX()][tBot.getY()] = true;
        }

        spinRobots(scanRobots);
        returnRobots(scanRobots);
        spinRobots(scanRobots);

        return coinScanMap;
    }

    /**
     * Performs one iteration of collecting coins, using the provided arrays to clean and determine where to clean.
     *
     * @param coinPositions An array with all the coin positions to be collected
     * @param cleanRobots   An array containing the {@linkplain CleanRobot CleanRobots} to collect the coins with.
     */
    public void moveCleanRobots(CleanRobot[] cleanRobots, boolean[][] coinPositions) {
        // TODO: H4.3

        while (cleanRobots[cleanRobots.length-1].isFrontClear()) {
            for (CleanRobot tBot : cleanRobots) {
                if (coinPositions[tBot.getX()][tBot.getY()]) {
                    tBot.pickCoin();
                }
                tBot.move();
            }
        }



        /*
        while (cleanRobots[cleanRobots.length-1].isFrontClear()) {
            for (CleanRobot tBot : cleanRobots) {
                if (coinPositions[tBot.getX()][tBot.getY()]) {
                    tBot.pickCoin();
                }
                tBot.move();
            }
        }
        */

        spinRobots(cleanRobots);
        returnRobots(cleanRobots);
        spinRobots(cleanRobots);
    }

    /**
     * Collects all the coins in the world using all the previously implemented helper methods.
     */
    public void cleanWorld() {
        ScanRobot[] scanRobots = initScanRobots();
        CleanRobot[] cleanRobots = initCleaningRobots();
        boolean coinsGathered = false;
        while (!coinsGathered) {
            boolean[][] coinsInWorld = scanWorld(scanRobots);
            if (allCoinsGathered(coinsInWorld)) {
                break;
            }
            moveCleanRobots(cleanRobots, coinsInWorld);
            coinsGathered = allCoinsGathered(coinsInWorld);
        }
        System.out.println("Finished cleaning the world!");
    }

    /**
     * Returns whether there are no coins left in the world.
     *
     * @param coins The array to search for coins
     * @return Whether the provided array contains at least one entry that is not false
     */
    public boolean allCoinsGathered(boolean[][] coins) {
        for (boolean[] coinRow : coins) {
            for (boolean b : coinRow) {
                if (b) {
                    return false;
                }
            }
        }
        return true;
    }
}
