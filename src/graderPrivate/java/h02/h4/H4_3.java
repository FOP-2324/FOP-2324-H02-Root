package h02.h4;

import com.fasterxml.jackson.databind.JsonNode;
import fopbot.Direction;
import fopbot.Robot;
import fopbot.Transition;
import fopbot.World;
import h02.CleanRobot;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.TestUtils;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
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
public class H4_3 implements IWorldSetup {

    public static final Map<String, Function<JsonNode, ?>> CUSTOM_CONVERTERS = new HashMap<>(H4Utils.CUSTOM_CONVERTERS);
    static {
        CUSTOM_CONVERTERS.put("robots", H4Utils.robotConverter(CleanRobot.class));
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testPickedUpCoinAmountsAny(JsonParameterSet parameterSet) {
        testPickedUpCoinAmounts(parameterSet, false);
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testPickedUpCoinAmountsExact(JsonParameterSet parameterSet) {
        testPickedUpCoinAmounts(parameterSet, true);
    }

    private static void testPickedUpCoinAmounts(JsonParameterSet parameterSet, boolean exact) {
        int worldWidth = parameterSet.get("worldWidth");
        int worldHeight = parameterSet.get("worldHeight");
        TestUtils.setWorldSizeAndActionLimit(worldWidth, worldHeight);

        CleanRobot[] robots = parameterSet.get("robots");
        Direction direction = parameterSet.get("direction");
        boolean[][] expected = parameterSet.get("expected");
        int[][] coins = parameterSet.get("coins");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        boolean[][] coinPositions = getCoinPositions(coins);

        var context = Assertions2.contextBuilder()
            .add("worldWidth", worldWidth)
            .add("worldHeight", worldHeight)
            .build();

        ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        Assertions2.call(
            () -> controlCenter.moveCleanRobots(robots, coinPositions),
            context,
            r -> "The method `returnRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        for (int y = 0; y < expected.length; y++) {
            for (int x = 0; x < expected[y].length; x++) {
                var finalY = y;
                var finalX = x;

                var coinsOnField = TestUtils.getCoinsOnField(x, y);

                if (exact) {
                    Assertions2.assertEquals(
                        coins[y][x] - (expected[y][x] ? 1 : 0),
                        coinsOnField,
                        context,
                        r -> "The coin amount at position (%d, %d) was incorrect.".formatted(finalX, finalY)
                    );
                } else {
                    Assertions2.assertTrue(
                        expected[y][x] ? coinsOnField < coins[y][x] : coinsOnField == coins[y][x],
                        context,
                        r -> "No coin was picked up at the position (%d, %d).".formatted(finalX, finalY)
                    );
                }
            }
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testRobotMovement(JsonParameterSet parameterSet) {
        int worldWidth = parameterSet.get("worldWidth");
        int worldHeight = parameterSet.get("worldHeight");
        TestUtils.setWorldSizeAndActionLimit(worldWidth, worldHeight);

        CleanRobot[] robots = parameterSet.get("robots");
        Direction direction = parameterSet.get("direction");
        int[][] coins = parameterSet.get("coins");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        CleanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        H4Utils.Position[] startingPositions = getPositions(robots);
        H4Utils.Position[] finalPositions = H4Utils.getEndOfWorldRobotPositions(robots, worldWidth, worldHeight);
        boolean[][] coinPositions = getCoinPositions(coins);

        var context = Assertions2.contextBuilder()
            .add("worldWidth", worldWidth)
            .add("worldHeight", worldHeight)
            .build();

        ControlCenter controlCenter = H4Utils.mockReturnAndSpinRobots();

        Assertions2.call(
            () -> controlCenter.moveCleanRobots(robots, coinPositions),
            context,
            r -> "The method `returnRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        for (int i = 0; i < robotsReferenceCopy.length; i++) {
            var finalI = i;
            var robot = robotsReferenceCopy[i];

            var trace = World.getGlobalWorld().getTrace(robot);
            var moveCount = 0;
            var reachedEnd = false;
            for (var transition : trace.getTransitions()) {
                var finalPosition = finalPositions[i];

                if (transition.robot.getX() == finalPosition.x() && transition.robot.getY() == finalPosition.y()) {
                    reachedEnd = true;
                    break;
                }

                if (transition.action != Transition.RobotAction.MOVE)
                    continue;

                moveCount++;
            }

            if (!reachedEnd)
                Assertions2.fail(context, r -> "The robot at index %d did not reach the end of the world.".formatted(finalI));

            Assertions2.assertEquals(
                startingPositions[i].manhattanDistance(finalPositions[i]),
                moveCount,
                context,
                r -> "The robot at index %d did not perform the minimal number of move actions to reach the end of the world.".formatted(finalI)
            );
        }
    }

    @ParameterizedTest
    @JsonParameterSetTest(value = "H4_testCases.json", customConverters = "CUSTOM_CONVERTERS")
    public void testSpinRobotsUsage(JsonParameterSet parameterSet) {
        int worldWidth = parameterSet.get("worldWidth");
        int worldHeight = parameterSet.get("worldHeight");
        TestUtils.setWorldSizeAndActionLimit(worldWidth, worldHeight);

        CleanRobot[] robots = parameterSet.get("robots");
        CleanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        Direction direction = parameterSet.get("direction");
        int[][] coins = parameterSet.get("coins");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        boolean[][] coinPositions = getCoinPositions(coins);
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

        Assertions2.call(
            () -> controlCenter.moveCleanRobots(robots, coinPositions),
            context,
            r -> "The method `moveCleanRobots` threw an exception: %s".formatted(r.cause().toString())
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

        CleanRobot[] robots = parameterSet.get("robots");
        CleanRobot[] robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        Direction direction = parameterSet.get("direction");
        int[][] coins = parameterSet.get("coins");

        H4Utils.initRobotsAndWorld(robots, direction, coins);

        boolean[][] coinPositions = getCoinPositions(coins);
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

        Assertions2.call(
            () -> controlCenter.moveCleanRobots(robots, coinPositions),
            context,
            r -> "The method `moveCleanRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        if (invalidInvocationMessage[0] != null)
            Assertions2.fail(context, r -> invalidInvocationMessage[0]);

        Mockito.verify(controlCenter).returnRobots(Mockito.same(robots));
    }

    /**
     * Converts the given coin array to a boolean array, where an entry in the result is {@code true} iff. the coin
     * amount at the corresponding position in the given array is greater than 0.
     *
     * @param coins The coin array to convert
     * @return The binarized coin array
     */
    private static boolean[][] getCoinPositions(int[][] coins) {
        boolean[][] coinPositions = new boolean[coins.length][coins[0].length];
        for (int x = 0; x < coins.length; x++) {
            for (int y = 0; y < coins[x].length; y++) {
                coinPositions[x][y] = coins[x][y] > 0;
            }
        }
        return coinPositions;
    }

    private static H4Utils.Position[] getPositions(Robot[] robots) {
        var positions = new H4Utils.Position[robots.length];
        for (int i = 0; i < robots.length; i++)
            positions[i] = new H4Utils.Position(robots[i].getX(), robots[i].getY());
        return positions;
    }
}
