package h02.h4;

import fopbot.Direction;
import fopbot.Robot;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.ScanRobot;
import h02.TestUtils;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@TestForSubmission
@Timeout(
    value = TestUtils.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
@SkipAfterFirstFailedTest(TestUtils.SKIP_AFTER_FIRST_FAILED_TEST)
public class H4_2 implements IWorldSetup {

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testArrayDimensions(final TestUtils.WorldSize worldSize) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final ScanRobot[] robots = IntStream.range(0, worldSize.width())
            .mapToObj(i -> new ScanRobot(i, 0, Direction.UP, 0))
            .toArray(ScanRobot[]::new);;
        final Direction direction = Direction.UP;
        final Random random = TestUtils.random(worldSize);
        final int[][] coins = IntStream.range(0, worldSize.height()).sequential()
            .mapToObj(x -> random.ints(worldSize.width(), 0, 5).toArray())
            .toArray(int[][]::new);

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        final var context = Assertions2.contextBuilder()
            .add("worldWidth", worldSize.width())
            .add("worldHeight", worldSize.height())
            .build();

        final ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        final var result = Assertions2.callObject(
            () -> controlCenter.scanWorld(robots),
            context,
            r -> "The method `returnRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        Assertions2.assertNotNull(
            result,
            context,
            r -> "The method `scanWorld` returned `null`."
        );

        Assertions2.assertEquals(
            worldSize.height(),
            result.length,
            context,
            r -> "The method `scanWorld` returned an array with the wrong width."
        );

        for (final boolean[] columns : result) {
            Assertions2.assertNotNull(
                columns,
                context,
                r -> "The method `scanWorld` returned an array with a `null` columns."
            );

            Assertions2.assertEquals(
                worldSize.width(),
                columns.length,
                context,
                r -> "The method `scanWorld` returned a column array with the wrong height."
            );
        }
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testResultArrayEntriesCorrect(final TestUtils.WorldSize worldSize) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final ScanRobot[] robots = IntStream.range(0, worldSize.width())
            .mapToObj(i -> new ScanRobot(i, 0, Direction.UP, 0))
            .toArray(ScanRobot[]::new);;
        final Direction direction = Direction.UP;
        final Random random = TestUtils.random(worldSize);
        final int[][] coins = IntStream.range(0, worldSize.height()).sequential()
            .mapToObj(x -> random.ints(worldSize.width(), 0, 5).toArray())
            .toArray(int[][]::new);

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        final var context = Assertions2.contextBuilder()
            .add("worldWidth", worldSize.width())
            .add("worldHeight", worldSize.height())
            .build();

        final ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        final var result = Assertions2.callObject(
            () -> controlCenter.scanWorld(robots),
            context,
            r -> "The method `returnRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        Assertions2.assertNotNull(
            result,
            context,
            r -> "The method `scanWorld` returned `null`."
        );

        for (int y = 0; y < coins.length; y++) {
            for (int x = 0; x < coins[y].length; x++) {
                final var finalY = y;
                final var finalX = x;

                try {
                    final var expected = y != 0 && coins[y][x] > 0;
                    Assertions2.assertEquals(
                        expected,
                        result[y][x],
                        context,
                        r -> "The method `scanWorld` returned an array with the wrong value at position (%d, %d)."
                            .formatted(finalX, finalY)
                    );
                } catch (final ArrayIndexOutOfBoundsException e) {
                    Assertions2.fail(
                        context,
                        r -> "The method `scanWorld` returned an array with the wrong dimensions."
                    );
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testSpinRobotsUsage(final TestUtils.WorldSize worldSize) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final ScanRobot[] robots = IntStream.range(0, worldSize.width())
            .mapToObj(i -> new ScanRobot(i, 0, Direction.UP, 0))
            .toArray(ScanRobot[]::new);
        final ScanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        final Direction direction = Direction.UP;
        final Random random = TestUtils.random(worldSize);
        final int[][] coins = IntStream.range(0, worldSize.height()).sequential()
            .mapToObj(x -> random.ints(worldSize.width(), 0, 5).toArray())
            .toArray(int[][]::new);

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        final var firstEdgePositions = H4Utils.getEndOfWorldRobotPositions(robots, worldSize.width(), worldSize.height());
        final var secondEdgePositions = H4Utils.getEndOfWorldPositions(
            firstEdgePositions,
            Direction.values()[(direction.ordinal() + 2) % 4],
            worldSize.width(), worldSize.height()
        );

        final var context = Assertions2.contextBuilder()
            .add("worldWidth", worldSize.width())
            .add("worldHeight", worldSize.height())
            .build();

        final ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        final var invalidInvocationMessage = new String[1];
        final var times = new AtomicInteger(0);
        Mockito.doAnswer((Answer<Object>) invocation -> {
            // The first time this should be called is when all robots are at the end of the world
            if (times.get() == 0) {
                for (int i = 0; i < robotsReferenceCopy.length; i++) {
                    final var robot = robotsReferenceCopy[i];
                    var valid = robot.getDirection() == direction;
                    valid &= firstEdgePositions[i].x() == robot.getX() && firstEdgePositions[i].y() == robot.getY();

                    if (!valid) {
                        invalidInvocationMessage[0] = "The method `spinRobots` was not called the first time when all "
                            + "robots were at the end of the world.";
                        break;
                    }
                }
            } else if (times.get() == 1) {
                // The second time this should be called is when all robots are at the opposite end of the world
                for (int i = 0; i < robotsReferenceCopy.length; i++) {
                    final var robot = robotsReferenceCopy[i];
                    var valid = (Direction.values()[(robot.getDirection().ordinal() + 2) % 4]) == direction;
                    valid &= secondEdgePositions[i].x() == robot.getX() && secondEdgePositions[i].y() == robot.getY();

                    if (!valid) {
                        invalidInvocationMessage[0] = "The method `spinRobots` was not called the second time when all"
                            + " robots were at the opposite end of the world.";
                        break;
                    }
                }
            }

            times.incrementAndGet();
            // Solution impl for spinRobots
            final var argument = (Robot[]) invocation.getArgument(0);
            for (final Robot robot : argument) {
                robot.turnLeft();
                robot.turnLeft();
            }
            return null;
        }).when(controlCenter).spinRobots(Mockito.any());

        final var result = Assertions2.callObject(
            () -> controlCenter.scanWorld(robots),
            context,
            r -> "The method `scanWorld` threw an exception: %s".formatted(r.cause().toString())
        );

        if (invalidInvocationMessage[0] != null) {
            Assertions2.fail(context, r -> invalidInvocationMessage[0]);
        }

        Mockito.verify(controlCenter, Mockito.times(2)).spinRobots(Mockito.same(robots));
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testReturnRobotsUsage(final TestUtils.WorldSize worldSize) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final ScanRobot[] robots = IntStream.range(0, worldSize.width())
            .mapToObj(i -> new ScanRobot(i, 0, Direction.UP, 0))
            .toArray(ScanRobot[]::new);
        final ScanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        final Direction direction = Direction.UP;
        final Random random = TestUtils.random(worldSize);
        final int[][] coins = IntStream.range(0, worldSize.height()).sequential()
            .mapToObj(x -> random.ints(worldSize.width(), 0, 5).toArray())
            .toArray(int[][]::new);

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        final var firstEdgePositions = H4Utils.getEndOfWorldRobotPositions(robots, worldSize.width(), worldSize.height());

        final var context = Assertions2.contextBuilder()
            .add("worldWidth", worldSize.width())
            .add("worldHeight", worldSize.height())
            .build();

        final ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        final var invalidInvocationMessage = new String[1];
        Mockito.doAnswer((Answer<Object>) invocation -> {
            // This should be called when all robots are at the end of the world, but facing the opposite direction
            if (invalidInvocationMessage[0] == null) {
                for (int i = 0; i < robotsReferenceCopy.length; i++) {
                    final var robot = robotsReferenceCopy[i];
                    var valid = (Direction.values()[(robot.getDirection().ordinal() + 2) % 4]) == direction;
                    valid &= firstEdgePositions[i].x() == robot.getX() && firstEdgePositions[i].y() == robot.getY();

                    if (!valid) {
                        invalidInvocationMessage[0] = "The method `returnRobots` was not called when all robots were at"
                            + " the end of the world and spun around.";
                        break;
                    }
                }
            }

            // Solution impl for returnRobots
            for (final Robot robot : (Robot[]) invocation.getArgument(0)) {
                while (robot.isFrontClear()) {
                    robot.move();
                }
            }
            return null;
        }).when(controlCenter).returnRobots(Mockito.any());

        final var result = Assertions2.callObject(
            () -> controlCenter.scanWorld(robots),
            context,
            r -> "The method `scanWorld` threw an exception: %s".formatted(r.cause().toString())
        );

        if (invalidInvocationMessage[0] != null) {
            Assertions2.fail(context, r -> invalidInvocationMessage[0]);
        }

        Mockito.verify(controlCenter).returnRobots(Mockito.same(robots));
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testRobotsFinalPositionAndDirection(final TestUtils.WorldSize worldSize) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final ScanRobot[] robots = IntStream.range(0, worldSize.width())
            .mapToObj(i -> new ScanRobot(i, 0, Direction.UP, 0))
            .toArray(ScanRobot[]::new);
        final ScanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        final Direction direction = Direction.UP;
        final Random random = TestUtils.random(worldSize);
        final int[][] coins = IntStream.range(0, worldSize.height()).sequential()
            .mapToObj(x -> random.ints(worldSize.width(), 0, 5).toArray())
            .toArray(int[][]::new);

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        final var finalPositions = H4Utils.getEndOfWorldPositions(
            H4Utils.getEndOfWorldRobotPositions(robots, worldSize.width(), worldSize.height()),
            Direction.values()[(direction.ordinal() + 2) % 4],
            worldSize.width(), worldSize.height()
        );

        final var context = Assertions2.contextBuilder()
            .add("worldWidth", worldSize.width())
            .add("worldHeight", worldSize.height())
            .build();

        final ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        final var result = Assertions2.callObject(
            () -> controlCenter.scanWorld(robots),
            context,
            r -> "The method `scanWorld` threw an exception: %s".formatted(r.cause().toString())
        );

        for (int i = 0; i < robotsReferenceCopy.length; i++) {
            final var robot = robotsReferenceCopy[i];
            final var finalI = i;
            Assertions2.assertEquals(
                direction,
                robot.getDirection(),
                context,
                r -> "The final direction for the robot at index %d was incorrect.".formatted(finalI)
            );

            Assertions2.assertEquals(
                finalPositions[i].x(),
                robot.getX(),
                context,
                r -> "The final x-coordinate for the robot at index %d was incorrect.".formatted(finalI)
            );

            Assertions2.assertEquals(
                finalPositions[i].y(),
                robot.getY(),
                context,
                r -> "The final y-coordinate for the robot at index %d was incorrect.".formatted(finalI)
            );
        }
    }
}
