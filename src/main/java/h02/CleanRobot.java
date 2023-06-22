package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;

public class CleanRobot extends Robot {
    public CleanRobot(int x, int y, Direction direction, int numberOfCoins) {
        super(x, y, direction, numberOfCoins, RobotFamily.SQUARE_BLACK);
    }

    @Override
    public boolean isOnACoin() {
        throw new UnsupportedOperationException("This robot is unable check for coins!");
    }

    @Override
    public String toString() {
        return "CleanRobot{"
            + "id='" + getId() + '\''
            + ", at=[" + getX() + '/' + getY()
            + "], numberOfCoins=" + getNumberOfCoins()
            + ", direction=" + getDirection()
            + '}';
    }

}
