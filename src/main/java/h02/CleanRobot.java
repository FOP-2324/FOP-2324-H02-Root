package h02;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.RobotFamily;

public class CleanRobot extends Robot {
    public CleanRobot(int x, int y, Direction direction, int numberOfCoins) {
        super(x, y, direction, numberOfCoins, RobotFamily.SQUARE_ORANGE);
    }

    @Override
    public boolean isOnACoin() {
        throw new UnsupportedOperationException("CleanRobots can not check if they are on a coin");
    }
}
