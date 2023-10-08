package h02.h1;

import fopbot.World;
import h02.ControlCenter;
import h02.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.HashSet;

@TestForSubmission
public class H1_1 extends H1_AbstractTest {

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testArrayLength(TestUtils.WorldSize worldSize, Context context) {
        testArrayLength(worldSize, context, RobotType.SCAN);
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testRobotStates(TestUtils.WorldSize worldSize, Context context) {
        testRobotStates(worldSize, context, RobotType.SCAN);
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testNoRobotsWithDuplicatePositions(TestUtils.WorldSize worldSize, Context context) {
        World.setSize(worldSize.width(), worldSize.height());

        ControlCenter controlCenter = new ControlCenter();

        var robots = Assertions2.callObject(
            () -> RobotType.SCAN.getInitMethodInvoker().apply(controlCenter),
            context,
            r -> "The method `%s` threw an exception: %s".formatted(RobotType.SCAN.getInitMethodName(), r.cause().toString())
        );

        Assertions2.assertNotNull(robots, context, r -> "The method `%s` returned `null`.".formatted(RobotType.SCAN.getInitMethodName()));

        record Position(int x, int y) {}
        var positions = new HashSet<Position>();

        // Could be done in an easier way, but this gives more precise error messages
        for (int i = 0; i < robots.length; i++) {
            var robot = robots[i];
            var finalI = i;
            Assertions2.assertNotNull(robot, context, r -> "The robot at index %d is `null`.".formatted(finalI));
            Assertions2.assertFalse(
                positions.contains(new Position(robot.getX(), robot.getY())),
                context,
                r -> "The robot at index %d has the same position as another robot.".formatted(finalI)
            );

            positions.add(new Position(robot.getX(), robot.getY()));
        }
    }
}
