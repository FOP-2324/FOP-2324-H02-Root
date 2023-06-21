package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;

public class ScanRobot extends Robot {

    public ScanRobot(int x, int y, Direction direction, int numberOfCoins) {
        super(x, y, direction, numberOfCoins, RobotFamily.SQUARE_RED);
    }

    @Override
    public void pickCoin() {
        throw new UnsupportedOperationException("This robot is unable to pick up coins!");
    }

    @Override
    public String toString() {
        return "ScanRobot{"
            + "id='" + getId() + '\''
            + ", at=[" + getX() + '/' + getY()
            + "], numberOfCoins=" + getNumberOfCoins()
            + ", direction=" + getDirection()
            + '}';
    }

}
