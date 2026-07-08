package simulator.stochastic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PiecewiseConstantArrivalRateTest {
    @Test
    void defaultScheduleHasMorningRushRate() {
        PiecewiseConstantArrivalRate rate = PiecewiseConstantArrivalRate.defaultDealershipDay();
        assertEquals(8.0, rate.rateAt(0.5));
    }

    @Test
    void integratedRateSumsSegmentContributions() {
        PiecewiseConstantArrivalRate rate =
                new PiecewiseConstantArrivalRate(new double[] {0.0, 2.0}, new double[] {4.0, 2.0});

        assertEquals(10.0, rate.integratedRate(0.0, 3.0), 1e-9);
    }

    @Test
    void integratedRateIsZeroWhenIntervalIsEmpty() {
        PiecewiseConstantArrivalRate rate = PiecewiseConstantArrivalRate.defaultDealershipDay();
        assertEquals(0.0, rate.integratedRate(5.0, 5.0));
    }

    @Test
    void rejectsEmptySegments() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PiecewiseConstantArrivalRate(new double[] {}, new double[] {}));
    }

    @Test
    void rejectsMismatchedSegmentLengths() {
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        new PiecewiseConstantArrivalRate(
                                new double[] {0.0, 1.0}, new double[] {3.0}));
    }
}
