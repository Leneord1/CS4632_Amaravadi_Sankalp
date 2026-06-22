package simulator.stochastic;

import java.util.Random;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GammaDistributionTest {
    @Test
    void sampleUsesSeededRandom() {
        double sample = GammaDistribution.sample(new Random(17), 4.0, 0.5);
        assertEquals(2.1469413087453972, sample, 1e-12);
    }

    @Test
    void technicianSampleAnchorsMeanToEquationNine() {
        Random random = new Random(17);
        double total = 0.0;
        int replications = 5000;
        for (int i = 0; i < replications; i++) {
            total += GammaDistribution.sampleForTechnician(random, 2.0, 1.0, 1.0, 4.0);
        }

        assertEquals(1.0, total / replications, 0.05);
    }

    @Test
    void sampleIsPositive() {
        assertTrue(GammaDistribution.sample(new Random(3), 2.0, 1.0) > 0.0);
    }
}
