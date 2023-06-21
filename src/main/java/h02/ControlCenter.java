package h02;

import fopbot.Robot;

public class ControlCenter {

    public final int NUMBER_OF_ROWS;
    public final int NUMBER_OF_COLUMNS;

    public ControlCenter(int numberOfRows, int numberOfColumns) {
        this.NUMBER_OF_COLUMNS = numberOfColumns;
        this.NUMBER_OF_ROWS = numberOfRows;
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
    public boolean[][] findPositionsOfCoinsInWorld(Robot[] scanRobots) {
        return null;
    }

    /**
     *
     * @param positionsOfCoins
     * @return
     */
    public Robot[] initializeCleaningRobots(boolean[][] positionsOfCoins) {
        return null;
    }


    public void moveAllRobots(boolean[][] positionsOfCoins, Robot[] cleaningRobots) {

    }

    public void cleanWorld(boolean[][] positionsOfCoins, Robot[] cleanRobots) {

    }
}
