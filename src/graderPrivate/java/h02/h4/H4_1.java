package h02.h4;

import fopbot.Transition;
import fopbot.World;
import h02.CleanRobot;
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

@TestForSubmission
@Timeout(
    value = TestUtils.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
@SkipAfterFirstFailedTest(TestUtils.SKIP_AFTER_FIRST_FAILED_TEST)
public class H4_1 implements IWorldSetup {

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testRobotsFinalPosition(final TestUtils.WorldSize worldSize, final Context context) {
        testRobotMovement(worldSize, context, false);
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testRobotsPerformedOnlyMoveActions(final TestUtils.WorldSize worldSize, final Context context) {
        testRobotMovement(worldSize, context, true);
    }

    private static void testRobotMovement(
        final TestUtils.WorldSize worldSize,
        final Context context,
        final boolean checkStrictMovement
    ) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final var random = TestUtils.random(worldSize);

        final var robotsPair = RobotArrayTestUtils.generateRobotArray(CleanRobot.class, random, worldSize);
        final var robots = robotsPair.getLeft();
        final var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        final var robotsCopy = robotsPair.getRight();

        final ControlCenter controlCenter = new ControlCenter();

        Assertions2.call(
            () -> controlCenter.returnRobots(robots),
            context,
            r -> "The method `returnRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        RobotArrayTestUtils.assertRobotArrayWasModifiedCorrectly(context, robots, robotsReferenceCopy, robotsCopy,
            RobotArrayTestUtils.IMMUTABLE_ARRAY_VERIFIER.and((context2, newRobot, referenceCopy, copy) -> {
                Assertions2.assertEquals(
                    copy.getX(),
                    newRobot.getX(),
                    context2,
                    r -> "The method changed the X-Coordinate of a robot."
                );
                Assertions2.assertEquals(
                    worldSize.height() - 1,
                    newRobot.getY(),
                    context2,
                    r -> "The method did not move a robot to the edge of the world."
                );
            }));

        if (checkStrictMovement) {
            for (int i = 0; i < robotsReferenceCopy.length; i++) {
                final var robot = robotsReferenceCopy[i];
                final var trace = World.getGlobalWorld().getTrace(robot);
                for (final var transition : trace.getTransitions()) {
                    if (transition.action == Transition.RobotAction.NONE) {
                        continue;
                    }

                    Assertions2.assertEquals(
                        Transition.RobotAction.MOVE,
                        transition.action,
                        context,
                        r -> "The method did not only perform move actions."
                    );
                }

                Assertions2.assertEquals(
                    worldSize.height() /*- 1 not needed because the trace ends with a NONE action*/ - robotsCopy[i].getY(),
                    trace.getTransitions().size(),
                    context,
                    r -> "The method did not perform the minimal number of move actions."
                );
            }
        }
    }
}
