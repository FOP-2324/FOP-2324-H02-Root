package h02;

import fopbot.World;
import org.junit.jupiter.api.BeforeEach;

/**
 * This interface is used to set up the world for the tests.
 */
public interface IWorldSetup {

    @BeforeEach
    default void setup() {
        World.setDelay(0);
        //noinspection UnstableApiUsage
        World.getGlobalWorld().setActionLimit(TestUtils.ACTION_LIMIT);
    }
}
