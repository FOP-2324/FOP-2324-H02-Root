package h02;

import fopbot.World;

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
        // define world dimensions
        int numberOfRows = 15;
        int numberOfColumns = 16;
        // create an array for filling the world with some coins
        int[][] coins = new int[numberOfRows][numberOfColumns];
        // add some entries to the array using the provided method that provides a mysterious pattern
        fillArray(coins);

        // // for a smaller example, simply uncomment the following code
        // numberOfRows = 6;
        // numberOfColumns = 11;
        // coins = new int[numberOfRows][numberOfColumns];
        // coins[4][7] = 1;
        // coins[1][8] = 1;
        // coins[2][9] = 2;

        // create a control center
        ControlCenter controlCenter = new ControlCenter();
        // set the world dimensions
        World.setSize(numberOfColumns, numberOfRows);
        // don't draw turned off robots
        World.getGlobalWorld().setDrawTurnedOffRobots(false);
        // open world, set its delay and draw some decorations
        World.setVisible(true);
        World.setDelay(30); // adjust this value if the world runs too fast/slow for your liking
        World.placeBlock(0, 0);
        World.placeHorizontalWall(0, 0);
        World.placeVerticalWall(0, 0);
        // use the previously created array to fill the world with coins
        placeCoinsInWorld(coins);
        // let the robots clean up the mess
        controlCenter.cleanWorld();
    }

    /**
     * Places coins in the world according to the provided array
     *
     * @param coins An array detailing how many coins to place in what position
     */
    public static void placeCoinsInWorld(int[][] coins) {
        for (int y = 0; y < coins.length; y++) {
            for (int x = 0; x < coins[y].length; x++) {
                if (coins[y][x] != 0) {
                    World.putCoins(x, y, coins[y][x]);
                }
            }
        }
    }

    /**
     * Fills the given array with a mysterious pattern that represents coins in the world.
     * The array should be at least 13x13 cells big.
     *
     * @param coins the array to fill
     */
    public static void fillArray(int[][] coins) {
        coins[12][11] = 3;
        coins[12][4] = 3;
        coins[11][10] = 3;
        coins[11][5] = 3;
        coins[11][12] = 2;
        coins[11][3] = 2;

        coins[10][9] = 3;
        coins[10][6] = 3;
        coins[10][8] = 1;
        coins[10][7] = 1;

        coins[8][5] = 4;
        coins[8][10] = 4;

        coins[5][7] = 4;
        coins[5][8] = 4;
        coins[4][6] = 4;
        coins[4][9] = 4;
        coins[4][5] = 4;
        coins[4][10] = 4;
        coins[5][4] = 4;
        coins[5][11] = 4;
        coins[6][4] = 4;
        coins[6][11] = 4;

        coins[10][2] = 2;
        coins[9][2] = 1;
        coins[8][2] = 1;
        coins[7][2] = 1;
        coins[6][2] = 1;
        coins[5][2] = 1;
        coins[4][3] = 1;
        coins[3][4] = 1;
        coins[2][5] = 1;
        coins[2][6] = 1;
        coins[2][7] = 1;
        coins[2][8] = 1;
        coins[2][9] = 1;
        coins[2][10] = 1;
        coins[3][11] = 1;
        coins[4][12] = 1;
        coins[5][13] = 1;
        coins[6][13] = 1;
        coins[7][13] = 1;
        coins[8][13] = 1;
        coins[9][13] = 1;
        coins[10][13] = 2;
    }
}
