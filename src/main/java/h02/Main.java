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
        int numberOfRows = 6;
        int numberOfColumns = 11;
        int[][] coins = new int[numberOfColumns][numberOfRows];
        coins[7][4] = 1;
        coins[8][1] = 1;
        coins[9][2] = 2;

        ControlCenter controlCenter = new ControlCenter();

        World.setSize(numberOfColumns, numberOfRows);
        World.setVisible(true);
        World.setDelay(10);
        World.placeBlock(0, 0);
        World.placeHorizontalWall(0, 0);
        World.placeVerticalWall(0, 0);

        placeCoinsInWorld(coins);
        controlCenter.cleanWorld();
    }

    /**
     * Places coins in the world according to the provided array
     *
     * @param coins An array detailing how many coins to place in what position
     */
    public static void placeCoinsInWorld(int[][] coins) {
        for (int i = 0; i < coins.length; i++) {
            for (int j = 0; j < coins[i].length; j++) {
                if (coins[i][j] != 0) {
                    World.putCoins(i, j, coins[i][j]);
                }
            }
        }
    }
}
