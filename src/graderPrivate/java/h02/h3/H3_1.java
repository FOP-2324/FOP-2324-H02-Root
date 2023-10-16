package h02.h3;

import fopbot.Robot;
import h02.ControlCenter;
import h02.IWorldSetup;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;

import java.util.Arrays;

@TestForSubmission
public class H3_1 implements IWorldSetup {

    @Test
    public void testInvertRobotsArray() {
        var controlCenter = new ControlCenter();

        for (int i = 1; i <= 20; i++) {
            var robots = new Robot[i];
            for (int j = 0; j < robots.length; j++)
                robots[j] = new Robot(0, 0);

            var robotsReferenceCopy = Arrays.copyOf(robots, robots.length);

            Assertions2.call(
                () -> controlCenter.reverseRobots(robots),
                Assertions2.emptyContext(),
                r -> "The method `invertRobots` threw an exception: %s".formatted(r.cause().toString())
            );

            for (int j = 0; j < robots.length; j++) {
                var finalJ = j;

                Assertions2.assertSame(
                    robotsReferenceCopy[j],
                    robots[robots.length - j - 1],
                    Assertions2.emptyContext(),
                    r -> "The robot at index %d was not swapped correctly.".formatted(finalJ)
                );
            }
        }
    }
}
