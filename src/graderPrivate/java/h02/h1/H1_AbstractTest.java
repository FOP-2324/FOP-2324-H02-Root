package h02.h1;

import fopbot.Direction;
import fopbot.Robot;
import h02.CleanRobot;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.ScanRobot;
import h02.TestUtils;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.HashSet;
import java.util.function.Function;

@SkipAfterFirstFailedTest(TestUtils.SKIP_AFTER_FIRST_FAILED_TEST)
public abstract class H1_AbstractTest implements IWorldSetup {

    /**
     * Tests that the array length is correct.
     *
     * @param worldSize the world size
     * @param context   the test context
     * @param robotType the robot Type
     */
    public void testArrayLength(final TestUtils.WorldSize worldSize, final Context context, final RobotType robotType) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final ControlCenter controlCenter = new ControlCenter();

        final var robots = Assertions2.callObject(
            () -> robotType.getInitMethodInvoker().apply(controlCenter),
            context,
            r -> "The method `%s` threw an exception: %s".formatted(robotType.getInitMethodName(), r.cause().toString())
        );
        Assertions2.assertNotNull(robots, context,
            r -> "The method `%s` returned `null`.".formatted(robotType.getInitMethodName())
        );

        final int expectedArrayLength = switch (robotType) {
            case SCAN -> worldSize.width() - 1;
            case CLEAN -> worldSize.height() - 1;
        };
        Assertions2.assertEquals(expectedArrayLength, robots.length, context,
            r -> "The array length should be %d.".formatted(expectedArrayLength)
        );
    }

    /**
     * Verifies the robot states.
     *
     * @param worldSize the world size
     * @param context   the test context
     * @param robotType the robot type
     */
    public void testRobotStates(final TestUtils.WorldSize worldSize, final Context context, final RobotType robotType) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final ControlCenter controlCenter = new ControlCenter();

        final var robots = Assertions2.callObject(
            () -> robotType.getInitMethodInvoker().apply(controlCenter),
            context,
            r -> "The method `%s` threw an exception: %s".formatted(robotType.getInitMethodName(), r.cause().toString())
        );

        Assertions2.assertNotNull(
            robots,
            context,
            r -> "The method `%s` returned `null`.".formatted(robotType.getInitMethodName())
        );
        for (int i = 0; i < robots.length; i++) {
            final Robot robot = robots[i];
            final var finalI = i;

            Assertions2.assertNotNull(robot, context,
                r -> "The robot at index %d is `null`.".formatted(finalI)
            );
            Assertions2.assertSame(robotType.getRobotClass(), robot.getClass(), context,
                r -> "The robot at index %d is not of type `%s`.".formatted(finalI, robotType.getRobotClass().getSimpleName())
            );

            final var expectedX = switch (robotType) {
                case SCAN -> i + 1;
                case CLEAN -> 0;
            };
            Assertions2.assertEquals(expectedX, robot.getX(), context,
                r -> "The robot at index %d has the wrong x coordinate.".formatted(finalI)
            );

            final var expectedY = switch (robotType) {
                case SCAN -> 0;
                case CLEAN -> i + 1;
            };
            Assertions2.assertEquals(expectedY, robot.getY(), context,
                r -> "The robot at index %d has the wrong y coordinate.".formatted(finalI)
            );

            final var expectedDirection = switch (robotType) {
                case SCAN -> Direction.UP;
                case CLEAN -> Direction.RIGHT;
            };
            Assertions2.assertEquals(expectedDirection, robot.getDirection(), context,
                r -> "The robot at index %d has the wrong direction.".formatted(finalI)
            );
            Assertions2.assertEquals(0, robot.getNumberOfCoins(), context,
                r -> "The robot at index %d has the wrong number of coins.".formatted(finalI)
            );
        }
    }

    /**
     * Tests that there are no robots with duplicate positions.
     *
     * @param worldSize the world size
     * @param context   the test context
     * @param robotType the robot type
     */
    @SuppressWarnings("checkstyle:EmptyLineSeparator")
    public void testNoRobotsWithDuplicatePositions(
        final TestUtils.WorldSize worldSize,
        final Context context,
        final RobotType robotType
    ) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final ControlCenter controlCenter = new ControlCenter();

        final var robots = Assertions2.callObject(
            () -> robotType.getInitMethodInvoker().apply(controlCenter),
            context,
            r -> "The method `%s` threw an exception: %s".formatted(robotType.getInitMethodName(), r.cause().toString())
        );

        Assertions2.assertNotNull(
            robots,
            context,
            r -> "The method `%s` returned `null`.".formatted(robotType.getInitMethodName())
        );

        record Position(int x, int y) {
        }

        final var positions = new HashSet<Position>();

        // Could be done in an easier way, but this gives more precise error messages
        for (int i = 0; i < robots.length; i++) {
            final var robot = robots[i];
            final var finalI = i;
            Assertions2.assertNotNull(robot, context, r -> "The robot at index %d is `null`.".formatted(finalI));
            Assertions2.assertFalse(
                positions.contains(new Position(robot.getX(), robot.getY())),
                context,
                r -> "The robot at index %d has the same position as another robot.".formatted(finalI)
            );

            positions.add(new Position(robot.getX(), robot.getY()));
        }
    }

    enum RobotType {
        SCAN(ScanRobot.class, "initScanRobots", ControlCenter::initScanRobots),
        CLEAN(CleanRobot.class, "initCleaningRobots", ControlCenter::initCleaningRobots);

        private final Class<?> robotClass;
        private final String initMethodName;
        private final Function<ControlCenter, Robot[]> initMethodInvoker;

        RobotType(
            final Class<?> robotClass,
            final String initMethodName,
            final Function<ControlCenter, Robot[]> initMethodInvoker
        ) {
            this.robotClass = robotClass;
            this.initMethodName = initMethodName;
            this.initMethodInvoker = initMethodInvoker;
        }

        public Class<?> getRobotClass() {
            return this.robotClass;
        }

        public String getInitMethodName() {
            return this.initMethodName;
        }

        public Function<ControlCenter, Robot[]> getInitMethodInvoker() {
            return this.initMethodInvoker;
        }
    }
}
