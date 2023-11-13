package h02.h3;

import fopbot.Robot;
import h02.CleanRobot;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.RobotArrayTestUtils;
import h02.ScanRobot;
import h02.TestUtils;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.Arrays;
import java.util.List;

@TestForSubmission
@Timeout(
    value = TestUtils.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
public class H3_3 implements IWorldSetup {

    private static final RobotArrayTestUtils.RobotArrayElementVerifier ROBOT_REPLACED_VERIFIER = ((context, newRobot, referenceCopy, copy) -> {
        if (copy.isTurnedOff()) {
            Assertions2.assertTrue(newRobot.isTurnedOn(), context, r -> "The method did not replace a disabled robot.");
            Assertions2.assertNotSame(referenceCopy, newRobot, context, r -> "The method turned a robot on instead of replacing it.");
        } else {
            Assertions2.assertSame(referenceCopy, newRobot, context, r -> "The method replaced a robot which was not turned off.");
        }
    });

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testOnlyDisabledRobotsAreReplaced(TestUtils.WorldSize worldSize, Context context) {
        testRobotProperties(worldSize, context, Robot.class, ROBOT_REPLACED_VERIFIER);
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testReplacedRobotsHaveCorrectProperties(TestUtils.WorldSize worldSize, Context context) {
        testRobotProperties(worldSize, context, Robot.class, ROBOT_REPLACED_VERIFIER.and(RobotArrayTestUtils.ROBOT_XY_COINS_VERIFIER.and(RobotArrayTestUtils.ROBOT_DIRECTION_VERIFIER)));
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testReplacedRobotsHaveCorrectType(TestUtils.WorldSize worldSize, Context context) {
        List.of(CleanRobot.class, ScanRobot.class).forEach(clazz -> {
            testRobotProperties(worldSize, context, clazz, ROBOT_REPLACED_VERIFIER.and((c, newRobot, referenceCopy, copy) -> {
                Assertions2.assertEquals(referenceCopy.getClass(), newRobot.getClass(), c, r -> "The method replaced a robot with a robot of a different type.");
            }));
        });
    }

    private void testRobotProperties(TestUtils.WorldSize worldSize, Context context, Class<? extends Robot> robotClass, RobotArrayTestUtils.RobotArrayElementVerifier verifier) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        var random = TestUtils.random(worldSize);
        var robotArrays = RobotArrayTestUtils.generateRobotArray(robotClass, random, worldSize);
        var robots = robotArrays.getLeft();
        var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);
        var robotsCopy = robotArrays.getRight();

        var controlCenter = new ControlCenter();

        for (int i = 0; i < robots.length; i++) {
            if (random.nextBoolean()) {
                robots[i].turnOff();
                robotsCopy[i].turnOff();
            }
        }

        Assertions2.call(
            // NOTE: We supply neither a ScanRobot[] nor a CleanRobot[] here, but a Robot[]. Otherwise, if the student
            // did implement criterion 3 correctly, all tests would crash.
            () -> controlCenter.replaceBrokenRobots(robots),
            context,
            r -> "The method `replaceDisabledRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        RobotArrayTestUtils.assertRobotArrayWasModifiedCorrectly(context, robots, robotsReferenceCopy, robotsCopy, verifier);
    }
}
