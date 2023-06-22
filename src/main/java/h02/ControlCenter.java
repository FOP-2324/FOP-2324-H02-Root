package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;
import fopbot.World;

public class ControlCenter {

    /**
     *
     * @param robot
     */
    private void spinAllRobots(Robot[] robot) {
        for(int i = 0; i < robot.length; i++) {
            robot[i].turnLeft();
            robot[i].turnLeft();
        }
    }


    /**
     *
     * @param robots
     */
    private void moveAllRobotsInArray(Robot[] robots) {
        boolean endOfWorldReached = true;
        while(endOfWorldReached) {
            for(int i = 0; i < robots.length; i++) {
                if(robots[i].isFrontClear()) {
                    robots[i].move();
                }
                endOfWorldReached &= robots[i].isFrontClear();
            }
        }
    }


    /**
     *
     * @param scanRobots
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
        moveAllRobotsInArray(scanRobots);
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
    public CleanRobot[] initCleaningRobots() {
        CleanRobot[] cleanRobots = new CleanRobot[World.getHeight()-1];
        for(int i = 0; i < World.getHeight()-1; i++) {
            cleanRobots[i] = new CleanRobot(0, i+1, Direction.RIGHT, 0);
        }
        return cleanRobots;
    }

    /**
     *
     * @param robots
     */
    public void reverseRobotArray(Robot[] robots) {
        for(int i = robots.length; i > robots.length/2; i--) {
            Robot tmp = robots[i-1];
            robots[i-1] = robots[robots.length - i];
            robots[robots.length - i] = tmp;
        }
    }

    /**
     *
     * @param coins
     */
    public void placeCoinsInWorld(int[][] coins) {
        for(int i = 0; i < coins.length; i++) {
            for(int j = 0; j < coins[i].length; j++) {
                if(coins[i][j] != 0) {
                    World.putCoins(i, j, coins[i][j]);
                }
            }
        }
    }


    /**
     *
     * @param positionsOfCoins
     * @param cleaningRobots
     */
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
        spinAllRobots(cleaningRobots);
        moveAllRobotsInArray(cleaningRobots);
        spinAllRobots(cleaningRobots);
    }

    /**
     *
     * @param robots
     */
    public void brokenRobots(Robot[] robots) {
        double p = 0.5;
        for(int i = 0; i < robots.length; i++) {
            if(Math.random() > p) {
                robots[i].turnOff();
            }
        }
    }

    /**
     *
     * @param robots
     */
    public void replaceBrokenRobots(Robot[] robots) {
        for(int i = 0; i < robots.length; i++) {
            if(robots[i].isTurnedOff()) {
                int x = robots[i].getX();
                int y = robots[i].getY();
                int numberOfCoins = robots[i].getNumberOfCoins();
                Direction direction = robots[i].getDirection();
                robots[i] = null;
                if(robots[i] instanceof  CleanRobot) {
                    robots[i] = new CleanRobot(x,y,direction,numberOfCoins);
                }
                else {
                    robots[i] = new ScanRobot(x,y,direction,numberOfCoins);
                }

            }
        }
    }

    /**
     *
     * @param coins
     * @return
     */
    public boolean allCoinsGathered(boolean[][] coins) {
        boolean allCoinsGathered = true;
        for(int i = 0; i < coins.length; i++) {
            for(int j = 0; j < coins[i].length; j++) {
                if(coins[i][j]) {
                    allCoinsGathered = false;
                }
            }
        }
        return allCoinsGathered;
    }

    /**
     *
     */
    public void cleanWorld() {
        ScanRobot[] scanRobots = initScanRobots();
        CleanRobot[] cleanRobots = initCleaningRobots();
        boolean coinsGathered = false;
        while(!coinsGathered) {
            boolean[][] coinsInWorld = scanWorld(scanRobots);
            reverseRobotArray(scanRobots);
            brokenRobots(scanRobots);
            replaceBrokenRobots(scanRobots);
            if(allCoinsGathered(coinsInWorld)) {
                break;
            }
            moveCleanRobots(coinsInWorld, cleanRobots);
            reverseRobotArray(cleanRobots);
            brokenRobots(cleanRobots);
            replaceBrokenRobots(cleanRobots);
            coinsGathered = allCoinsGathered(coinsInWorld);
        }
        System.out.println("Finished");
    }
}
