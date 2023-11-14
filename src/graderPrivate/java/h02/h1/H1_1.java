package h02.h1;

import h02.TestUtils;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.annotation.SkipAfterFirstFailedTest;
import org.tudalgo.algoutils.tutor.general.assertions.Context;

@TestForSubmission
@Timeout(
    value = TestUtils.TEST_TIMEOUT_IN_SECONDS,
    threadMode = Timeout.ThreadMode.SEPARATE_THREAD
)
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
        testNoRobotsWithDuplicatePositions(worldSize, context, RobotType.SCAN);
    }
}
