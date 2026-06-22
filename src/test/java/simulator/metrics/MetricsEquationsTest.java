package simulator.metrics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MetricsEquationsTest {
    @Test
    void systemUtilizationMatchesEquationOne() {
        assertEquals(0.667, MetricsEquations.systemUtilization(4.0, 3, 2.0), 1e-3);
    }

    @Test
    void erlangCProbabilityMatchesEquationTwo() {
        assertEquals(4.0 / 9.0, MetricsEquations.erlangCProbabilityOfWaiting(4.0, 3, 2.0), 1e-3);
    }

    @Test
    void expectedQueueWaitMatchesEquationFour() {
        assertEquals(2.0 / 9.0, MetricsEquations.expectedQueueWait(4.0, 3, 2.0), 1e-3);
    }

    @Test
    void totalCustomerWaitMatchesEquationTwelve() {
        assertEquals(6.5, MetricsEquations.totalCustomerWaitTime(1.0, 2.0, 1.5, 2.0), 1e-9);
    }

    @Test
    void totalJobDelayMatchesEquationEleven() {
        assertEquals(3.5, MetricsEquations.totalJobDelay(2.0, 1.5), 1e-9);
    }

    @Test
    void shopResourceUtilizationAggregatesBusyTime() {
        assertEquals(0.5, MetricsEquations.shopResourceUtilization(15.0, 3, 10.0), 1e-9);
    }
}
