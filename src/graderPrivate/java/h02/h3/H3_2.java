package h02.h3;

import fopbot.Direction;
import fopbot.Robot;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.RobotArrayTestUtils;
import h02.TestUtils;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.Arrays;
import java.util.IdentityHashMap;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;

@TestForSubmission
@Timeout(
    value = TestUtils.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
@SkipAfterFirstFailedTest(TestUtils.SKIP_AFTER_FIRST_FAILED_TEST)
public class H3_2 implements IWorldSetup {

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testRotateRobots(final TestUtils.WorldSize worldSize, final Context context) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final var random = TestUtils.random(worldSize);
        final var robotArrays = RobotArrayTestUtils.generateRobotArray(Robot.class, random, worldSize);
        final var robots = robotArrays.getLeft();
        final var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        final var robotsCopy = robotArrays.getRight();

        final var controlCenter = new ControlCenter();

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
    public void testCheckForDamageCalls(final TestUtils.WorldSize worldSize, final Context context) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final var random = TestUtils.random(worldSize);
        final var robotArrays = RobotArrayTestUtils.generateRobotArray(Robot.class, random, worldSize);
        final var robots = robotArrays.getLeft();
        final var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        final var robotsCopy = robotArrays.getRight();

        final var controlCenter = spy(new ControlCenter());
        final var checkForDamageCalls = new IdentityHashMap<Robot, Integer>();
        doAnswer(invocation -> {
            final var robot = (Robot) invocation.getArgument(0);
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
