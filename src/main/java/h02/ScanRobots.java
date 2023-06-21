package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;

public class ScanRobots extends Robot {

    public ScanRobots(int x, int y, Direction direction, int numberOfCoins) {
        super(x, y, direction, numberOfCoins, RobotFamily.SQUARE_RED);
    }

    @Override
    public void pickCoin() {
        throw new UnsupportedOperationException("Scan Robots can not pick up coins");
    }

}
