package simulator;

import org.junit.jupiter.api.Test;
import simulator.config.SimulationConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    @Test
    void runExecutesEngineForSmallConfig() {
        SimulationConfig config = SimulationConfig.builder()
                .simulationHorizonHours(2.0)
                .customerCount(3)
                .randomSeed(5L)
                .build();
        Main.run(config);
    }

    @Test
    void mainHelpArgumentReturnsWithoutError() {
        Main.main(new String[] {"--help"});
    }

    @Test
    void mainRethrowsNonUsageIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> Main.main(new String[] {"--technicians=0"}));
    }

    @Test
    void mainPromptsThenRunsSimulation() {
        InputStream originalIn = System.in;
        // technicians: invalid -> below-min -> valid; advisors: empty (default); customers: valid.
        String prompts = "x\n0\n2\n\n3\n";
        System.setIn(new ByteArrayInputStream(prompts.getBytes(StandardCharsets.UTF_8)));
        try {
            Main.main(new String[] {"--horizon=2"});
        } finally {
            System.setIn(originalIn);
        }
    }
}
