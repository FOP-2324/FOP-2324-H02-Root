package h02;

import fopbot.World;
import org.junit.jupiter.api.BeforeEach;

public interface IWorldSetup {

    @BeforeEach
    default void setup() {
        World.setDelay(0);
        World.getGlobalWorld().setActionLimit(TestUtils.ACTION_LIMIT);
    }
}
