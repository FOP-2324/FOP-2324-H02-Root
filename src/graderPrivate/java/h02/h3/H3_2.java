package h02.h3;

import fopbot.Direction;
import fopbot.Robot;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.RobotArrayTestUtils;
import h02.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.Arrays;
import java.util.IdentityHashMap;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

@TestForSubmission
public class H3_2 implements IWorldSetup {

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testRotateRobots(TestUtils.WorldSize worldSize, Context context) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        var random = TestUtils.random(worldSize);
        var robotArrays = RobotArrayTestUtils.generateRobotArray(Robot.class, random, worldSize);
        var robots = robotArrays.getLeft();
        var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        var robotsCopy = robotArrays.getRight();

        var controlCenter = new ControlCenter();

        Assertions2.call(
            () -> controlCenter.rotateRobots(robots),
            context,
            r -> "The method `rotateRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        RobotArrayTestUtils.assertRobotArrayWasModifiedCorrectly(context, robots, robotsReferenceCopy, robotsCopy,
            RobotArrayTestUtils.ROBOT_XY_COINS_VERIFIER.and(((context2, newRobot, referenceCopy, copy) -> {
                Assertions2.assertEquals(
                    switch (copy.getDirection()) {
                        case UP -> Direction.DOWN;
                        case DOWN -> Direction.UP;
                        case LEFT -> Direction.RIGHT;
                        case RIGHT -> Direction.LEFT;
                    },
                    newRobot.getDirection(),
                    context2,
                    r -> "The method did not rotate a robot correctly."
                );
            })));
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testCheckForDamageCalls(TestUtils.WorldSize worldSize, Context context) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        var random = TestUtils.random(worldSize);
        var robotArrays = RobotArrayTestUtils.generateRobotArray(Robot.class, random, worldSize);
        var robots = robotArrays.getLeft();
        var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        var robotsCopy = robotArrays.getRight();

        var controlCenter = spy(new ControlCenter());
        var checkForDamageCalls = new IdentityHashMap<Robot, Integer>();
        doAnswer(invocation -> {
            var robot = (Robot) invocation.getArgument(0);
            checkForDamageCalls.merge(robot, 1, Integer::sum);
            return null;
        }).when(controlCenter).checkForDamage(any());

        Assertions2.call(
            () -> controlCenter.rotateRobots(robots),
            context,
            r -> "The method `rotateRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        RobotArrayTestUtils.assertRobotArrayWasModifiedCorrectly(context, robots, robotsReferenceCopy, robotsCopy,
            (c, newRobot, referenceCopy, copy) -> Assertions2.assertEquals(
                1,
                checkForDamageCalls.getOrDefault(referenceCopy, 0),
                c,
                r -> "The method `checkForDamage` was not called exactly once for a robot."
            )
        );
    }
}
