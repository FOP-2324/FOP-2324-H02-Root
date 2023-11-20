package h02;

import fopbot.Coin;
import fopbot.World;
import org.junit.jupiter.params.provider.Arguments;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {

    public static final int TEST_TIMEOUT_IN_SECONDS = 5;
    // Running the solution shows no action count higher than 560
    public static final int ACTION_LIMIT = 700;
    public static final boolean SKIP_AFTER_FIRST_FAILED_TEST = true;
    private static final long THE_SEED = 1234L;

    /**
     * A "random" hand selected batch of varying world sizes for all tests.
     */
    private static final List<Arguments> WORLD_SIZE_ARGUMENTS = Stream.of(
            new WorldSize(1, 1),
            new WorldSize(2, 1),
            new WorldSize(1, 2),
            new WorldSize(1, 4),
            new WorldSize(3, 5),
            new WorldSize(4, 4),
            new WorldSize(2, 7),
            new WorldSize(6, 6),
            new WorldSize(9, 8),
            new WorldSize(10, 11),
            new WorldSize(11, 10),
            new WorldSize(15, 13)
        )
        .map(size -> {
            var context = Assertions2.contextBuilder()
                .add("worldWidth", size.width())
                .add("worldHeight", size.height())
                .build();

            return Arguments.of(size, context);
        })
        .collect(Collectors.collectingAndThen(
            Collectors.toCollection(ArrayList::new),
            (l) -> {
                // Prevent students from predicting the order of the tests
                var random = random();
                Collections.shuffle(l, random);
                return l;
            }
        ));

    public static Random random(final Object... magicValues) {
        return new Random(Objects.hash(THE_SEED, Arrays.hashCode(magicValues)));
    }

    public static void setWorldSizeAndActionLimit(final int width, final int height) {
        World.setSize(width, height);
        //noinspection UnstableApiUsage
        World.getGlobalWorld().setActionLimit(ACTION_LIMIT);
    }

    public static Stream<Arguments> allWorldSizes() {
        return WORLD_SIZE_ARGUMENTS.stream();
    }

    /**
     * Returns the number of coins on a given field.
     *
     * @param x the x coordinate of the field
     * @param y the y coordinate of the field
     * @return the number of coins on the field
     */
    public static int getCoinsOnField(final int x, final int y) {
        return World.getGlobalWorld().getAllFieldEntities().stream()
            .filter(entity -> entity.getX() == x && entity.getY() == y)
            .filter(entity -> entity instanceof Coin)
            .map(entity -> (Coin) entity)
            .mapToInt(Coin::getCount)
            .sum();
    }

    public record WorldSize(int width, int height) {
    }
}
