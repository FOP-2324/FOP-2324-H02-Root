package h02.h4;

import com.fasterxml.jackson.databind.JsonNode;
import fopbot.Direction;
import fopbot.Robot;
import fopbot.World;
import h02.ControlCenter;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.tudalgo.algoutils.tutor.general.json.JsonConverters;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class H4Utils {

    static final Map<String, Function<JsonNode, ?>> CUSTOM_CONVERTERS = Map.ofEntries(
        Map.entry("worldWidth", JsonNode::asInt),
        Map.entry("worldHeight", JsonNode::asInt),
        Map.entry("direction", (n) -> Direction.valueOf(n.asText())),
        Map.entry(
            "coins",
            (list) -> verticalMirrorArray(to2dArray(list, int.class, JsonNode::asInt))
        ),
        Map.entry(
            "expected",
            (list) -> verticalMirrorArray(to2dArray(list, boolean.class, JsonNode::asBoolean))
        )
    );

    static Function<JsonNode, ?> robotConverter(final Class<? extends Robot> robotClass) {
        return (list) -> JsonConverters.toList(list, (node) -> {
            final var x = node.get("x").asInt();
            final var y = node.get("y").asInt();
            try {
                return robotClass.getConstructor(
                    int.class,
                    int.class,
                    Direction.class,
                    int.class
                ).newInstance(x, y, Direction.UP, 0);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }).toArray(i -> (Object[]) Array.newInstance(robotClass, i));
    }

    static void initRobotsAndWorld(final Robot[] robots, final Direction dir, final int[][] coins) {
        for (final Robot robot : robots) {
            while (robot.getDirection() != dir) {
                robot.turnLeft();
            }
        }

        for (int y = 0; y < coins.length; y++) {
            for (int x = 0; x < coins[y].length; x++) {
                if (coins[y][x] > 0) {
                    World.putCoins(x, y, coins[y][x]);
                }
            }
        }
    }

    /**
     * Calculates the final positions of the given robots after they have walked to the end of the world in the
     * direction they're facing.
     *
     * @param robots      The robots to calculate the final positions for
     * @param worldWidth  The width of the world
     * @param worldHeight The height of the world
     * @return The final positions of the robots
     */
    static Position[] getEndOfWorldRobotPositions(final Robot[] robots, final int worldWidth, final int worldHeight) {
        return Arrays.stream(robots)
            .map((robot) -> snapPositionToWorldEdge(
                new Position(robot.getX(), robot.getY()),
                robot.getDirection(), worldWidth, worldHeight
            )).toArray(Position[]::new);
    }

    static Position[] getEndOfWorldPositions(
        final Position[] positions,
        final Direction direction,
        final int worldWidth,
        final int worldHeight
    ) {
        return Arrays.stream(positions)
            .map((position) -> snapPositionToWorldEdge(
                position,
                direction, worldWidth, worldHeight
            )).toArray(Position[]::new);
    }

    static Position snapPositionToWorldEdge(
        final Position position,
        final Direction direction,
        final int worldWidth,
        final int worldHeight
    ) {
        final var x = switch (direction) {
            case UP, DOWN -> position.x();
            case LEFT -> 0;
            case RIGHT -> worldWidth - 1;
        };

        final var y = switch (direction) {
            case UP -> worldHeight - 1;
            case DOWN -> 0;
            case LEFT, RIGHT -> position.y();
        };

        return new Position(x, y);
    }

    /**
     * Converts the given JsonNode to a 2d array of the given type, using the given mapper to convert the individual
     * elements. All operations here are unchecked, so make sure to only use this method with the correct parameters
     * and assigning the result to the correct type.
     *
     * @param node             The node to convert
     * @param elementTypeClass The class of an element in the array (e.g. {@code int.class}
     * @param mapper           The mapper to convert the individual elements
     * @param <RT>             The type of the 2d array (e.g. {@code int[][]})
     * @param <ET>             The type of element in the array (e.g. {@code Integer}, will be unboxed)
     * @return The 2d array represented by the given node
     */
    @SuppressWarnings("unchecked")
    static <RT, ET> RT to2dArray(
        final JsonNode node,
        final Class<ET> elementTypeClass,
        final Function<JsonNode, ET> mapper
    ) {
        return (RT) JsonConverters.toList(
            node,
            (n) -> {
                final var array = Array.newInstance(elementTypeClass, n.size());
                for (int i = 0; i < n.size(); i++) {
                    Array.set(array, i, mapper.apply(n.get(i)));
                }
                return array;
            }
        ).toArray((n) -> (Object[]) Array.newInstance(elementTypeClass, n, 0));
    }

    /**
     * Transposes the given 2d array horizontally and then transposes it.
     *
     * @param array The array to mirror and transpose
     * @param <T>   The type of the array
     * @return The mirrored and transposed array
     */
    static <T> T verticalMirrorArray(final T array) {
        final var arrayLength = Array.getLength(array);
        for (int y = 0; y < arrayLength / 2; y++) {
            final var tmp = Array.get(array, y);
            Array.set(array, y, Array.get(array, arrayLength - y - 1));
            Array.set(array, arrayLength - y - 1, tmp);
        }

        return array;
    }

    /**
     * Replaces the method {@link ControlCenter#spinRobots(Robot[])} and {@link ControlCenter#returnRobots(Robot[])}
     * with solution implementations to not depend on the correctness of those methods.
     *
     * @return The mocked instance
     */
    static ControlCenter mockReturnAndSpinRobots() {
        final ControlCenter controlCenter = Mockito.mock(
            ControlCenter.class,
            InvocationOnMock::callRealMethod
        );
        Mockito.doAnswer((i) -> {
            final Robot[] robots = i.getArgument(0);
            for (final Robot robot : robots) {
                robot.turnLeft();
                robot.turnLeft();
            }

            return null;
        }).when(controlCenter).spinRobots(Mockito.any());

        Mockito.doAnswer((i) -> {
            final Robot[] robots = i.getArgument(0);
            for (final Robot robot : robots) {
                while (robot.isFrontClear()) {
                    robot.move();
                }
            }

            return null;
        }).when(controlCenter).returnRobots(Mockito.any());
        return controlCenter;
    }

    record Position(int x, int y) {

        public int manhattanDistance(final Position other) {
            return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
        }
    }
}
