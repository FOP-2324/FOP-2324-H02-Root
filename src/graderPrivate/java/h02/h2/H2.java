package h02.h2;

import h02.IWorldSetup;
import h02.Main;
import h02.TestUtils;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

import java.util.Arrays;
import java.util.stream.IntStream;

@TestForSubmission
@Timeout(
    value = TestUtils.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
@SkipAfterFirstFailedTest(TestUtils.SKIP_AFTER_FIRST_FAILED_TEST)
public class H2 implements IWorldSetup {

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testCoinPositions(final TestUtils.WorldSize worldSize, final Context context) {
        testCoins(worldSize, context, true);
    }

    @ParameterizedTest
    @MethodSource("h02.TestUtils#allWorldSizes")
    public void testCoinAmounts(final TestUtils.WorldSize worldSize, final Context context) {
        testCoins(worldSize, context, false);
    }

    private void testCoins(final TestUtils.WorldSize worldSize, Context context, final boolean normalizeAmounts) {
        TestUtils.setWorldSizeAndActionLimit(worldSize.width(), worldSize.height());

        final var random = TestUtils.random(worldSize);

        final var coins = IntStream.range(1, worldSize.height()).sequential()
            .mapToObj(x -> random.ints(worldSize.width(), 0, 5).toArray())
            .toArray(int[][]::new);

        context = Assertions2.contextBuilder()
            .add(context)
            .add("coins", stringifyCoinGrid(coins))
            .build();

        Assertions2.call(
            () -> Main.placeCoinsInWorld(coins),
            context,
            r -> "The method `placeCoinsInWorld` threw an exception: %s".formatted(r.cause().toString())
        );

        for (int y = 1; y < coins.length; y++) {
            for (int x = 1; x < coins[y].length; x++) {
                final var finalY = y;
                final var finalX = x;

                Assertions2.assertEquals(
                    normalizeAmounts ? normalizeAmount(coins[y][x]) : coins[y][x],
                    normalizeAmounts ? normalizeAmount(TestUtils.getCoinsOnField(x, y)) : TestUtils.getCoinsOnField(x, y),
                    context,
                    r -> "The coin amount at position (%d, %d) should be %d.".formatted(finalX, finalX, coins[finalY][finalX])
                );
            }
        }
    }

    private int normalizeAmount(final int amount) {
        return amount > 0 ? 1 : 0;
    }

    /**
     * Generates a string representing the given coins array in a grid. It is assumed that the array is in column major
     * order and that the origin is in the bottom left corner.
     *
     * @param coins The coins array in column major order
     * @return A string representing the coins array
     */
    private String stringifyCoinGrid(final int[][] coins) {
        return Arrays.deepToString(coins);
    }
}
