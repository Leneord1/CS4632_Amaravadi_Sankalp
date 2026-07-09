package simulator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import simulator.config.SimulationConfig;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

class MainTest {

    @Test
    void runExecutesEngineForSmallConfig(@TempDir Path outputDir) throws IOException {
        SimulationConfig config =
                SimulationConfig.builder()
                        .simulationHorizonHours(2.0)
                        .customerCount(3)
                        .randomSeed(5L)
                        .build();
        Main.run(config, outputDir);
    }

    @Test
    void mainHelpArgumentReturnsWithoutError() {
        Main.main(new String[] {"--help"});
    }

    @Test
    void mainRethrowsNonUsageIllegalArgument() {
        assertThrows(
                IllegalArgumentException.class, () -> Main.main(new String[] {"--technicians=0"}));
    }

    @Test
    void mainPromptsThenRunsSimulation(@TempDir Path outputDir) {
        InputStream originalIn = System.in;
        // technicians: invalid -> below-min -> valid; advisors: empty (default); customers: valid.
        String prompts = "x\n0\n2\n\n3\n";
        System.setIn(new ByteArrayInputStream(prompts.getBytes(StandardCharsets.UTF_8)));
        try {
            Main.main(new String[] {"--horizon=2", "--output=" + outputDir});
        } finally {
            System.setIn(originalIn);
        }
    }
}
