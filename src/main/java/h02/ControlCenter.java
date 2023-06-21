package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

public class ControlCenter {

    public final int NUMBER_OF_ROWS;
    public final int NUMBER_OF_COLUMNS;

    public ControlCenter(int numberOfRows, int numberOfColumns) {
        this.NUMBER_OF_COLUMNS = numberOfColumns;
        this.NUMBER_OF_ROWS = numberOfRows;
    }


    private void printRobotsArray(Robot[] robots) {
        for(int i = 0; i < robots.length; i++) {
            System.out.println(robots[i]);
        }
    }
    private void turnAllRobotsAround(Robot[] robot) {
        for(int i = 0; i < robot.length; i++) {
            robot[i].turnLeft();
            robot[i].turnLeft();
        }
    }

    /**
     *
     * @return
     */
    public boolean[][] scanWorld(Robot[] scanRobot) {
        boolean[][] positionsOfCoinsInWorld = new boolean[this.NUMBER_OF_ROWS][this.NUMBER_OF_COLUMNS];
        for(int i = 0; i < scanRobot.length; i++) {
            if(scanRobot[i].isFrontClear()) {
                if(scanRobot[i].isOnACoin()) {
                    int x = scanRobot[i].getX();
                    int y = scanRobot[i].getY();
                    positionsOfCoinsInWorld[x][y] = true;
                }
                scanRobot[i].move();
            }
        }
        return positionsOfCoinsInWorld;
    }

    /**
     *
     * @return
     */
    public ScanRobot[] initScanRobots() {
        ScanRobot[] scanRobot = new ScanRobot[World.getWidth()-1];
        for(int i = 0; i < World.getWidth()-1; i++) {
            scanRobot[i] = new ScanRobot(i+1, 0, Direction.UP, 0);
        }
        return scanRobot;
    }
    /**
     *
     * @return
     */
    public Robot[] initCleaningRobots() {
        CleanRobot[] cleanRobots = new CleanRobot[World.getHeight()-1];
        for(int i = 0; i < World.getHeight()-1; i++) {
            cleanRobots[i] = new CleanRobot(0, i+1, Direction.RIGHT, 0);
        }
        return cleanRobots;
    }

    public void reverseRobotArray(Robot[] robots) {
        for(int i = robots.length; i > robots.length/2; i--) {
            Robot tmp = robots[i-1];
            robots[i-1] = robots[robots.length - i];
            robots[robots.length - i] = tmp;
        }
    }

    public void placeCoinsInWorld(int[][] coins) {
        for(int i = 0; i < coins.length; i++) {
            for(int j = 0; j < coins[i].length; j++) {
                if(coins[i][j] != 0) {
                    World.putCoins(i, j, coins[i][j]);
                }
            }
        }
    }


    public void moveAllRobots(boolean[][] positionsOfCoins, Robot[] cleaningRobots) {

    }

    public void cleanWorld(boolean[][] positionsOfCoins, Robot[] cleanRobots) {

    }
}
