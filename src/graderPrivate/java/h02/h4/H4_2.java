package h02.h4;

import com.fasterxml.jackson.databind.JsonNode;
import fopbot.Direction;
import fopbot.Robot;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.ScanRobot;
import h02.TestUtils;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSetTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@TestForSubmission
@Timeout(
    value = TestUtils.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
@SkipAfterFirstFailedTest(TestUtils.SKIP_AFTER_FIRST_FAILED_TEST)
public class H4_2 implements IWorldSetup {

    public static final Map<String, Function<JsonNode, ?>> CUSTOM_CONVERTERS = new HashMap<>(H4Utils.CUSTOM_CONVERTERS);
    static {
        CUSTOM_CONVERTERS.put("robots", H4Utils.robotConverter(ScanRobot.class));
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testArrayDimensions(JsonParameterSet parameterSet) {
        int worldWidth = parameterSet.get("worldWidth");
        int worldHeight = parameterSet.get("worldHeight");
        TestUtils.setWorldSizeAndActionLimit(worldWidth, worldHeight);

        ScanRobot[] robots = parameterSet.get("robots");
        Direction direction = parameterSet.get("direction");
        int[][] coins = parameterSet.get("coins");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        var context = Assertions2.contextBuilder()
            .add("worldWidth", worldWidth)
            .add("worldHeight", worldHeight)
            .build();

        ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        var result = Assertions2.callObject(
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
            worldHeight,
            result.length,
            context,
            r -> "The method `scanWorld` returned an array with the wrong width."
        );

        for (boolean[] columns : result) {
            Assertions2.assertNotNull(
                columns,
                context,
                r -> "The method `scanWorld` returned an array with a `null` columns."
            );

            Assertions2.assertEquals(
                worldWidth,
                columns.length,
                context,
                r -> "The method `scanWorld` returned a column array with the wrong height."
            );
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testResultArrayEntriesCorrect(JsonParameterSet parameterSet) {
        int worldWidth = parameterSet.get("worldWidth");
        int worldHeight = parameterSet.get("worldHeight");
        TestUtils.setWorldSizeAndActionLimit(worldWidth, worldHeight);

        ScanRobot[] robots = parameterSet.get("robots");
        Direction direction = parameterSet.get("direction");
        int[][] coins = parameterSet.get("coins");
        boolean[][] expected = parameterSet.get("expected");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        var context = Assertions2.contextBuilder()
            .add("worldWidth", worldWidth)
            .add("worldHeight", worldHeight)
            .build();

        ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        var result = Assertions2.callObject(
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
                var finalY = y;
                var finalX = x;

                try {
                    Assertions2.assertEquals(
                        expected[y][x],
                        result[y][x],
                        context,
                        r -> "The method `scanWorld` returned an array with the wrong value at position (%d, %d).".formatted(finalX, finalY)
                    );
                } catch (ArrayIndexOutOfBoundsException e) {
                    Assertions2.fail(
                        context,
                        r -> "The method `scanWorld` returned an array with the wrong dimensions."
                    );
                }
            }
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testSpinRobotsUsage(JsonParameterSet parameterSet) {
        int worldWidth = parameterSet.get("worldWidth");
        int worldHeight = parameterSet.get("worldHeight");
        TestUtils.setWorldSizeAndActionLimit(worldWidth, worldHeight);

        ScanRobot[] robots = parameterSet.get("robots");
        ScanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        Direction direction = parameterSet.get("direction");
        int[][] coins = parameterSet.get("coins");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        var firstEdgePositions = H4Utils.getEndOfWorldRobotPositions(robots, worldWidth, worldHeight);
        var secondEdgePositions = H4Utils.getEndOfWorldPositions(firstEdgePositions, Direction.values()[(direction.ordinal() + 2) % 4], worldWidth, worldHeight);

        var context = Assertions2.contextBuilder()
            .add("worldWidth", worldWidth)
            .add("worldHeight", worldHeight)
            .build();

        ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        var invalidInvocationMessage = new String[1];
        var times = new AtomicInteger(0);
        Mockito.doAnswer((Answer<Object>) invocation -> {
            // The first time this should be called is when all robots are at the end of the world
            if (times.get() == 0) {
                for (int i = 0; i < robotsReferenceCopy.length; i++) {
                    var robot = robotsReferenceCopy[i];
                    var valid = robot.getDirection() == direction;
                    valid &= firstEdgePositions[i].x() == robot.getX() && firstEdgePositions[i].y() == robot.getY();

                    if (!valid) {
                        invalidInvocationMessage[0] = "The method `spinRobots` was not called the first time when all robots were at the end of the world.";
                        break;
                    }
                }
            } else if (times.get() == 1) { // The second time this should be called is when all robots are at the opposite end of the world
                for (int i = 0; i < robotsReferenceCopy.length; i++) {
                    var robot = robotsReferenceCopy[i];
                    var valid = (Direction.values()[(robot.getDirection().ordinal() + 2) % 4]) == direction;
                    valid &= secondEdgePositions[i].x() == robot.getX() && secondEdgePositions[i].y() == robot.getY();

                    if (!valid) {
                        invalidInvocationMessage[0] = "The method `spinRobots` was not called the second time when all robots were at the opposite end of the world.";
                        break;
                    }
                }
            }

            times.incrementAndGet();
            // Solution impl for spinRobots
            var argument = (Robot[]) invocation.getArgument(0);
            for (Robot robot : argument) {
                robot.turnLeft();
                robot.turnLeft();
            }
            return null;
        }).when(controlCenter).spinRobots(Mockito.any());

        var result = Assertions2.callObject(
            () -> controlCenter.scanWorld(robots),
            context,
            r -> "The method `scanWorld` threw an exception: %s".formatted(r.cause().toString())
        );

        if (invalidInvocationMessage[0] != null)
            Assertions2.fail(context, r -> invalidInvocationMessage[0]);

        Mockito.verify(controlCenter, Mockito.times(2)).spinRobots(Mockito.same(robots));
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testReturnRobotsUsage(JsonParameterSet parameterSet) {
        int worldWidth = parameterSet.get("worldWidth");
        int worldHeight = parameterSet.get("worldHeight");
        TestUtils.setWorldSizeAndActionLimit(worldWidth, worldHeight);

        ScanRobot[] robots = parameterSet.get("robots");
        ScanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        Direction direction = parameterSet.get("direction");
        int[][] coins = parameterSet.get("coins");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        var firstEdgePositions = H4Utils.getEndOfWorldRobotPositions(robots, worldWidth, worldHeight);

        var context = Assertions2.contextBuilder()
            .add("worldWidth", worldWidth)
            .add("worldHeight", worldHeight)
            .build();

        ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        var invalidInvocationMessage = new String[1];
        Mockito.doAnswer((Answer<Object>) invocation -> {
            // This should be called when all robots are at the end of the world, but facing the opposite direction
            if (invalidInvocationMessage[0] == null) {
                for (int i = 0; i < robotsReferenceCopy.length; i++) {
                    var robot = robotsReferenceCopy[i];
                    var valid = (Direction.values()[(robot.getDirection().ordinal() + 2) % 4]) == direction;
                    valid &= firstEdgePositions[i].x() == robot.getX() && firstEdgePositions[i].y() == robot.getY();

                    if (!valid) {
                        invalidInvocationMessage[0] = "The method `returnRobots` was not called when all robots were at the end of the world and spun around.";
                        break;
                    }
                }
            }

            // Solution impl for returnRobots
            for (Robot robot : (Robot[]) invocation.getArgument(0)) {
                while (robot.isFrontClear())
                    robot.move();
            }
            return null;
        }).when(controlCenter).returnRobots(Mockito.any());

        var result = Assertions2.callObject(
            () -> controlCenter.scanWorld(robots),
            context,
            r -> "The method `scanWorld` threw an exception: %s".formatted(r.cause().toString())
        );

        if (invalidInvocationMessage[0] != null)
            Assertions2.fail(context, r -> invalidInvocationMessage[0]);

        Mockito.verify(controlCenter).returnRobots(Mockito.same(robots));
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testRobotsFinalPositionAndDirection(JsonParameterSet parameterSet) {
        int worldWidth = parameterSet.get("worldWidth");
        int worldHeight = parameterSet.get("worldHeight");
        TestUtils.setWorldSizeAndActionLimit(worldWidth, worldHeight);

        ScanRobot[] robots = parameterSet.get("robots");
        ScanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        Direction direction = parameterSet.get("direction");
        int[][] coins = parameterSet.get("coins");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        var finalPositions = H4Utils.getEndOfWorldPositions(
            H4Utils.getEndOfWorldRobotPositions(robots, worldWidth, worldHeight),
            Direction.values()[(direction.ordinal() + 2) % 4],
            worldWidth, worldHeight
        );

        var context = Assertions2.contextBuilder()
            .add("worldWidth", worldWidth)
            .add("worldHeight", worldHeight)
            .build();

        ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        var result = Assertions2.callObject(
            () -> controlCenter.scanWorld(robots),
            context,
            r -> "The method `scanWorld` threw an exception: %s".formatted(r.cause().toString())
        );

        for (int i = 0; i < robotsReferenceCopy.length; i++) {
            var robot = robotsReferenceCopy[i];
            var finalI = i;
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
