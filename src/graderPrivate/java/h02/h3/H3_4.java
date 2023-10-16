package h02.h3;

import fopbot.Robot;
import h02.ControlCenter;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.rubric.TestForSubmission;
import org.tudalgo.algoutils.tutor.general.assertions.Assertions2;

import static org.mockito.Mockito.*;


@TestForSubmission
public class H3_4 {

    @Test
    public void testSpinRobots() {
        var controlCenter = mock(ControlCenter.class);
        doCallRealMethod().when(controlCenter).spinRobots(any());

        var dummyArray = new Robot[0];
        Assertions2.call(
            () -> controlCenter.spinRobots(dummyArray),
            Assertions2.emptyContext(),
            r -> "The method `spinRobots` threw an exception: %s".formatted(r.cause().toString())
        );

        var inOrder = inOrder(controlCenter);
        inOrder.verify(controlCenter).reverseRobots(same(dummyArray));
        inOrder.verify(controlCenter).rotateRobots(same(dummyArray));
        inOrder.verify(controlCenter).replaceBrokenRobots(same(dummyArray));
        inOrder.verifyNoMoreInteractions();
    }
}
