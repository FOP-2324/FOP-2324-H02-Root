package h02;

import fopbot.Robot;
import fopbot.RobotFamily;
import fopbot.World;
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

        ControlCenter cc = new ControlCenter();

        World.setSize(numberOfColumns,numberOfRows);
        World.setVisible(true);
        World.placeBlock(0,0);
        World.placeHorizontalWall(0,0);
        World.placeVerticalWall(0,0);

        cc.placeCoinsInWorld(coins);
        cc.cleanWorld();
    }

    public static void printBooleanArray(boolean[][] bool) {
        for(int i = 0; i < bool.length; i++) {
            for(int j = 0; j < bool[i].length; j++) {
                System.out.print(bool[i][j] + " ");
            }
            System.out.println();
        }
    }
}
