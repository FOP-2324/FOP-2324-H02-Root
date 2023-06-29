package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;

public class ControlCenter {

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
    public void invertRobots(Robot[] robots) {
        for(int i = robots.length; i > robots.length/2; i--) {
            Robot tmp = robots[i-1];
            robots[i-1] = robots[robots.length - i];
            robots[robots.length - i] = tmp;
        }
    }

    /**
     *
     * @param robots
     */
    public void rotateRobots(Robot[] robots) {
        for (Robot robot : robots) {
            robot.turnLeft();
            robot.turnLeft();
            checkForDamage(robot);
        }
    }

    /**
     *
     * @param robot
     */
    public void checkForDamage(Robot robot) {
        double p = 0.5;
        if(Math.random() > p) {
            robot.turnOff();
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
                if(isScanRobotArray(robots)) {
                    robots[i] = new ScanRobot(x,y,direction,numberOfCoins);
                }
                else {
                    robots[i] = new CleanRobot(x,y,direction,numberOfCoins);
                }
            }
        }
    }

    /**
     *
     * @param robots
     * @return
     */
    public boolean isScanRobotArray(Robot[] robots) {
        return robots instanceof ScanRobot[];
    }

    /**
     *
     * @param robots
     */
    public void spinRobots(Robot[] robots) {
        invertRobots(robots);
        rotateRobots(robots);
        replaceBrokenRobots(robots);
    }

    /**
     *
     * @param robots
     */
    public void returnRobots(Robot[] robots) {
        for (Robot robot :robots) {
            while (robot.isFrontClear()) {
                robot.move();
            }
        }
    }

    /**
     *
     * @param scanRobots
     * @return
     */
    public boolean[][] scanWorld(ScanRobot[] scanRobots) {
        boolean[][] positionsOfCoinsInWorld = new boolean[World.getWidth()][World.getHeight()];
        boolean endOfWorldReached = true;
        while(endOfWorldReached) {
            for (ScanRobot scanRobot : scanRobots) {
                if (scanRobot.isFrontClear()) {
                    if (scanRobot.isOnACoin()) {
                        int x = scanRobot.getX();
                        int y = scanRobot.getY();
                        positionsOfCoinsInWorld[x][y] = true;
                    }
                    scanRobot.move();
                }
                endOfWorldReached &= scanRobot.isFrontClear();
            }
        }
        spinRobots(scanRobots);
        returnRobots(scanRobots);
        spinRobots(scanRobots);
        return positionsOfCoinsInWorld;
    }

    /**
     *
     * @param positionsOfCoins
     * @param cleanRobots
     */
    public void moveCleanRobots(CleanRobot[] cleanRobots, boolean[][] positionsOfCoins) {
        boolean endOfWorldNotReached = true;
        while(endOfWorldNotReached) {
            for (CleanRobot cleanRobot : cleanRobots) {
                if (cleanRobot.isFrontClear()) {
                    int x = cleanRobot.getX();
                    int y = cleanRobot.getY();
                    if (positionsOfCoins[x][y]) {
                        cleanRobot.pickCoin();
                    }
                    cleanRobot.move();
                }
                endOfWorldNotReached &= cleanRobot.isFrontClear();
            }
        }
        spinRobots(cleanRobots);
        returnRobots(cleanRobots);
        spinRobots(cleanRobots);
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
            if(allCoinsGathered(coinsInWorld)) {
                break;
            }
            moveCleanRobots(cleanRobots, coinsInWorld);
            coinsGathered = allCoinsGathered(coinsInWorld);
        }
        System.out.println("Finished");
    }

    /**
     *
     * @param coins
     * @return
     */
    public boolean allCoinsGathered(boolean[][] coins) {
        boolean allCoinsGathered = true;
        for (boolean[] coin : coins) {
            for (boolean b : coin) {
                if (b) {
                    allCoinsGathered = false;
                    break;
                }
            }
        }
        return allCoinsGathered;
    }
}
