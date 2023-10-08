package h02.h1;

import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;
import h02.CleanRobot;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.ScanRobot;
import h02.TestUtils;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.function.Function;

public abstract class H1_AbstractTest implements IWorldSetup {

    public void testArrayLength(TestUtils.WorldSize worldSize, Context context, RobotType robotType) {
        World.setSize(worldSize.width(), worldSize.height());

        ControlCenter controlCenter = new ControlCenter();

        var robots = Assertions2.callObject(
            () -> robotType.getInitMethodInvoker().apply(controlCenter),
            context,
            r -> "The method `%s` threw an exception: %s".formatted(robotType.getInitMethodName(), r.cause().toString())
        );
        Assertions2.assertNotNull(robots, context,
            r -> "The method `%s` returned `null`.".formatted(robotType.getInitMethodName()));

        int expectedArrayLength = switch (robotType) {
            case SCAN -> worldSize.width() - 1;
            case CLEAN -> worldSize.height() - 1;
        };
        Assertions2.assertEquals(expectedArrayLength, robots.length, context,
            r -> "The array length should be %d.".formatted(expectedArrayLength));
    }

    public void testRobotStates(TestUtils.WorldSize worldSize, Context context, RobotType robotType) {
        World.setSize(worldSize.width(), worldSize.height());

        ControlCenter controlCenter = new ControlCenter();

        var robots = Assertions2.callObject(
            () -> robotType.getInitMethodInvoker().apply(controlCenter),
            context,
            r -> "The method `%s` threw an exception: %s".formatted(robotType.getInitMethodName(), r.cause().toString())
        );

        Assertions2.assertNotNull(robots, context, r -> "The method `%s` returned `null`.".formatted(robotType.getInitMethodName()));
        for (int i = 0; i < robots.length; i++) {
            Robot robot = robots[i];
            var finalI = i;

            Assertions2.assertNotNull(robot, context,
                r -> "The robot at index %d is `null`.".formatted(finalI));
            Assertions2.assertSame(robotType.getRobotClass(), robot.getClass(), context,
                r -> "The robot at index %d is not of type `%s`.".formatted(finalI, robotType.getRobotClass().getSimpleName()));

            var expectedX = switch (robotType) {
                case SCAN -> i + 1;
                case CLEAN -> 0;
            };
            Assertions2.assertEquals(expectedX, robot.getX(), context,
                r -> "The robot at index %d has the wrong x coordinate.".formatted(finalI));

            var expectedY = switch (robotType) {
                case SCAN -> 0;
                case CLEAN -> i + 1;
            };
            Assertions2.assertEquals(expectedY, robot.getY(), context,
                r -> "The robot at index %d has the wrong y coordinate.".formatted(finalI));

            var expectedDirection = switch (robotType) {
                case SCAN -> Direction.UP;
                case CLEAN -> Direction.RIGHT;
            };
            Assertions2.assertEquals(expectedDirection, robot.getDirection(), context,
                r -> "The robot at index %d has the wrong direction.".formatted(finalI));
            Assertions2.assertEquals(0, robot.getNumberOfCoins(), context,
                r -> "The robot at index %d has the wrong number of coins.".formatted(finalI));
        }
    }

    enum RobotType {
        SCAN(ScanRobot.class, "initScanRobots", ControlCenter::initScanRobots),
        CLEAN(CleanRobot.class, "initCleaningRobots", ControlCenter::initCleaningRobots);

        private final Class<?> robotClass;
        private final String initMethodName;
        private final Function<ControlCenter, Robot[]> initMethodInvoker;

        RobotType(Class<?> robotClass, String initMethodName, Function<ControlCenter, Robot[]> initMethodInvoker) {
            this.robotClass = robotClass;
            this.initMethodName = initMethodName;
            this.initMethodInvoker = initMethodInvoker;
        }

        public Class<?> getRobotClass() {
            return robotClass;
        }

        public String getInitMethodName() {
            return initMethodName;
        }

        public Function<ControlCenter, Robot[]> getInitMethodInvoker() {
            return initMethodInvoker;
        }
    }
}
