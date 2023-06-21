package h02;

import fopbot.Robot;
import fopbot.RobotFamily;
import fopbot.World;

import javax.naming.ldap.Control;

import static fopbot.Direction.LEFT;

/**
 * Main entry point in executing the program.
 */
public class Main {

    /**
     * Main entry point in executing the program.
     *
     * @param args program arguments, currently ignored
     */
    public static void main(String[] args) {
        int numberOfRows = 6;
        int numberOfColumns = 11;
        int[][] coins = new int[numberOfColumns][numberOfRows];
        coins[7][4] = 1;
        coins[8][1] = 1;
        coins[9][2] = 5;

        ControlCenter cc = new ControlCenter(numberOfRows, numberOfColumns);

        World.setSize(numberOfColumns,numberOfRows);
        World.setVisible(true);
        World.setDelay(300);
        World.placeBlock(0,0);
        World.placeHorizontalWall(0,0);
        World.placeVerticalWall(0,0);

        cc.placeCoinsInWorld(coins);
        Robot[] scanRobots = cc.initScanRobots();
        cc.reverseRobotArray(scanRobots);
        cc.initCleaningRobots();
    }
}
