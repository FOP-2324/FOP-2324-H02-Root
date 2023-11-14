package h02.h3;

import fopbot.Robot;
import fopbot.World;
import h02.ControlCenter;
import h02.IWorldSetup;
import h02.RobotArrayTestUtils;
import h02.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;

import java.util.Arrays;

@TestForSubmission
@Timeout(
    value = TestUtils.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
@SkipAfterFirstFailedTest(TestUtils.SKIP_AFTER_FIRST_FAILED_TEST)
public class H3_1 implements IWorldSetup {

    @Test
    public void testInvertRobotsArray() {
        final var controlCenter = new ControlCenter();

        for (int i = 1; i <= 20; i++) {
            final var robotPair = RobotArrayTestUtils.generateRobotArray(
                Robot.class,
                TestUtils.random(i),
                new TestUtils.WorldSize(World.getWidth(), World.getHeight())
            );
            final var robots = robotPair.getLeft();
            final var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);

            Assertions2.call(
                () -> controlCenter.reverseRobots(robots),
                Assertions2.emptyContext(),
                r -> "The method `invertRobots` threw an exception: %s".formatted(r.cause().toString())
            );

            for (int j = 0; j < robots.length; j++) {
                final var finalJ = j;

                Assertions2.assertSame(
                    robotsReferenceCopy[j],
                    robots[robots.length - j - 1],
                    Assertions2.emptyContext(),
                    r -> "The robot at index %d was not swapped correctly.".formatted(finalJ)
                );
            }
        }
    }

    @Test
    public void testArrayHasSameElements() {
        final var controlCenter = new ControlCenter();

        for (int i = 1; i <= 20; i++) {
            final var robotPair = RobotArrayTestUtils.generateRobotArray(
                Robot.class,
                TestUtils.random(i),
                new TestUtils.WorldSize(World.getWidth(), World.getHeight())
            );
            final var robots = robotPair.getLeft();
            final var robotsCopy = robotPair.getRight();
            final var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);

            Assertions2.call(
                () -> controlCenter.reverseRobots(robots),
                Assertions2.emptyContext(),
                r -> "The method `invertRobots` threw an exception: %s".formatted(r.cause().toString())
            );

            RobotArrayTestUtils.assertRobotArrayWasModifiedCorrectly(
                Assertions2.emptyContext(),
                robots,
                robotsReferenceCopy,
                robotsCopy,
                (context, newRobot, referenceCopy, copy) -> {
                    if (Arrays.stream(robotsReferenceCopy).noneMatch(r -> r == newRobot)) {
                        Assertions2.fail(context, r -> "The method added a robot which was not in the original array.");
                    }
                }
            );
        }
    }

    @Test
    public void testRobotsWereNotModified() {
        final var controlCenter = new ControlCenter();

        for (int i = 1; i <= 20; i++) {
            final var robotPair = RobotArrayTestUtils.generateRobotArray(
                Robot.class,
                TestUtils.random(i),
                new TestUtils.WorldSize(World.getWidth(), World.getHeight())
            );
            final var robots = robotPair.getLeft();
            final var robotsCopy = robotPair.getRight();
            final var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);

            Assertions2.call(
                () -> controlCenter.reverseRobots(robots),
                Assertions2.emptyContext(),
                r -> "The method `invertRobots` threw an exception: %s".formatted(r.cause().toString())
            );

            for (int j = 0; j < robotsReferenceCopy.length; j++) {
                //check coins,direction,x,y
                final var finalJ = j;

                Assertions2.assertEquals(
                    robotsReferenceCopy[j].getNumberOfCoins(),
                    robotsCopy[j].getNumberOfCoins(),
                    Assertions2.emptyContext(),
                    r -> "The coin amount of the robot at index %d was modified.".formatted(finalJ)
                );

                Assertions2.assertEquals(
                    robotsReferenceCopy[j].getDirection(),
                    robotsCopy[j].getDirection(),
                    Assertions2.emptyContext(),
                    r -> "The direction of the robot at index %d was modified.".formatted(finalJ)
                );

                Assertions2.assertEquals(
                    robotsReferenceCopy[j].getX(),
                    robotsCopy[j].getX(),
                    Assertions2.emptyContext(),
                    r -> "The x coordinate of the robot at index %d was modified.".formatted(finalJ)
                );

                Assertions2.assertEquals(
                    robotsReferenceCopy[j].getY(),
                    robotsCopy[j].getY(),
                    Assertions2.emptyContext(),
                    r -> "The y coordinate of the robot at index %d was modified.".formatted(finalJ)
                );
            }
        }
    }
}
