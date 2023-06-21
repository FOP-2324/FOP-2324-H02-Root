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
    private void spinAllRobots(Robot[] robot) {
        for(int i = 0; i < robot.length; i++) {
            robot[i].turnLeft();
            robot[i].turnLeft();
        }
    }

    /**
     *
     * @return
     */
    public boolean[][] scanWorld(Robot[] scanRobots) {
        boolean[][] positionsOfCoinsInWorld = new boolean[World.getWidth()][World.getHeight()];
        boolean endOfWorldReached = true;
        while(endOfWorldReached) {
            for(int i = 0; i < scanRobots.length; i++) {
                if(scanRobots[i].isFrontClear()) {
                    if(scanRobots[i].isOnACoin()) {
                        int x = scanRobots[i].getX();
                        int y = scanRobots[i].getY();
                        positionsOfCoinsInWorld[x][y] = true;
                    }
                    scanRobots[i].move();
                }
                endOfWorldReached &= scanRobots[i].isFrontClear();
            }
        }
        spinAllRobots(scanRobots);
        return positionsOfCoinsInWorld;
    }

    /**
     *
     * @return
     */
    public ScanRobot[] initScanRobots() {
        ScanRobot[] scanRobots = new ScanRobot[World.getWidth()-1];
        for(int i = 0; i < World.getWidth()-1; i++) {
            scanRobots[i] = new ScanRobot(i+1, 0, Direction.UP, 0);
        }
        return scanRobots;
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


    public void moveCleanRobots(boolean[][] positionsOfCoins, Robot[] cleaningRobots) {
        boolean endOfWorldNotReached = true;
        while(endOfWorldNotReached) {
            for(int i = 0; i < cleaningRobots.length; i++) {
                if(cleaningRobots[i].isFrontClear()) {
                    int x = cleaningRobots[i].getX();
                    int y = cleaningRobots[i].getY();
                    if(positionsOfCoins[x][y]) {
                        cleaningRobots[i].pickCoin();
                    }
                    cleaningRobots[i].move();
                }
                endOfWorldNotReached &= cleaningRobots[i].isFrontClear();
            }
        }
        this.spinAllRobots(cleaningRobots);
    }

    public void replaceNullRobots(Robot[] robots) {
        for(int i = 0; i < robots.length; i++) {
            if(robots[i] == null) {
                int x = robots[i].getX();
                int y = robots[i].getY();
                int numberOfCoins = robots[i].getNumberOfCoins();
                Direction direction = robots[i].getDirection();
                robots[i] = new Robot(x,y,direction,numberOfCoins);
            }
        }
    }

    public void cleanWorld(boolean[][] positionsOfCoins, Robot[] cleanRobots) {

    }
}
