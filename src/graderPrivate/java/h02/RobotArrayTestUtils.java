package h02;

import fopbot.Direction;
import fopbot.Robot;
import org.apache.commons.lang3.tuple.Pair;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.lang.reflect.Array;
import java.util.Random;

public class RobotArrayTestUtils {

    private static final int MIN_ROBOTS = 5;
    private static final int MAX_ROBOTS = 30;

    public static final RobotArrayElementVerifier ROBOT_XY_COINS_VERIFIER = (
        (context, newRobot, referenceCopy, copy) -> {
            Assertions2.assertEquals(
                copy.getX(),
                newRobot.getX(),
                context,
                r -> "The method changed the x coordinate of a robot."
            );
            Assertions2.assertEquals(
                copy.getY(),
                newRobot.getY(),
                context,
                r -> "The method changed the y coordinate of a robot."
            );
            Assertions2.assertEquals(
                copy.getNumberOfCoins(),
                newRobot.getNumberOfCoins(),
                context,
                r -> "The method changed the coin amount of a robot."
            );
        }
    );

    public static final RobotArrayElementVerifier ROBOT_DIRECTION_VERIFIER = (
        (context, newRobot, referenceCopy, copy) -> {
            Assertions2.assertEquals(
                copy.getDirection(),
                newRobot.getDirection(),
                context,
                r -> "The method changed the direction of a robot."
            );
        }
    );

    public static final RobotArrayElementVerifier IMMUTABLE_ARRAY_VERIFIER = (
        (context, newRobot, referenceCopy, copy) -> {
            Assertions2.assertSame(referenceCopy, newRobot, context, r -> "The method modified the array.");
        }
    );

    /**
     * Asserts that the robot array was modified correctly.
     *
     * @param context             The context to use for assertions.
     * @param robots              The robot array to check.
     * @param robotsReferenceCopy The reference copy of the robot array.
     * @param robotsCopy          The copy of the robot array.
     * @param verifier            The verifier to use for each robot.
     */
    public static void assertRobotArrayWasModifiedCorrectly(
        final Context context,
        final Robot[] robots,
        final Robot[] robotsReferenceCopy,
        final Robot[] robotsCopy,
        final RobotArrayElementVerifier verifier
    ) {
        for (int i = 0; i < robots.length; i++) {
            final var iterationContext = Assertions2.contextBuilder()
                .add(context)
                .add("robotIndex", i)
                .build();
            Assertions2.assertNotNull(robots[i], iterationContext, r -> "The method set an element to null.");

            verifier.verify(iterationContext, robots[i], robotsReferenceCopy[i], robotsCopy[i]);
        }
    }

    /**
     * Generates a robot array with a random size and random positions.
     *
     * @param robotClass The class of the robots to generate.
     * @param random     The random instance to use.
     * @param worldSize  The world size to use.
     * @return A pair of the generated robot array and a copy of it.
     */
    public static Pair<Robot[], Robot[]> generateRobotArray(
        final Class<? extends Robot> robotClass,
        final Random random,
        final TestUtils.WorldSize worldSize
    ) {
        final var robots = (Robot[]) Array.newInstance(
            robotClass,
            Math.min(worldSize.width() * worldSize.height(), random.nextInt(MIN_ROBOTS, MAX_ROBOTS + 1))
        );
        final var robotsCopy = (Robot[]) Array.newInstance(robotClass, robots.length);
        for (int i = 0; i < robots.length; i++) {
            robots[i] = Assertions2.callObject(
                () -> robotClass
                    .getConstructor(int.class, int.class, Direction.class, int.class)
                    .newInstance(
                        random.nextInt(worldSize.width()), random.nextInt(worldSize.height()),
                        Direction.UP,
                        0
                    ),
                Assertions2.emptyContext(),
                r -> "Instantiating a robot threw an exception: %s".formatted(r.cause().toString())
            );

            final var finalI = i;
            robotsCopy[i] = Assertions2.callObject(
                () -> robotClass
                    .getConstructor(int.class, int.class, Direction.class, int.class)
                    .newInstance(
                        robots[finalI].getX(), robots[finalI].getY(),
                        Direction.UP,
                        0
                    ),
                Assertions2.emptyContext(),
                r -> "Instantiating a robot threw an exception: %s".formatted(r.cause().toString())
            );
        }
        return Pair.of(robots, robotsCopy);
    }

    /**
     * A verifier for a single robot array element.
     */
    @FunctionalInterface
    public interface RobotArrayElementVerifier {

        void verify(Context context, Robot newRobot, Robot referenceCopy, Robot copy);

        default RobotArrayElementVerifier and(final RobotArrayElementVerifier other) {
            return (context, newRobot, referenceCopy, copy) -> {
                verify(context, newRobot, referenceCopy, copy);
                other.verify(context, newRobot, referenceCopy, copy);
            };
        }
    }
}
