package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

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
        // H1.1
        // create array with correct size
        final ScanRobot[] scanRobots = new ScanRobot[World.getWidth() - 1];
        // fill array
        for (int i = 0; i < World.getWidth() - 1; i++) {
            scanRobots[i] = new ScanRobot(i + 1, 0, Direction.UP, 0);
        }
        return scanRobots;
    }

    /**
     * Creates a new line of {@linkplain CleanRobot CleanRobots}.
     *
     * @return An array containing the newly initialised robots
     */
    public CleanRobot[] initCleaningRobots() {
        // H1.2
        // create array with correct size
        final CleanRobot[] cleanRobots = new CleanRobot[World.getHeight() - 1];
        // fill array
        for (int i = 0; i < World.getHeight() - 1; i++) {
            cleanRobots[i] = new CleanRobot(0, i + 1, Direction.RIGHT, 0);
        }
        return cleanRobots;
    }

    /**
     * Inverts the given array by swapping the first and last entry, continuing with the second and second last entry
     * and so on until the entire array has been inverted.
     *
     * @param robots The array to invert
     */
    public void reverseRobots(final Robot[] robots) {
        // H3.1
        // only walk half of the array
        for (int i = robots.length; i > robots.length / 2; i--) {
            // swap two robots
            final Robot tmp = robots[i - 1];
            robots[i - 1] = robots[robots.length - i];
            robots[robots.length - i] = tmp;
        }
    }

    /**
     * Rotates the {@linkplain Robot robots} in the given array in ascending order and calls {@link #checkForDamage} on
     * every {@link Robot} after its rotation.
     *
     * @param robots The array of {@linkplain Robot robots} to rotate
     */
    public void rotateRobots(final Robot[] robots) {
        // H3.2
        // this construct is useful here because we do not care about the index of the robot being rotated
        for (final Robot robot : robots) {
            robot.turnLeft();
            robot.turnLeft();
            checkForDamage(robot);
        }
    }

    /**
     * Simulates inspecting a {@link Robot} for wear, turning it off if it should no longer serve. Currently implemented
     * as a coin flip.
     *
     * @param robot The {@link Robot} to inspect
     */
    public void checkForDamage(final Robot robot) {
        final double p = 0.5;
        if (Math.random() > p) {
            robot.turnOff();
        }
    }

    /**
     * Replaces the {@linkplain Robot robots} that are turned off in the provided array with new ones. <br>
     * The method expects either an array of {@linkplain ScanRobot ScanRobots} or {@linkplain CleanRobot CleanRobots}
     * and uses the correct class when replacing the robots.
     *
     * @param robots An array possibly containing {@linkplain Robot robots} that are turned off and need to be replaced
     */
    public void replaceBrokenRobots(final Robot[] robots) {
        // H3.3
        for (int i = 0; i < robots.length; i++) {
            if (robots[i].isTurnedOff()) {
                // save all important attributes
                final int x = robots[i].getX();
                final int y = robots[i].getY();
                final int numberOfCoins = robots[i].getNumberOfCoins();
                final Direction direction = robots[i].getDirection();
                // create new robot with the previously saved attributes
                if (isScanRobotArray(robots)) {
                    robots[i] = new ScanRobot(x, y, direction, numberOfCoins);
                } else {
                    robots[i] = new CleanRobot(x, y, direction, numberOfCoins);
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
    public boolean isScanRobotArray(final Robot[] robots) {
        return robots instanceof ScanRobot[];
    }

    /**
     * Calls {@link #reverseRobots}, {@link #rotateRobots} and {@link #replaceBrokenRobots} in that order, with the
     * given array as the argument.
     *
     * @param robots The array to perform the aforementioned actions on
     */
    public void spinRobots(final Robot[] robots) {
        // H3.4
        reverseRobots(robots);
        rotateRobots(robots);
        replaceBrokenRobots(robots);
    }

    /**
     * Moves the robots to the end of the world, in ascending order and one at a time.
     *
     * @param robots The robots to move
     */
    public void returnRobots(final Robot[] robots) {
        // H4.1
        for (final Robot robot : robots) {
            while (robot.isFrontClear()) {
                robot.move();
            }
        }
    }

    /**
     * Scans the world using the provided {@linkplain ScanRobot ScanRobots} and returns an array containing the scan results.
     *
     * @param scanRobots The robots to scan the world with
     * @return An array detailing which world fields contain at least one coin
     */
    public boolean[][] scanWorld(final ScanRobot[] scanRobots) {
        // H4.2
        // booleans are initialised as false in arrays
        final boolean[][] coinPositions = new boolean[World.getHeight()][World.getWidth()];
        boolean allAtEndOfWorld = false;
        while (!allAtEndOfWorld) {
            for (final ScanRobot scanRobot : scanRobots) {
                // check whether the end of the world has been reached
                if (!scanRobot.isFrontClear()) {
                    allAtEndOfWorld = true;
                    continue;
                }
                // if not move and potentially deal with a found coin
                scanRobot.move();
                if (scanRobot.isOnACoin()) {
                    final int x = scanRobot.getX();
                    final int y = scanRobot.getY();
                    coinPositions[y][x] = true;
                }
            }
        }
        // if the while loop has terminated, the end of the world has been reached, so spin the robots and return
        spinRobots(scanRobots);
        returnRobots(scanRobots);
        spinRobots(scanRobots);
        return coinPositions;
    }

    /**
     * Performs one iteration of collecting coins, using the provided arrays to clean and determine where to clean.
     *
     * @param coinPositions An array with all the coin positions to be collected
     * @param cleanRobots   An array containing the {@linkplain CleanRobot CleanRobots} to collect the coins with.
     */
    public void moveCleanRobots(final CleanRobot[] cleanRobots, final boolean[][] coinPositions) {
        //H4.3
        boolean allAtEndOfWorld = false;
        // very similar to scanWorld, just collect coins if needed instead of adding an array entry
        while (!allAtEndOfWorld) {
            for (final CleanRobot cleanRobot : cleanRobots) {
                if (!cleanRobot.isFrontClear()) {
                    allAtEndOfWorld = true;
                    continue;
                }
                cleanRobot.move();
                final int x = cleanRobot.getX();
                final int y = cleanRobot.getY();
                if (coinPositions[y][x]) {
                    cleanRobot.pickCoin();
                }
            }
        }
        spinRobots(cleanRobots);
        returnRobots(cleanRobots);
        spinRobots(cleanRobots);
    }

    /**
     * Collects all the coins in the world using all the previously implemented helper methods.
     */
    public void cleanWorld() {
        final ScanRobot[] scanRobots = initScanRobots();
        final CleanRobot[] cleanRobots = initCleaningRobots();
        boolean coinsGathered = false;
        while (!coinsGathered) {
            final boolean[][] coinsInWorld = scanWorld(scanRobots);
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
    public boolean allCoinsGathered(final boolean[][] coins) {
        for (final boolean[] coinRow : coins) {
            for (final boolean b : coinRow) {
                if (b) {
                    return false;
                }
            }
        }
        return true;
    }
}
