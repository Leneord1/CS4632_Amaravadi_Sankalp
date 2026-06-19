package simulator.stochastic;

import java.util.Random;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoissonDistributionTest {
    @Test
    void stationaryProbabilityMatchesEquationFiveForZeroArrivals() {
        double probability = PoissonDistribution.probabilityExactlyNArrivals(0, 4.0, 0.5);
        assertEquals(Math.exp(-2.0), probability, 1e-9);
    }

    @Test
    void stationaryProbabilityMatchesEquationFiveForOneArrival() {
        double probability = PoissonDistribution.probabilityExactlyNArrivals(1, 4.0, 0.5);
        assertEquals(2.0 * Math.exp(-2.0), probability, 1e-9);
    }

    @Test
    void integratedIntensityProbabilityMatchesNonStationaryForm() {
        double probability = PoissonDistribution.probabilityExactlyNArrivals(2, 3.0);
        assertEquals(4.5 * Math.exp(-3.0), probability, 1e-9);
    }

    @Test
    void zeroArrivalsHasProbabilityOneWhenIntensityIsZero() {
        assertEquals(1.0, PoissonDistribution.probabilityExactlyNArrivals(0, 0.0), 1e-9);
    }

    @Test
    void samplePoissonUsesSeededRandom() {
        assertEquals(6, PoissonDistribution.samplePoisson(new Random(21), 5.0));
    }

    @Test
    void sampleExponentialInterArrivalUsesSeededRandom() {
        assertEquals(
                0.658296818935262,
                PoissonDistribution.sampleExponentialInterArrival(new Random(21), 2.0),
                1e-12);
    }

    @Test
    void rejectsNegativeArrivalCount() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PoissonDistribution.probabilityExactlyNArrivals(-1, 2.0, 1.0));
    }

    @Test
    void rejectsNegativeLambda() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PoissonDistribution.probabilityExactlyNArrivals(1, -1.0, 1.0));
    }

    @Test
    void rejectsNegativeTime() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PoissonDistribution.probabilityExactlyNArrivals(1, 2.0, -0.1));
    }

    @Test
    void rejectsNegativeIntegratedIntensity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PoissonDistribution.probabilityExactlyNArrivals(1, -0.5));
    }
}
