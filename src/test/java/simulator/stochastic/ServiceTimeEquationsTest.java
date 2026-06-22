package simulator.stochastic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceTimeEquationsTest {
    @Test
    void experienceScalingMatchesEquationSix() {
        assertEquals(0.5, ServiceTimeEquations.experienceScalingFactor(1.0, 1.0), 1e-9);
    }

    @Test
    void effectiveMeanMatchesEquationSeven() {
        assertEquals(1.0, ServiceTimeEquations.effectiveMeanServiceTime(2.0, 1.0, 1.0), 1e-9);
    }

    @Test
    void gammaScaleMatchesEquationEight() {
        assertEquals(0.5, ServiceTimeEquations.gammaScaleParameter(2.0, 4.0), 1e-9);
    }

    @Test
    void gammaMeanMatchesEquationNine() {
        assertEquals(2.0, ServiceTimeEquations.gammaMean(0.5, 4.0), 1e-9);
    }

    @Test
    void gammaVarianceMatchesEquationTen() {
        assertEquals(1.0, ServiceTimeEquations.gammaVariance(2.0, 4.0), 1e-9);
    }

    @Test
    void normalizeExperienceLevelCapsAtOne() {
        assertEquals(1.0, ServiceTimeEquations.normalizeExperienceLevel(12, 10), 1e-9);
    }

    @Test
    void rejectsInvalidExperienceLevel() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ServiceTimeEquations.experienceScalingFactor(1.5, 1.0));
    }
}
