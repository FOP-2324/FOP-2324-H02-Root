package h02.h4;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;
import h02.ControlCenter;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import java.util.Arrays;

public class H4Utils {

    static void initRobotsAndWorld(final Robot[] robots, final Direction dir, final int[][] coins) {
        for (final Robot robot : robots) {
            while (robot.getDirection() != dir) {
                robot.turnLeft();
            }
        }

        for (int y = 0; y < coins.length; y++) {
            for (int x = 0; x < coins[y].length; x++) {
                if (coins[y][x] > 0) {
                    World.putCoins(x, y, coins[y][x]);
                }
            }
        }
    }

    /**
     * Calculates the final positions of the given robots after they have walked to the end of the world in the
     * direction they're facing.
     *
     * @param robots      The robots to calculate the final positions for
     * @param worldWidth  The width of the world
     * @param worldHeight The height of the world
     * @return The final positions of the robots
     */
    static Position[] getEndOfWorldRobotPositions(final Robot[] robots, final int worldWidth, final int worldHeight) {
        return Arrays.stream(robots)
            .map((robot) -> snapPositionToWorldEdge(
                new Position(robot.getX(), robot.getY()),
                robot.getDirection(), worldWidth, worldHeight
            )).toArray(Position[]::new);
    }

    static Position[] getEndOfWorldPositions(
        final Position[] positions,
        final Direction direction,
        final int worldWidth,
        final int worldHeight
    ) {
        return Arrays.stream(positions)
            .map((position) -> snapPositionToWorldEdge(
                position,
                direction, worldWidth, worldHeight
            )).toArray(Position[]::new);
    }

    static Position snapPositionToWorldEdge(
        final Position position,
        final Direction direction,
        final int worldWidth,
        final int worldHeight
    ) {
        final var x = switch (direction) {
            case UP, DOWN -> position.x();
            case LEFT -> 0;
            case RIGHT -> worldWidth - 1;
        };

        final var y = switch (direction) {
            case UP -> worldHeight - 1;
            case DOWN -> 0;
            case LEFT, RIGHT -> position.y();
        };

        return new Position(x, y);
    }

    /**
     * Replaces the method {@link ControlCenter#spinRobots(Robot[])} and {@link ControlCenter#returnRobots(Robot[])}
     * with solution implementations to not depend on the correctness of those methods.
     *
     * @return The mocked instance
     */
    static ControlCenter mockReturnAndSpinRobots() {
        final ControlCenter controlCenter = Mockito.mock(
            ControlCenter.class,
            InvocationOnMock::callRealMethod
        );
        Mockito.doAnswer((i) -> {
            final Robot[] robots = i.getArgument(0);
            for (final Robot robot : robots) {
                robot.turnLeft();
                robot.turnLeft();
            }

            return null;
        }).when(controlCenter).spinRobots(Mockito.any());

        Mockito.doAnswer((i) -> {
            final Robot[] robots = i.getArgument(0);
            for (final Robot robot : robots) {
                while (robot.isFrontClear()) {
                    robot.move();
                }
            }

            return null;
        }).when(controlCenter).returnRobots(Mockito.any());
        return controlCenter;
    }

    record Position(int x, int y) {

        public int manhattanDistance(final Position other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }
    }
}
